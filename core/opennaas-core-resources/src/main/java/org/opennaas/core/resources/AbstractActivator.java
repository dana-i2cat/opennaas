package org.opennaas.core.resources;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractActivator {
	/** Logger */
	private static Log	log	= LogFactory.getLog(AbstractActivator.class);

	/**
	 * Create a Filter to give to the service tracker based on the information about the service to lookup in the properties
	 * 
	 * @throws InvalidSyntaxException
	 */
	protected static Filter createServiceFilter(String clazz, Properties properties) throws InvalidSyntaxException {
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
	 * @throws ActivatorException
	 */
	protected static Object getServiceFromRegistry(BundleContext bundleContext, Filter filter)
			throws ActivatorException {
		ServiceTracker tracker = new ServiceTracker(bundleContext, filter, null);

		tracker.open();
		Object service = null;
		log.debug("Looking up Service from regisrty with properties: " + filter);
		try {
			service = tracker.waitForService(20000);
		} catch (InterruptedException e) {
			throw new ActivatorException(e);
		}
		tracker.close();

		if (service != null) {
			return service;
		}

		throw new ActivatorException(ErrorConstants.ERROR_ACTIVATOR_SERVICE_NOTFOUND);
	}

	/**
	 * Fetch a service from OSGI Registry
	 * 
	 * @return Object service instance
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	protected static Object getServiceFromRegistry(BundleContext bundleContext, String clazz)
			throws ActivatorException {
		ServiceTracker tracker = new ServiceTracker(bundleContext, clazz, null);

		tracker.open();
		Object service = null;
		log.debug("Looking up Service: " + clazz + " From service registry");
		try {
			service = tracker.waitForService(20000);
		} catch (InterruptedException e) {
			throw new ActivatorException(e);
		}
		tracker.close();

		if (service != null) {
			return service;
		}

		throw new ActivatorException(ErrorConstants.ERROR_ACTIVATOR_SERVICE_NOTFOUND);
	}

}
