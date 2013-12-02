package org.opennaas.extensions.roadm.wonesys.actionsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivator implements BundleActivator {

	static Log						log				= LogFactory.getLog(Activator.class);

	private static BundleContext	bundleContext	= null;

	@Override
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.bundleContext = null;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	public static IEventManager getEventManagerService() throws ActivatorException {
		log.debug("Calling EventManager");
		log.debug("Params: context=" + bundleContext + " class=" + IEventManager.class.getName());
		return (IEventManager) getServiceFromRegistry(bundleContext, IEventManager.class.getName());
	}

}
