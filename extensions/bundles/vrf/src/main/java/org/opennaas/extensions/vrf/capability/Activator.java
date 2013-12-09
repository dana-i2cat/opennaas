package org.opennaas.extensions.vrf.capability;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

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

    public static IResourceManager getResourceManagerService() throws ActivatorException {
        log.debug("Calling ResourceManager service");
        return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
    }
}
