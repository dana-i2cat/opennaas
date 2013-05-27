package org.opennaas.extensions.router.capability.gretunnel;

import java.util.Properties;

import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
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

	public static IQueueManagerCapability getQueueManagerService(String resourceId) throws ActivatorException {
		log.debug("Calling QueueManagerService");
		try {
			return (IQueueManagerCapability) getServiceFromRegistry(context, createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	private static Filter createFilterQueueManager(String resourceId) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		return createServiceFilter(IQueueManagerCapability.class.getName(), properties);
	}

	/**
	 * Get the connections action set service
	 * 
	 * @param name
	 * @param version
	 * @return IActionSet
	 * @throws ActivatorException
	 */
	public static IActionSet getGRETunnelActionSetService(String name, String version) throws ActivatorException {

		try {
			log.debug("Calling GRETunnelActionSetService");
			return (IActionSet) getServiceFromRegistry(context, createFilterGRETunnelActionSet(name, version));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	private static Filter createFilterGRETunnelActionSet(String name, String version) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, "gretunnel");
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}