package org.opennaas.extensions.openflowswitch.repository;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	public static BundleContext getContext() {
		return context;
	}

	/**
	 * Get the Queue Manager Service
	 * 
	 * @param resourceId
	 * @return IQueueManagerCapability
	 * @throws ActivatorException
	 */
	public static IQueueManagerCapability getQueueManagerService(String resourceId) throws ActivatorException {

		try {
			log.debug("Calling QueueManagerService");
			return (IQueueManagerCapability) getServiceFromRegistry(context, createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/**
	 * Necessary to get some capability type
	 * 
	 * @param resourceId
	 * @return Filter
	 * @throws InvalidSyntaxException
	 */
	protected static Filter createFilterQueueManager(String resourceId) throws InvalidSyntaxException {

		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		return createServiceFilter(IQueueManagerCapability.class.getName(), properties);
	}

	public static IActionSet getOpenflowForwardingActionSetService(String name, String version) throws ActivatorException {
		log.debug("Calling OpenflowForwardingActionSetService");
		try {
			return (IActionSet) getServiceFromRegistry(context, createFilterOpenflowForwardingActionSet(name, version));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	private static Filter createFilterOpenflowForwardingActionSet(String name, String version) throws InvalidSyntaxException {

		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, "offorwarding");
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {

		log.debug("Calling ResourceManager service");

		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling QueueManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

}
