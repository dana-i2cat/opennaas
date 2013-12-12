package org.opennaas.core.security.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author Julio Carlos Barrera
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SecurityRepository {

	private static Log				log	= LogFactory.getLog(SecurityRepository.class);

	private EntityManagerFactory	entityManagerFactory;
	private EntityManager			entityManager;

	public SecurityRepository() {
	}

	public void init() throws Exception {
		try {
			initializeEntityManager();
		} catch (Exception e) {
			log.error("Error initializing Security Persistence!", e);
			throw e;
		}
	}

	private void initializeEntityManager() throws Exception {

		if (getEntityManager() == null) {
			if (getEntityManagerFactory() != null) {
				setEntityManager(getEntityManagerFactory().createEntityManager());
			}
		}

		if (getEntityManager() == null)
			throw new PersistenceException("Required EntityManager is null");
	}

	public void close() {
		EntityManager entityManager = getEntityManager();
		if (entityManager != null) {
			log.debug("Closing entity manager: " + entityManager);
			entityManager.flush();
			entityManager.close();
			setEntityManager(null);
		}
	}

	@Required
	@PersistenceContext
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

}
