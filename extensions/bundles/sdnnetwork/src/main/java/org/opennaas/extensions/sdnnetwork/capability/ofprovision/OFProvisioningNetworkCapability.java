package org.opennaas.extensions.sdnnetwork.capability.ofprovision;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.sdnnetwork.Activator;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModel;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModelHelper;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class OFProvisioningNetworkCapability extends AbstractCapability implements IOFProvisioningNetworkCapability {

	public static String	CAPABILITY_TYPE	= "ofprovisionnet";

	Log						log				= LogFactory.getLog(OFProvisioningNetworkCapabilityFactory.class);

	private String			resourceId		= "";

	public OFProvisioningNetworkCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Openflow Provisioning Network Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getActionSetService(OFProvisioningNetworkCapability.CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public String allocateOFFlow(SDNNetworkOFFlow flowWithRoute)
			throws CapabilityException {

		log.info("Start of allocateOFFlow call");

		SDNNetworkOFFlow flowCopy = new SDNNetworkOFFlow(flowWithRoute);
		flowCopy = generateFlowAndRouteIds(flowCopy);

		IAction action = createActionAndCheckParams(OFProvisioningNetworkActionSet.ALLOCATEFLOW, flowCopy);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		// assuming allocated flow has not been modified by underlying functionality.
		// FIXME replace flowCopy with flow obtained from response.
		addAllocatedFlowToModel(flowCopy, (SDNNetworkModel) resource.getModel());

		log.info("End of allocateOFFlow call");

		return flowCopy.getName();
	}

	@Override
	public void deallocateOFFlow(String flowId) throws CapabilityException {
		log.info("Start of deallocateOFFlow call");

		// check flow exists in model
		SDNNetworkOFFlow flow;
		try {
			flow = SDNNetworkModelHelper.getFlowFromModelByName(flowId, (SDNNetworkModel) resource.getModel());
		} catch (ModelElementNotFoundException e) {
			throw new CapabilityException(e);
		}

		IAction action = createActionAndCheckParams(OFProvisioningNetworkActionSet.DEALLOCATEFLOW, flowId);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		removeDeallocatedFlowFromModel(flow, (SDNNetworkModel) resource.getModel());

		log.info("End of deallocateOFFlow call");
	}

	@Override
	public Collection<SDNNetworkOFFlow> getAllocatedFlows()
			throws CapabilityException {

		log.info("Start of getAllocatedFlows call");

		IAction action = createActionAndCheckParams(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS, null);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		Collection<SDNNetworkOFFlow> result;
		if (response.getResult() != null && response.getResult() instanceof Collection<?>) {
			result = (Collection<SDNNetworkOFFlow>) response.getResult();
		} else {
			throw new CapabilityException("Failed to retrieve result from action response of action " + action.getActionID());
		}

		log.info("End of getAllocatedFlows call");
		return result;
	}

	@Override
	public String updateAllocatedOFFlow(String flowId,
			SDNNetworkOFFlow flowWithRoute) throws CapabilityException {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void mapDeviceResource(String deviceId, String resourceID) throws CapabilityException {
		((SDNNetworkModel) resource.getModel()).getDeviceResourceMap().put(deviceId, resourceID);
	}

	@Override
	public void clearMap() {
		((SDNNetworkModel) resource.getModel()).getDeviceResourceMap().clear();
	}

	private ActionResponse executeAction(IAction action) throws CapabilityException {

		try {
			IProtocolManager protocolManager = getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			ActionResponse response = action.execute(protocolSessionManager);
			return response;

		} catch (ProtocolException pe) {
			log.error("Error getting protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		}
	}

	private IProtocolManager getProtocolManagerService() throws ActivatorException {
		return Activator.getProtocolManagerService();
	}

	private String generateRandomFlowId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Updates given flow with a name and all its NetworkConnections with an different id.
	 * 
	 * @param flow
	 * @return flow with name set to its id and with network connections in given flow route having an id.
	 */
	private SDNNetworkOFFlow generateFlowAndRouteIds(SDNNetworkOFFlow flow) {

		String flowId = generateRandomFlowId();
		flow.setName(flowId);

		if (flow.getRoute() != null && flow.getRoute().getNetworkConnections() != null) {
			for (NetworkConnection connection : flow.getRoute().getNetworkConnections()) {
				connection.setId(generateRandomFlowId());
			}
		}
		return flow;
	}

	private void addAllocatedFlowToModel(SDNNetworkOFFlow flow, SDNNetworkModel model) {
		model.getFlows().add(flow);
	}

	private void removeDeallocatedFlowFromModel(SDNNetworkOFFlow flow, SDNNetworkModel model) {
		model.getFlows().remove(flow);
	}

}
