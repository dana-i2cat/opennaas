/**
 * 
 */
package org.opennaas.core.resources.configurationadmin;

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
