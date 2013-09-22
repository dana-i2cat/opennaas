package org.opennaas.extensions.openflowswitch.capability;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
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
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.opennaas.extensions.openflowswitch.repository.Activator;

/**
 * 
 * @author Adrian Rosello (i2CAT)
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

		IAction action = createActionAndCheckParams(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE, forwardingRule);

		try {
			ActionResponse response = executeAction(action);

			if (!response.getStatus().equals(ActionResponse.STATUS.OK))
				throw new ActionException(response.getResponses().get(0).toString());

		} catch (ProtocolException pe) {
			log.error("Error getting OFswitch protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActivatorException ae) {
			log.error("Protocol error - " + ae.getMessage());
			throw new CapabilityException();
		} catch (ActionException ae) {
			log.error("Error executing " + action.getActionID() + " action - " + ae.getMessage());
			throw (ae);
		}

		log.info("End of createOpenflowForwardingRule call");

	}

	@Override
	public void removeOpenflowForwardingRule(String flowId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<FloodlightOFFlow> getOpenflowForwardingRules() throws CapabilityException {
		log.info("Start of getOpenflowForwardingRules call");

		List<FloodlightOFFlow> forwardingRules = new ArrayList<FloodlightOFFlow>();
		try {

			OpenflowSwitchModel model = getResourceModel();
			log.debug("Reading forwarding rules from model.");
			forwardingRules = OpenflowSwitchModelHelper.getSwitchForwardingRules(model);

		} catch (ActivatorException ae) {
			log.error("Error getting resource model - " + ae.getMessage());
			throw new CapabilityException(ae);
		} catch (ResourceException re) {
			log.error("Error getting resource model - " + re.getMessage());
			throw new CapabilityException(re);
		}

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

	private IResourceManager getResourceManager() throws ActivatorException {
		return Activator.getResourceManagerService();
	}

	private OpenflowSwitchModel getResourceModel() throws ActivatorException, ResourceException {

		log.debug("Getting resource model.");

		IResourceManager resourceManager = getResourceManager();

		IResource switchResource = resourceManager.getResourceById(this.resourceId);

		return (OpenflowSwitchModel) switchResource.getModel();
	}

	private ActionResponse executeAction(IAction action) throws ProtocolException, ActionException, ActivatorException {

		IProtocolManager protocolManager = Activator.getProtocolManagerService();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

		ActionResponse response = action.execute(protocolSessionManager);

		return response;
	}

}
