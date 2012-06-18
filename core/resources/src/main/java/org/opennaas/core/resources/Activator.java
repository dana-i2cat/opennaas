package org.opennaas.core.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivator implements BundleActivator {
	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

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

	public static BundleContext getBundleContext() {
		return context;
	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {
		log.debug("Calling ResourceManagerService");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IProfileManager getProfileManagerService() throws ActivatorException {
		log.debug("Calling ProfileManagerService");
		return (IProfileManager) getServiceFromRegistry(context, IProfileManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling ProtocolManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

}
