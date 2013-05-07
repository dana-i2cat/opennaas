package org.opennaas.core.security.persistence;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.persistence.PersistenceException;
import org.opennaas.core.security.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class SecurityRepository {

	private static Log				log						= LogFactory.getLog(SecurityRepository.class);

	// OpenNaaS-Security SQL scripts to initialize Spring Security ACLs SQL schema
	private static String			SQL_USERS_AUTHORITIES;
	private static String			SQL_GROUPS;
	private static String			SQL_PERSISTENT_LOGINS;
	private static String			SQL_ACLS;

	private EntityManagerFactory	entityManagerFactory	= null;
	private String					persistenceUnit			= null;
	private EntityManager			entityManager;

	public SecurityRepository() {
	}

	public void init() {
		try {
			initializeEntityManager();
		} catch (Exception e) {
			log.error("Error initializing Security Persistence!", e);
		}
		initializeSpringSecurityACLDB();
	}

	private void initializeSpringSecurityACLDB() {
		log.debug("Reading OpenNaaS-Security SQL init scripts contents...");
		SQL_USERS_AUTHORITIES = Activator.getBundleTextFileContents("/security_db_scripts/users_authorities.sql");
		SQL_GROUPS = Activator.getBundleTextFileContents("/security_db_scripts/groups.sql");
		SQL_PERSISTENT_LOGINS = Activator.getBundleTextFileContents("/security_db_scripts/persistent_logins.sql");
		SQL_ACLS = Activator.getBundleTextFileContents("/security_db_scripts/users_authorities.sql");
		log.debug("OpenNaaS-Security SQL init scripts contents read.");

		log.debug("Executing OpenNaaS-Security SQL init scripts...");
		getEntityManager().getTransaction().begin();
		try {
			getEntityManager().createNativeQuery(SQL_USERS_AUTHORITIES).executeUpdate();
			getEntityManager().createNativeQuery(SQL_GROUPS).executeUpdate();
			getEntityManager().createNativeQuery(SQL_PERSISTENT_LOGINS).executeUpdate();
			getEntityManager().createNativeQuery(SQL_ACLS).executeUpdate();
			getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			log.error("Error executing OpenNaaS-Security SQL init scripts, rollbacking", e);
			getEntityManager().getTransaction().rollback();
		}
		log.debug("OpenNaaS-Security SQL init scripts executed.");
	}

	private void initializeEntityManager() throws Exception {
		try {
			/*
			 * FIXME We have to find a better method to do this to get the entityManager. It tries 10 times to try to get the persistence. The
			 * persistence bundle spends more time because it is to need the config file persistence.xml which it needs more time to finish it.
			 */
			entityManagerFactory = waitForEntityManager(persistenceUnit);
			if (entityManagerFactory == null)
				throw new PersistenceException();

			log.debug("DESCRIPTION. Entity manager: " + entityManagerFactory);
		} catch (PersistenceException e) {
			log.debug("PersistenceException: " + e.getMessage());
			throw new Exception(e);
		}
		setEntityManager(entityManagerFactory.createEntityManager());
	}

	public void close() {
		EntityManager entityManager = getEntityManager();
		if (entityManager != null) {
			log.debug("Closing entity manager: " + entityManager);
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
			log.error("InvalidSyntaxException:" + e.getMessage());

			return null;
		}

		try {
			entityManagerFactory = (EntityManagerFactory) getServiceFromRegistry(
					Activator.getBundleContext(), filter);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error("InterruptedException:" + e.getMessage());
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
			log.info("Waiting for the activation of Entity Manager");
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
		for (String key : properties.stringPropertyNames()) {
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
		log.debug("Looking up Service from registry with properties: " + filter);
		service = tracker.waitForService(10000);
		tracker.close();

		if (service != null) {
			return service;
		}

		return null;
	}

	@Required
	@PersistenceContext
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

}
