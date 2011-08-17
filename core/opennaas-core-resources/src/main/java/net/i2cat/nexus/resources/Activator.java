package net.i2cat.nexus.resources;

import net.i2cat.nexus.resources.profile.IProfileManager;
import net.i2cat.nexus.resources.protocol.IProtocolManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivator implements BundleActivator {
	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	public void stop(BundleContext context) throws Exception {

	}

	public static IResourceManager getResourceManagerService() throws Exception {
		log.debug("Calling ResourceManagerService");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IProfileManager getProfileManagerService() throws Exception {
		log.debug("Calling ProfileManagerService");
		return (IProfileManager) getServiceFromRegistry(context, IProfileManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService() throws Exception {
		log.debug("Calling ProtocolManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

}
