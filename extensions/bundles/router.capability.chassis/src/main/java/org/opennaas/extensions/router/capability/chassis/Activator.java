package org.opennaas.extensions.router.capability.chassis;

import java.util.Properties;

import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class Activator extends AbstractActivator implements BundleActivator {
	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	// public static IResourceManager getResourceManagerService() throws Exception {
	// return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	// }

	public static IQueueManagerCapability getQueueManagerService(String resourceId) throws ActivatorException {
		try {
			log.debug("Calling QueueManagerService");
			return (IQueueManagerCapability) getServiceFromRegistry(context, createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	protected static Filter createFilterQueueManager(String resourceId) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		return createServiceFilter(IQueueManagerCapability.class.getName(), properties);
	}

	public static IActionSet getChassisActionSetService(String name, String version) throws ActivatorException {
		try {
			log.debug("Calling ChassisActionSetService");
			return (IActionSet) getServiceFromRegistry(context, createFilterChassisActionSet(name, version));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	private static Filter createFilterChassisActionSet(String name, String version) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, "chassis");
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
