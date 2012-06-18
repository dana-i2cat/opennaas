package org.opennaas.extensions.capability.macbridge.vlanawarebridge;

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
 * @author Jordi Puig
 */
public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;

	static Log						log	= LogFactory.getLog(Activator.class);

	/**
	 * Get the Bunble Context
	 * 
	 * @return BundleContext
	 */
	public static BundleContext getContext() {
		return context;
	}

	/**
	 * Initialize the context
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	/**
	 *
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

	}

	/**
	 * Get the Queue Manager Service
	 * 
	 * @param resourceId
	 * @return IQueueManagerCapability
	 * @throws ActivatorException
	 */
	public static IQueueManagerCapability getQueueManagerService(String resourceId)
			throws ActivatorException {

		try {
			log.debug("Calling QueueManagerService");
			return (IQueueManagerCapability) getServiceFromRegistry(context,
					createFilterQueueManager(resourceId));
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
	protected static Filter createFilterQueueManager(String resourceId)throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME,
				resourceId);
		return createServiceFilter(IQueueManagerCapability.class.getName(),
				properties);
	}

	/**
	 * Get the connections action set service
	 * 
	 * @param name
	 * @param version
	 * @return IActionSet
	 * @throws ActivatorException
	 */
	public static IActionSet getVLANAwareBridgeActionSetService(String name, String version) throws ActivatorException {
		try {
			log.debug("Calling VLANAwareBridgeActionSetService");
			return (IActionSet) getServiceFromRegistry(context,
					createFilterVLANAwareBridgeActionSet(name, version));
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
	private static Filter createFilterVLANAwareBridgeActionSet(String name, String version) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, VLANAwareBridgeCapability.CAPABILITY_TYPE);
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
