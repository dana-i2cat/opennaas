/**
 * 
 */
package org.opennaas.core.resources.configurationadmin;

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

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jordi
 */
public class ConfigurationAdminUtil {

	private BundleContext	bundleContext;

	/**
	 * 
	 */
	public ConfigurationAdminUtil(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * @param pid
	 *            the persistent id
	 * @param key
	 *            the key
	 * @return the value
	 * @throws IOException
	 */
	public String getProperty(String pid, String key) throws IOException {
		return getProperty(bundleContext, pid, key);
	}

	public static String getProperty(BundleContext bundleContext, String pid, String key) throws IOException {
		String value = null;
		Dictionary<String, String> dict = getPropertiesDict(bundleContext, pid);
		if (dict != null) {
			value = dict.get(key);
		}
		return value;
	}

	public static Properties getProperties(BundleContext bundleContext, String configurationPid) throws IOException {
		return dictionaryToProperties(getPropertiesDict(bundleContext, configurationPid));
	}

	@SuppressWarnings("unchecked")
	private static Dictionary<String, String> getPropertiesDict(BundleContext bundleContext, String configurationPid) throws IOException {
		Dictionary<String, String> dict = null;
		ConfigurationAdmin cm = getConfigurationAdmin(bundleContext);
		if (cm != null) {
			dict = cm.getConfiguration(configurationPid).getProperties();
		}
		return dict;
	}

	private static ConfigurationAdmin getConfigurationAdmin(BundleContext bundleContext) {
		ConfigurationAdmin cm = null;
		ServiceReference ref = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
		if (ref != null) {
			cm = (ConfigurationAdmin) bundleContext.getService(ref);
		}
		return cm;
	}

	private static Properties dictionaryToProperties(Dictionary<String, String> dict) {
		if (dict == null)
			return null;

		Properties properties = new Properties();
		Enumeration<String> keys = dict.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, dict.get(key));
		}
		return properties;
	}
}
