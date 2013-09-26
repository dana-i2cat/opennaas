package org.opennaas.extensions.sdnnetwork;

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

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class Activator extends AbstractActivator implements BundleActivator {

	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	public static BundleContext getContext() {
		return context;
	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {
		log.debug("Calling ResourceManager service");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}
	
	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling ProtocolManager service");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}
	
	public static IActionSet getActionSetService(
			String capability, String driverName, String driverVersion) throws ActivatorException {
		log.debug("Calling ActionSetService: Capability="+capability+",Driver="+driverName+",Version="+driverVersion);
		try {
			return (IActionSet) getServiceFromRegistry(context, createFilterActionSet(capability, driverName, driverVersion));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}
	
	private static Filter createFilterActionSet(String capability, String driverName, String driverVersion) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, capability);
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, driverName);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, driverVersion);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
