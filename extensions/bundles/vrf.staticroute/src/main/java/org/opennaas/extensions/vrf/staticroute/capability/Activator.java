package org.opennaas.extensions.vrf.staticroute.capability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator extends AbstractActivator implements BundleActivator {

    private static BundleContext context;
    static Log log = LogFactory.getLog(Activator.class);
    private ServiceRegistration registration;

    /**
     * Get the Bundle Context
     *
     * @return BundleContext
     */
    public static BundleContext getContext() {
        return context;
    }

    /**
     * Initialise the context
     *
     * @param context
     * @throws java.lang.Exception
     */
    @Override
    public void start(BundleContext context) throws Exception {
        //registration = context.registerService(IStaticRoutingCapability.class.getName(), new StaticRoutingCapability(), null);
        Activator.context = context;
    }

    /**
     *
     * @param context
     * @throws java.lang.Exception
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        //registration.unregister();
    }
}
