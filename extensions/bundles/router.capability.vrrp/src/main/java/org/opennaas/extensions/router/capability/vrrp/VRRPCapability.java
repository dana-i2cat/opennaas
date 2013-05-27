package org.opennaas.extensions.router.capability.vrrp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;

/**
 * @author Julio Carlos Barrera
 */
public class VRRPCapability extends AbstractCapability implements IVRRPCapability {

	public static final String	CAPABILITY_TYPE	= "vrrp";
	Log							log				= LogFactory.getLog(VRRPCapability.class);
	private String				resourceId		= "";

	public VRRPCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) {
		super(capabilityDescriptor);
		this.resourceId = resourceId;
		log.debug("Built new VRRP Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.caactivatepability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IVRRPCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String actionSetName = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String actionSetVersion = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);
		try {
			return Activator.getVRRPActionSetService(getCapabilityName(), actionSetName, actionSetVersion);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#queueAction(org.opennaas.core.resources.action.IAction)
	 */
	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to (the one of the resource it belongs to).
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	@Override
	public void configureVRRP(VRRPProtocolEndpoint vrrpProtocolEndpoint)
			throws CapabilityException {
		log.info("Start of configureVRRP call");
		IAction action = createActionAndCheckParams(VRRPActionSet.VRRP_CONFIGURE, vrrpProtocolEndpoint);
		queueAction(action);
		log.info("End of configureVRRP call");
	}

	@Override
	public void unconfigureVRRP(VRRPProtocolEndpoint vrrpProtocolEndpoint)
			throws CapabilityException {
		log.info("Start of unconfigureVRRP call");
		IAction action = createActionAndCheckParams(VRRPActionSet.VRRP_UNCONFIGURE, vrrpProtocolEndpoint);
		queueAction(action);
		log.info("End of unconfigureVRRP call");
	}

	@Override
	public void updateVRRPVirtualIPAddress(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException {
		log.info("Start of updateVRRPVirtualIPAddress call");
		IAction action = createActionAndCheckParams(VRRPActionSet.VRRP_UPDATE_VIRTUAL_IP_ADDRESS, vrrpProtocolEndpoint);
		queueAction(action);
		log.info("End of updateVRRPVirtualIPAddress call");
	}

	@Override
	public void updateVRRPPriority(VRRPProtocolEndpoint vrrpProtocolEndpoint) throws CapabilityException {
		log.info("Start of updateVRRPPriority call");
		IAction action = createActionAndCheckParams(VRRPActionSet.VRRP_UPDATE_PRIORITY, vrrpProtocolEndpoint);
		queueAction(action);
		log.info("End of updateVRRPPriority call");
	}
}