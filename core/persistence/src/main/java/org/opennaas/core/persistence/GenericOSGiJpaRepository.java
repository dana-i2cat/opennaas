package org.opennaas.core.persistence;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import javax.persistence.EntityManager;
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

		try {
			/*
			 * FIXME We have to find a better method to do this to get the entityManager. It tries 10 times to try to get the persistence. The
			 * persistence bundle spends more time because it is to need the config file persistence.xml which it needs more time to finish it.
			 */
			entityManagerFactory = waitForEntityManager(persistenceUnit);
			if (entityManagerFactory == null)
				throw new PersistenceException();

			logger.debug("DESCRIPTION. Entity manager: " + entityManagerFactory);
		} catch (PersistenceException e) {
			logger.debug("PersistenceException: " + e.getMessage());
			throw new Exception(e);
		}
		setEntityManager(entityManagerFactory.createEntityManager());
	}

    public void close() {
        EntityManager entityManager = getEntityManager();
        if (entityManager != null) {
			logger.debug("Closing entity manager: " + entityManager);
            entityManager.close();
            setEntityManager(null);
        }
    }

	private EntityManagerFactory getEntityManagerFactoryFromOSGiRegistry(String persistenceUnit) {
		EntityManagerFactory entityManagerFactory = null;
		Filter filter = null;

		try {
			filter = createServiceFilter(EntityManagerFactory.class
					.getName(), createFilterProperties(persistenceUnit));
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			logger.error("InvalidSyntaxException:" + e.getMessage());

			return null;
		}

		try {
			entityManagerFactory = (EntityManagerFactory) getServiceFromRegistry(
					Activator.getBundleContext(), filter);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("InterruptedException:" + e.getMessage());
		}

		return entityManagerFactory;
	}

	public EntityManagerFactory waitForEntityManager(String persistenceUnit) {
		int MAX_RETRIES = 10;
		boolean active = false;

		for (int i = 0; i < MAX_RETRIES; i++) {
			EntityManagerFactory entityManagerFactory = getEntityManagerFactoryFromOSGiRegistry(persistenceUnit);
			active = (null != entityManagerFactory);
			if (active == true) {
				return entityManagerFactory;
			}
			logger.info("Waiting for the activation of Entity Manager");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private Properties createFilterProperties(String persistenceUnit) {
		Properties properties = new Properties();
		properties.setProperty("osgi.unit.name", persistenceUnit);
		properties.setProperty("org.apache.aries.jpa.container.managed", "true");
		return properties;
	}

	private Filter createServiceFilter(String clazz, Properties properties) throws InvalidSyntaxException {
		StringBuilder query = new StringBuilder();
		query.append("(&");
		query.append("(").append(Constants.OBJECTCLASS).append("=").append(clazz).append(")");
		for (String key: properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			query.append("(").append(key).append("=").append(value).append(")");
		}
		query.append(")");
		return FrameworkUtil.createFilter(query.toString());
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
