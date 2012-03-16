package org.opennaas.network.capability.ospf;

import java.util.Properties;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;

	private static Log				log	= LogFactory.getLog(Activator.class);

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

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.context = null;
	}

	public static IQueueManagerService getQueueManagerService(String resourceId)
			throws ActivatorException {
		try {
			log.debug("Calling QueueManagerService");
			return (IQueueManagerService) getServiceFromRegistry(context,
					createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	protected static Filter createFilterQueueManager(String resourceId)
			throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY, "netqueue");
		properties.setProperty(ResourceDescriptorConstants.CAPABILITY_NAME,
				resourceId);
		return createServiceFilter(IQueueManagerService.class.getName(),
				properties);
	}

}