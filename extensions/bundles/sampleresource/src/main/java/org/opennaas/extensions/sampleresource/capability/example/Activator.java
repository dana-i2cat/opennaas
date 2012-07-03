package org.opennaas.extensions.sampleresource.capability.example;

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
 * 
 * @author Elisabeth Rigol
 * 
 */
public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;

	static Log						log	= LogFactory.getLog(Activator.class);

	/**
	 * Get the Bundle Context
	 * 
	 * @return BundleContext
	 */
	public static BundleContext getContext() {
		return context;
	}

	/**
	 * Initialise the context
	 */
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	/**
	 *
	 */
	public void stop(BundleContext context) throws Exception {

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

	/**
	 * Get the connections action set service
	 * 
	 * @param name
	 * @param version
	 * @return IActionSet
	 * @throws ActivatorException
	 */
	public static IActionSet getExampleActionSetService(String name, String version) throws ActivatorException {

		try {
			log.debug("Calling ExampleActionSetService");
			return (IActionSet) getServiceFromRegistry(context, createFilterExampleActionSet(name, version));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/**
	 * Necessary to get some capability type
	 * 
	 * @param name
	 * @param version
	 * @return Filter
	 * @throws InvalidSyntaxException
	 */
	private static Filter createFilterExampleActionSet(String name, String version) throws InvalidSyntaxException {

		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, "example");
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
