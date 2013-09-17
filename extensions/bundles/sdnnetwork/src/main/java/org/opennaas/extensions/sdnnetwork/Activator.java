package org.opennaas.extensions.sdnnetwork;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


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
}
