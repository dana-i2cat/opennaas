package org.opennaas.extensions.queuemanager;

/*
 * #%L
 * OpenNaaS :: Queue Manager
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

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.AbstractActivator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class Activator extends AbstractActivator implements BundleActivator {
	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling QueueManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {
		log.debug("Calling ResourceManager");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IActionSet getQueueActionSet(String name, String version, String protocol) throws ActivatorException {
		try {
			log.debug("Calling QueueActionSet");
			return (IActionSet) getServiceFromRegistry(context, createFilterQueueActionSet(name, version, protocol));
		} catch (InvalidSyntaxException e) {
			throw new ActivatorException(e);
		}
	}

	/*
	 * necessary to get some capability type
	 */
	private static Filter createFilterQueueActionSet(String name, String version, String protocol) throws InvalidSyntaxException {
		Properties properties = new Properties();
		properties.setProperty(ResourceDescriptorConstants.ACTION_CAPABILITY, "queue");
		properties.setProperty(ResourceDescriptorConstants.ACTION_NAME, name);
		properties.setProperty(ResourceDescriptorConstants.ACTION_VERSION, version);
		return createServiceFilter(IActionSet.class.getName(), properties);
	}

}
