package org.opennaas.core.persistence;

/*
 * #%L
 * OpenNaaS :: Core :: JPA Persistence
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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This bundle's activator. Mainly used to get the bundle context
 * 
 * @author edu
 * 
 */
public class Activator implements BundleActivator {

	private static BundleContext	context;

	public void start(BundleContext bundleContext) throws Exception {
		context = bundleContext;
	}

	public void stop(BundleContext context) throws Exception {
	}

	public static BundleContext getBundleContext() {
		return context;
	}

}
