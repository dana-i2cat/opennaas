package net.i2cat.nexus.resources;

import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that contains utility methods which help is accessing the OSGI service
 * registry
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class RegistryUtil {
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(RegistryUtil.class);

	/**
	 * Create a Filter to give to the service tracker based on the information
	 * about the service to lookup in the properties
	 * 
	 * @throws InvalidSyntaxException
	 */
	public static Filter createServiceFilter(String clazz, Properties properties) throws InvalidSyntaxException {
		String objectClass = "(" + Constants.OBJECTCLASS + "=" + clazz + ")";

		String filterString = "(&" + objectClass;
		
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String currentKey = (String) keys.nextElement();
			filterString += "(" + currentKey + "=" + properties.getProperty(currentKey) + ")";
		}

		filterString += ")";

		Filter filter = FrameworkUtil.createFilter(filterString);

		return filter;
	}

	/**
	 * Fetch a service from OSGI Registry
	 * 
	 * @return Object service instance
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	public static Object getServiceFromRegistry(BundleContext bundleContext, Filter filter)
			throws InterruptedException {
		ServiceTracker tracker = new ServiceTracker(bundleContext, filter, null);

		tracker.open();
		Object service = null;
		logger.debug("Looking up Service from regisrty with properties: " + filter);
		service = tracker.waitForService(20000);
		tracker.close();

		if (service != null) {
			return service;
		}

		return null;
	}
	

	/**
	 * Fetch a service from OSGI Registry
	 * 
	 * @return Object service instance
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	public static Object getServiceFromRegistry(BundleContext bundleContext, String clazz)
			throws InterruptedException {
		ServiceTracker tracker = new ServiceTracker(bundleContext, clazz, null);

		tracker.open();
		Object service = null;
		logger.debug("Looking up Service: " + clazz + " From service registry");
		service = tracker.waitForService(20000);
		tracker.close();

		if (service != null) {
			return service;
		}

		return null;
	}	
}