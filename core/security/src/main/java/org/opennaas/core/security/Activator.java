package org.opennaas.core.security;

/*
 * #%L
 * OpenNaaS :: Core :: Security
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.security.persistence.SecurityRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Activator implements BundleActivator {
	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	private SecurityRepository		securityRepository;

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

	public static String getBundleTextFileContents(String path) {
		log.trace("Obtaining contents from resource '" + path + "':");

		URL url = context.getBundle().getEntry(path);

		if (url == null) {
			log.error("Resource not found: " + path);
			return null;
		}

		String contents = "";

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				contents += line + "\n";
			}
		} catch (IOException e) {
			log.error("Error getting contents from resource: " + url, e);
		}

		log.trace("Resource contents:\n" + contents);

		return contents;
	}
}
