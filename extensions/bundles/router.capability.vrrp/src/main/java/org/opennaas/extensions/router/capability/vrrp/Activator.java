package org.opennaas.extensions.router.capability.vrrp;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @author Julio Carlos Barrera
 */
public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	public static BundleContext getContext() {
		return context;
	}

	public static IActionSet getVRRPActionSetService(String capabilityName, String actionsetName, String actionsetVersion) throws ActivatorException {
		try {
			log.debug("Calling ActionSetService for capability " + capabilityName);
			return (IActionSet) getServiceFromRegistry(context, createFilterVRRPActionSet(capabilityName, actionsetName, actionsetVersion));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	private static Filter createFilterVRRPActionSet(String capabilityName, String actionsetName, String actionsetVersion) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, capabilityName);
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, actionsetName);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, actionsetVersion);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

	public static IQueueManagerCapability getQueueManagerService(String resourceId) throws ActivatorException {
		try {
			log.debug("Calling QueueManagerService");
			return (IQueueManagerCapability) getServiceFromRegistry(context, createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	protected static Filter createFilterQueueManager(String resourceId) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		return createServiceFilter(IQueueManagerCapability.class.getName(), properties);
	}
}
