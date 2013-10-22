package org.opennaas.extensions.openflowswitch.capability;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
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
import org.opennaas.extensions.openflowswitch.helpers.OpenflowSwitchModelHelper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.opennaas.extensions.openflowswitch.repository.Activator;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class OpenflowForwardingCapability extends AbstractCapability implements IOpenflowForwardingCapability {

	public static String	CAPABILITY_TYPE	= "offorwarding";

	Log						log				= LogFactory.getLog(OpenflowForwardingCapability.class);

	private String			resourceId		= "";

	public OpenflowForwardingCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Openflow Forwarding Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.caactivatepability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IOpenflowForwardingCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void createOpenflowForwardingRule(FloodlightOFFlow forwardingRule) throws CapabilityException {

		log.info("Start of createOpenflowForwardingRule call");
		log.info("Creating forwarding rule " + forwardingRule.getName() + " in resource " + resource.getResourceIdentifier().getId());

		IAction action = createActionAndCheckParams(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE, forwardingRule);

		ActionResponse response = executeAction(action);

		refreshModelFlows();

		log.info("End of createOpenflowForwardingRule call");
	}

	@Override
	public void removeOpenflowForwardingRule(String flowId) throws CapabilityException {

		log.info("Start of removeOpenflowForwardingRule call");
		log.info("Removing forwarding rule " + flowId + " in resource " + resource.getResourceIdentifier().getId());

		IAction action = createActionAndCheckParams(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE, flowId);

		ActionResponse response = executeAction(action);

		refreshModelFlows();

		log.info("End of removeOpenflowForwardingRule call");
	}

	@Override
	public List<FloodlightOFFlow> getOpenflowForwardingRules() throws CapabilityException {
		log.info("Start of getOpenflowForwardingRules call");

		List<FloodlightOFFlow> forwardingRules;

		refreshModelFlows();

		log.debug("Reading forwarding rules from model.");
		forwardingRules = OpenflowSwitchModelHelper.getSwitchForwardingRules(getResourceModel());

		log.info("End of getOpenflowForwardingRules call");
		return forwardingRules;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {

		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getOpenflowForwardingActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);

		}
	}

	/**
	 * This method retrieves flows in the switch by calling GETFLOWS action and sets them in the switch model.
	 * 
	 * @return retrieved flows
	 * @throws CapabilityException
	 */
	private List<FloodlightOFFlow> refreshModelFlows() throws CapabilityException {
		log.info("Start of refreshModelFlows call");

		IAction action = createActionAndCheckParams(OpenflowForwardingActionSet.GETFLOWS, null);

		ActionResponse response = executeAction(action);

		// assuming the action returns what it is meant to
		List<FloodlightOFFlow> currentFlows = (List<FloodlightOFFlow>) response.getResult();

		// assuming only one table may exist in switch model
		// thats true with OpenFlow version 1.0
		if (getResourceModel().getOfTables().isEmpty())
			getResourceModel().getOfTables().add(new OFFlowTable());

		getResourceModel().getOfTables().get(0).setOfForwardingRules(currentFlows);

		log.info("End of refreshModelFlows call");

		return currentFlows;
	}

	private OpenflowSwitchModel getResourceModel() {
		return (OpenflowSwitchModel) resource.getModel();
	}

	private ActionResponse executeAction(IAction action) throws CapabilityException {
		ActionResponse response;
		try {
			IProtocolManager protocolManager = Activator.getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			response = action.execute(protocolSessionManager);

		} catch (ProtocolException pe) {
			log.error("Error with protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		} catch (ActionException ae) {
			log.error("Error executing " + action.getActionID() + " action - " + ae.getMessage());
			throw (ae);
		}

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			String errMsg = "Error executing " + action.getActionID() + " action - " + response.getInformation();
			log.error(errMsg);
			throw new ActionException(errMsg);
		}
		return response;
	}

}
