package org.opennaas.extensions.queuemanager;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
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

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling QueueManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {
		log.debug("Calling ResourceManager");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IActionSet getQueueActionSet(String name, String version, String protocol) throws ActivatorException {
		try {
			log.debug("Calling QueueActionSet");
			return (IActionSet) getServiceFromRegistry(context, createFilterQueueActionSet(name, version, protocol));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	private static Filter createFilterQueueActionSet(String name, String version, String protocol) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
