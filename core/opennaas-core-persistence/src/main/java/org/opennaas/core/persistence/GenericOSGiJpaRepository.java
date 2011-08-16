package org.opennaas.core.persistence;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * GenericJPARepository that gets the EntityManagerFactory from the OSGi registry
 * 
 * @author eduardgrasa
 * 
 * @param <T>
 * @param <ID>
 */
public class GenericOSGiJpaRepository<T, ID extends Serializable> extends GenericJpaRepository<T, ID> {

	private EntityManagerFactory	entityManagerFactory	= null;
	private String					persistenceUnit			= null;
	private static Log				logger					= LogFactory.getLog(GenericOSGiJpaRepository.class);

	public GenericOSGiJpaRepository() {
		super();
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	public void initializeEntityManager() throws Exception {
		entityManagerFactory = getEntityManagerFactoryFromOSGiRegistry(persistenceUnit);
		setEntityManager(entityManagerFactory.createEntityManager());
	}

	private EntityManagerFactory getEntityManagerFactoryFromOSGiRegistry(String persistenceUnit) {
		EntityManagerFactory entityManagerFactory = null;
		Filter filter = null;

		try {
			filter = createServiceFilter(EntityManagerFactory.class
					.getName(), createFilterProperties(persistenceUnit));
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		}

		try {
			entityManagerFactory = (EntityManagerFactory) getServiceFromRegistry(
					Activator.getBundleContext(), filter);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return entityManagerFactory;
	}

	private Properties createFilterProperties(String persistenceUnit) {
		Properties properties = new Properties();
		properties.put("persistenceUnit", persistenceUnit);
		properties.put("isDynamicFactory", true);

		return properties;
	}

	private Filter createServiceFilter(String clazz, Properties properties) throws InvalidSyntaxException {
		String objectClass = "(" + Constants.OBJECTCLASS + "=" + clazz + ")";
		String filterString = "(& " + objectClass;

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String currentKey = (String) keys.nextElement();
			filterString += "(" + currentKey + "=" + properties.getProperty(currentKey) + ")";
		}
		filterString += ")";
		Filter filter = FrameworkUtil.createFilter(filterString);

		return filter;
	}

	private Object getServiceFromRegistry(BundleContext bundleContext, Filter filter) throws InterruptedException {
		ServiceTracker tracker = new ServiceTracker(bundleContext, filter, null);

		tracker.open();
		Object service = null;
		logger.debug("Looking up Service from registry with properties: " + filter);
		service = tracker.waitForService(10000);
		tracker.close();

		if (service != null) {
			return service;
		}

		return null;
	}
}
