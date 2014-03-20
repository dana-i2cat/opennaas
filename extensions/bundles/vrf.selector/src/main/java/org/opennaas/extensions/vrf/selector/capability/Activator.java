package org.opennaas.extensions.vrf.selector.capability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractActivator implements BundleActivator {

    private static BundleContext context;
    static Log log = LogFactory.getLog(Activator.class);

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
     */
    @Override
    public void start(BundleContext context) throws Exception {
        Activator.context = context;
    }

    /**
     *
     */
    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
