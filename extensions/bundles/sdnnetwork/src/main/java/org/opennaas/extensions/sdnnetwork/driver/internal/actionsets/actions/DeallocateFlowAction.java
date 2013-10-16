package org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.actions;

import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.sdnnetwork.Activator;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModel;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkModelHelper;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class DeallocateFlowAction extends Action {

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params == null || !(params instanceof String))
			throw new ActionException("Invalid params for action " + OFProvisioningNetworkActionSet.DEALLOCATEFLOW);

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		String flowName = (String) params;
		SDNNetworkOFFlow flow;

		try {
			flow = SDNNetworkModelHelper.getFlowFromModelByName(flowName, (SDNNetworkModel) getModelToUpdate());
		} catch (ModelElementNotFoundException e) {
			throw new ActionException(e);
		}

		deallocateNetworkConnections(flow.getRoute().getNetworkConnections());

		return ActionResponse.okResponse(OFProvisioningNetworkActionSet.DEALLOCATEFLOW);
	}

	private void deallocateNetworkConnections(List<NetworkConnection> networkConnections) throws ActionException {
		// FIXME what to do if deallocation fails partially?
		// - should we reallocate already deallocated networkConnections?
		// - should we leave the flow allocated only partially?
		// The second option is applied by now: A fail in deallocateNetworkConnection(networkConnection) will cause this action to interrupt,
		// leaving the flow partially allocated and partially deallocated.
		// This is not consistent with the vision the capability has: the user will see the flow as completely allocated, but may be only partially
		// allocated.
		for (NetworkConnection networkConnection : networkConnections) {
			deallocateNetworkConnection(networkConnection);
		}
	}

	private void deallocateNetworkConnection(NetworkConnection networkConnection) throws ActionException {
		if (networkConnection.getSource().getDeviceId() != networkConnection.getDestination().getDeviceId()) {
			/* link between different devices, assume it is statically allocated/deallocated */
		} else {
			/* link inside same device, use device internal capability to deallocate it */
			deallocateConnectionInsideDevice(networkConnection);
		}
	}

	private void deallocateConnectionInsideDevice(NetworkConnection networkConnection) throws ActionException {
		// use device IOpenflowForwardingCapability capability to deallocate networkConnection
		try {
			IOpenflowForwardingCapability forwardingCapability = getForwardingCapabilityOfDevice(networkConnection.getSource().getDeviceId());
			forwardingCapability.removeOpenflowForwardingRule(networkConnection.getId());
		} catch (ResourceException e) {
			throw new ActionException("Failed to deallocate networkConnection " + networkConnection.getId(), e);
		}
	}

	private IOpenflowForwardingCapability getForwardingCapabilityOfDevice(String deviceId) throws ResourceException {
		String resourceName = deviceId;
		IResource resource = getResourceByName(resourceName);
		return (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
	}

	private IResource getResourceByName(String resourceName) throws ResourceException {
		try {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResourceIdentifier resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);
			return resourceManager.getResource(resourceId);
		} catch (ActivatorException e) {
			throw new ResourceException("Couldn't get resource. Failed to get ResourceManagerService", e);
		}
	}

}
