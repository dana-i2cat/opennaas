package org.opennaas.extensions.roadm.capability.monitoring;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
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

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

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

	public static IActionSet getMonitoringActionSetService(String name, String version) throws ActivatorException {
		try {
			log.debug("Calling ConnectionsActionSetService");
			return (IActionSet) getServiceFromRegistry(context, createFilterMonitoringActionSet(name, version));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	public static IAlarmsRepository getAlarmsRepositoryService() throws ActivatorException {
		log.debug("Calling AlarmsManagerService");
		return (IAlarmsRepository) getServiceFromRegistry(context, IAlarmsRepository.class.getName());
	}

	/*
	 * necessary to get some capability type
	 */
	private static Filter createFilterMonitoringActionSet(String name, String version) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, MonitoringCapability.CAPABILITY_NAME);
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

	public static IEventManager getEventManagerService() throws ActivatorException {
		log.debug("Calling EventManager");
		// log.debug("Params: context=" + context + " class=" + IEventManager.class.getName());
		return (IEventManager) getServiceFromRegistry(context, IEventManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService(String resourceId) throws ActivatorException {
		log.debug("Calling ProtocolManager");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

}
