package org.opennaas.extensions.vrf.staticroute;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Static Routing
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
