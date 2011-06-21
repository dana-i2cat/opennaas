package net.i2cat.mantychore.capability.ip;

import java.util.Properties;

import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.AbstractActivator;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

	public static IQueueManagerService getQueueManagerService(String resourceId) throws ActivatorException {
		try {
			log.debug("CallingQueueManagerService");
			return (IQueueManagerService) getServiceFromRegistry(context, createFilterQueueManager(resourceId));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	protected static Filter createFilterQueueManager(String resourceId) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.put(ResourceDescriptorConstants.CAPABILITY, "queue");
		properties.put(ResourceDescriptorConstants.CAPABILITY_NAME, resourceId);
		return createServiceFilter(IQueueManagerService.class.getName(), properties);
	}

	public static IActionSet getIPActionSetService(String name, String version, String protocol) throws ActivatorException {
		try {
			log.debug("Calling IPActionSetService");
			return (IActionSet) getServiceFromRegistry(context, createFilterIPActionSet(name, version, protocol));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	private static Filter createFilterIPActionSet(String name, String version, String protocol) throws InvalidSyntaxException {
		Properties properties = new Properties();

		properties
				.put(ResourceDescriptorConstants.ACTION_CAPABILITY, "ip");

		properties
				.put(ResourceDescriptorConstants.ACTION_NAME, name);

		properties
				.put(ResourceDescriptorConstants.ACTION_VERSION, version);

		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
