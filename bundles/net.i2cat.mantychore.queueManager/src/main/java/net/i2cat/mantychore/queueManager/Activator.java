package net.i2cat.mantychore.queueManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This bundle's activator. Mainly used to get the bundle context
 * @author edu
 *
 */
public class Activator implements BundleActivator{
	
	private static BundleContext context;

	public void start(BundleContext bundleContext) throws Exception {
		context = bundleContext;
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
	}
	
	public static BundleContext getContext(){
		return context;
	}
}
