/**
 * 
 */
package org.opennaas.core.resources.configurationadmin;

import java.io.IOException;
import java.util.Dictionary;

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
	@SuppressWarnings("unchecked")
	public String getProperty(String pid, String key) throws IOException {
		String value = null;
		try {
			ServiceReference ref = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
			if (ref != null) {
				ConfigurationAdmin cm = (ConfigurationAdmin) bundleContext.getService(ref);
				if (cm != null) {
					Dictionary<String, String> dict = cm.getConfiguration(pid).getProperties();
					value = dict.get(key);
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return value;
	}
}
