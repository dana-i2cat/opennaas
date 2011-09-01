package net.i2cat.nexus.events;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	Logger							log				= LoggerFactory.getLogger(Activator.class);

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
