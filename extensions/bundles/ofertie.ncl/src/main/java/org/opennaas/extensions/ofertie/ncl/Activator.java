package org.opennaas.extensions.ofertie.ncl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
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

		log.debug("Calling ResourceManager service");

		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IEventManager getEventManagerService() throws ActivatorException {
		log.debug("Calling EventManager");
		log.debug("Params: context=" + context + " class=" + IEventManager.class.getName());
		return (IEventManager) getServiceFromRegistry(context, IEventManager.class.getName());
	}

}
