package net.i2cat.mantychore.actionsets.junos;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	private static BundleContext	context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		this.context = context;
	}

	public void stop(BundleContext context) throws Exception {

	}

}
