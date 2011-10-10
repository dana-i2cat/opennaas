package org.opennaas.core.events;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Activator implements BundleActivator {

	Log							log				= LogFactory.getLog(Activator.class);

	private static BundleContext	bundleContext	= null;

	@Override
	public void start(BundleContext context) throws Exception {
		if (context == null)
			log.error("CONTEXT IS NULL!!!!!");
		Activator.bundleContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.bundleContext = null;
	}

	public static BundleContext getBundleContext() {
		return Activator.bundleContext;
	}

}
