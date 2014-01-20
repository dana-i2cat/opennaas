package org.opennaas.core.resources;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

	public static BundleContext getBundleContext() {
		return context;
	}

	public static IResourceManager getResourceManagerService() throws ActivatorException {
		log.debug("Calling ResourceManagerService");
		return (IResourceManager) getServiceFromRegistry(context, IResourceManager.class.getName());
	}

	public static IProfileManager getProfileManagerService() throws ActivatorException {
		log.debug("Calling ProfileManagerService");
		return (IProfileManager) getServiceFromRegistry(context, IProfileManager.class.getName());
	}

	public static IProtocolManager getProtocolManagerService() throws ActivatorException {
		log.debug("Calling ProtocolManagerService");
		return (IProtocolManager) getServiceFromRegistry(context, IProtocolManager.class.getName());
	}

}
