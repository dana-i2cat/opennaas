package net.i2cat.luminis.capability.monitoring;

import java.util.Properties;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class Activator extends AbstractActivator implements BundleActivator {

	static Log						log	= LogFactory.getLog(Activator.class);
	private static BundleContext	context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	public void stop(BundleContext context) throws Exception {

	}

	public static IQueueManagerService getQueueManagerService(String resourceId) throws ActivatorException {
		try {
			log.debug("CallingQueueManagerService");
			return (IQueueManagerService) getServiceFromRegistry(context, createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	protected static Filter createFilterQueueManager(String resourceId) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.put(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.put(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		return createServiceFilter(IQueueManagerService.class.getName(), properties);
	}

	public static IActionSet getMonitoringActionSetService(String name, String version) throws ActivatorException {
		try {
			log.debug("Calling ConnectionsActionSetService");
			return (IActionSet) getServiceFromRegistry(context, createFilterMonitoringActionSet(name, version));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	private static Filter createFilterMonitoringActionSet(String name, String version) throws InvalidSyntaxException {
		Properties properties = new Properties();

		properties
				.put(ResourceDescriptorConstants.ACTION_CAPABILITY, MonitoringCapability.CAPABILITY_NAME);

		properties
				.put(ResourceDescriptorConstants.ACTION_NAME, name);

		properties
				.put(ResourceDescriptorConstants.ACTION_VERSION, version);

		return createServiceFilter(IActionSet.class.getName(), properties);
	}

	public static IEventManager getEventManagerService() throws ActivatorException {
		log.debug("Calling EventManager");
		// log.debug("Params: context=" + context + " class=" + IEventManager.class.getName());
		return (IEventManager) getServiceFromRegistry(context, IEventManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService(String resourceId) throws ActivatorException {
		log.debug("Calling EventManager");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

}
