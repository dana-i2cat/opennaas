package org.opennaas.core.resources.tests.descriptor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

public class ResourceDescriptorRepositoryTest extends ResourceDescriptorSupport {

	private static Log				logger		= LogFactory.getLog(ResourceDescriptorRepositoryTest.class);
	private EntityManagerFactory	emFactory	= null;
	private EntityManager			em			= null;
	private Connection				connection	= null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			logger.info("Starting in-memory HSQL database for unit tests");
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection("jdbc:hsqldb:mem:unit-testing-jpa", "sa", "");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception during HSQL database startup.");
		}
		try {
			logger.info("Building JPA EntityManager for unit tests");
			emFactory = Persistence.createEntityManagerFactory("ResourceCore-test");
			if(emFactory==null)
				fail("Persistence.createEntityManagerFactory didn't give us one :(.");
			em = emFactory.createEntityManager();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception during JPA EntityManager instanciation.");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		logger.info("Shuting down Hibernate JPA layer.");
		if (em != null) {
			em.close();
		}
		if (emFactory != null) {
			emFactory.close();
		}
		logger.info("Stopping in-memory HSQL database.");
		try {
			connection.createStatement().execute("SHUTDOWN");
		} catch (Exception ex) {
		}
	}

	@Test
	public void testBasicPersistence() {
		try {
			// Test Save
			em.getTransaction().begin();
			ResourceDescriptor config = createSampleDescriptor();
			em.persist(config);
			em.getTransaction().commit();
			assertNotNull(config);
			// Test Load
			em.getTransaction().begin();
			ResourceDescriptor loaded = em.find(ResourceDescriptor.class, config.getId());
			assertNotNull(loaded);
			List<CapabilityDescriptor> capabilityDescriptors = loaded.getCapabilityDescriptors();
			Iterator<CapabilityDescriptor> capabilityIt = capabilityDescriptors.iterator();
			assertEquals(loaded.getInformation().getType(), "ca.inocybe.xxx");
			assertEquals(loaded.getInformation().getDescription(), "Test");
			assertEquals(loaded.getInformation().getName(), "Resource");
			assertEquals(loaded.getInformation().getVersion(), "1.0.0");
			while (capabilityIt.hasNext()) {
				CapabilityDescriptor capabilityDescriptor = capabilityIt.next();
				CapabilityProperty prop = capabilityDescriptor.getCapabilityProperties().get(0);
				assertEquals(prop.getName(), "name");
				assertEquals(prop.getValue(), "value");
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			logger.error("Exception during testPersistence", ex);
			fail("Exception during testPersistence");
		}
	}
}