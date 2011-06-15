package net.i2cat.mantychore.queuemanager;

import java.util.Properties;

import net.i2cat.nexus.resources.AbstractActivator;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptorConstants;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

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

	public void start(BundleContext context) throws Exception {
		this.context = context;
	}

	public void stop(BundleContext context) throws Exception {

	}

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling QueueManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
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

		properties
				.put(ResourceDescriptorConstants.ACTION_CAPABILITY, "queue");

		properties
				.put(ResourceDescriptorConstants.ACTION_NAME, name);

		properties
				.put(ResourceDescriptorConstants.ACTION_VERSION, version);

		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
