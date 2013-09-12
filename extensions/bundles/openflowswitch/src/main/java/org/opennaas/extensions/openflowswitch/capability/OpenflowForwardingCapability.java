package org.opennaas.extensions.openflowswitch.capability;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.openflowswitch.model.OFForwardingRule;
import org.opennaas.extensions.openflowswitch.repository.Activator;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

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

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void createOpenflowForwardingRule(OFForwardingRule forwardingRule) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeOpenflowForwardingRule(String flowId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<OFForwardingRule> getOpenflowForwardingRules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);

	}

	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
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
}
