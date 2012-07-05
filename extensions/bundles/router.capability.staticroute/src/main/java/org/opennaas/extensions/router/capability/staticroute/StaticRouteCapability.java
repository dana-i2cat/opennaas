package org.opennaas.extensions.router.capability.staticroute;

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

/**
 * @author Jordi Puig
 */
public class StaticRouteCapability extends AbstractCapability implements IStaticRouteCapability {

	public static String	CAPABILITY_TYPE	= "staticroute";

	Log						log				= LogFactory.getLog(StaticRouteCapability.class);

	private String			resourceId		= "";

	/**
	 * StaticRouteCapability constructor
	 * 
	 * @param descriptor
	 * @param resourceId
	 */
	public StaticRouteCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new StaticRoute Capability");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#activate()
	 */
	@Override
	public void activate() throws CapabilityException {
		// registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceName(), IStaticRouteCapability.class.getName());
		super.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.AbstractCapability#deactivate()
	 */
	@Override
	public void deactivate() throws CapabilityException {
		// registration.unregister();
		super.deactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.capability.ICapability#getCapabilityName()
	 */
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
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
	 * @return QueuemanagerService this capability is associated to.
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

	/**
	 * Return the Static Route ActionSet
	 */
	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);
		try {
			return Activator.getStaticRouteActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	/*
	 * IStaticRoute Implementation
	 */

	@Override
	public void createStaticRoute(String netIdIpAdress, String maskIpAdress, String nextHopIpAddress) throws CapabilityException {
		log.info("Start of createStaticRoute call");
		String[] aParams = new String[3];
		aParams[0] = netIdIpAdress;
		aParams[1] = maskIpAdress;
		aParams[2] = nextHopIpAddress;

		IAction action = createActionAndCheckParams(StaticRouteActionSet.STATIC_ROUTE_CREATE, aParams);
		queueAction(action);
		log.info("End of createStaticRoute call");
	}
}
