package org.opennaas.extensions.network.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.core.resources.helpers.ResourceDescriptorSupport;
import org.opennaas.extensions.network.helpers.NetworkResourceDescriptorSupport;

public class ResourceDescriptorRepositoryTest extends TestCase {

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
			logger.error("Exception in set up", ex);
			fail("Exception during HSQL database startup.");
		}
		try {
			logger.info("Building JPA EntityManager for unit tests");
			emFactory = Persistence.createEntityManagerFactory("ResourceCore-test");
			if (emFactory == null)
				fail("Persistence.createEntityManagerFactory didn't give us one :(.");
			em = emFactory.createEntityManager();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in set up", ex);
			fail("Exception during JPA EntityManager instanciation.");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		logger.info("Shutting down EntityManager.");
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
			logger.error("Exception in set up", ex);
		}
	}

	@Test
	public void testBasicPersistence() {
		try {
			// Test Save
			em.getTransaction().begin();
			ResourceDescriptor config = ResourceDescriptorSupport.createSampleDescriptor();
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

	@Test
	public void testCloningOfPersistentDescriptor()
			throws Exception {
		try {
			// Create persistent descriptor
			em.getTransaction().begin();
			ResourceDescriptor config = ResourceDescriptorSupport.createSampleDescriptor();
			em.persist(config);
			em.getTransaction().commit();

			// Load it back
			em.getTransaction().begin();
			ResourceDescriptor loaded =
					em.find(ResourceDescriptor.class, config.getId());

			// Clone it
			ResourceDescriptor copy = (ResourceDescriptor) loaded.clone();

			// Check content of descriptor
			assertEquals(copy.getInformation().getType(), "ca.inocybe.xxx");
			assertEquals(copy.getInformation().getDescription(), "Test");
			assertEquals(copy.getInformation().getName(), "Resource");
			assertEquals(copy.getInformation().getVersion(), "1.0.0");
			for (CapabilityDescriptor descriptor : copy.getCapabilityDescriptors()) {
				CapabilityProperty prop =
						descriptor.getCapabilityProperties().get(0);
				assertEquals(prop.getName(), "name");
				assertEquals(prop.getValue(), "value");
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			throw ex;
		}
	}

	@Test
	public void testPersistingClonedPersistentDescriptor()
			throws Exception {
		try {
			// Create persistent descriptor
			em.getTransaction().begin();
			ResourceDescriptor config = ResourceDescriptorSupport.createSampleDescriptor();
			em.persist(config);
			em.getTransaction().commit();

			// Clone it and store with new ID
			em.getTransaction().begin();
			ResourceDescriptor loaded =
					em.find(ResourceDescriptor.class, config.getId());
			ResourceDescriptor copy = (ResourceDescriptor) loaded.clone();
			copy.setId("2");
			em.persist(copy);
			em.getTransaction().commit();

			// Load back the result and check values
			em.getTransaction().begin();
			ResourceDescriptor result =
					em.find(ResourceDescriptor.class, copy.getId());

			// Assert content of descriptor
			assertEquals(result.getInformation().getType(), "ca.inocybe.xxx");
			assertEquals(result.getInformation().getDescription(), "Test");
			assertEquals(result.getInformation().getName(), "Resource");
			assertEquals(result.getInformation().getVersion(), "1.0.0");
			for (CapabilityDescriptor descriptor : result.getCapabilityDescriptors()) {
				CapabilityProperty prop =
						descriptor.getCapabilityProperties().get(0);
				assertEquals(prop.getName(), "name");
				assertEquals(prop.getValue(), "value");
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			throw ex;
		}
	}

	@Test
	public void testVirtualResourcePersistence() {
		try {
			// Test Save
			em.getTransaction().begin();
			ResourceDescriptor config = ResourceDescriptorSupport.createVirtualResourceDescriptor();
			em.persist(config);
			em.getTransaction().commit();
			assertNotNull(config);
			// Test Load
			em.getTransaction().begin();
			ResourceDescriptor loaded = em.find(ResourceDescriptor.class, config.getId());
			assertNotNull(loaded);
			List<CapabilityDescriptor> capabilityDescriptors = loaded.getCapabilityDescriptors();
			Iterator<CapabilityDescriptor> capabilityIt = capabilityDescriptors.iterator();
			assertEquals(loaded.getInformation().getType(), "router");
			assertEquals(loaded.getInformation().getDescription(), "virtual resource description");
			assertEquals(loaded.getInformation().getName(), "logical1");
			assertEquals(loaded.getInformation().getVersion(), "1.0.0");
			while (capabilityIt.hasNext()) {
				CapabilityDescriptor capabilityDescriptor = capabilityIt.next();
				CapabilityProperty prop = capabilityDescriptor.getCapabilityProperties().get(0);
				assertEquals(prop.getName(), "name");
				assertEquals(prop.getValue(), "value");
			}

			Map<String, String> properties = config.getProperties();
			assertNotNull(properties);
			String key = properties.get("virtual");
			assertEquals("true", key);

			em.getTransaction().commit();
		} catch (Exception ex) {
			em.getTransaction().rollback();
			logger.error("Exception during testPersistence", ex);
			fail("Exception during testPersistence");
		}
	}

	@Test
	public void testResourceWithNetworkCapabilitiesPersistence() {
		try {
			// Test Save
			em.getTransaction().begin();
			ResourceDescriptor config = NetworkResourceDescriptorSupport.createNetworkDescriptor();
			em.persist(config);
			em.getTransaction().commit();
			assertNotNull(config);
			// Test Load
			em.getTransaction().begin();
			ResourceDescriptor loaded = em.find(ResourceDescriptor.class, config.getId());
			assertNotNull(loaded);

			List<CapabilityDescriptor> capabilityDescriptors = loaded.getCapabilityDescriptors();
			Iterator<CapabilityDescriptor> capabilityIt = capabilityDescriptors.iterator();
			assertEquals(loaded.getInformation().getType(), "network");
			assertEquals(loaded.getInformation().getDescription(), "network description");
			assertEquals(loaded.getInformation().getName(), "networklayer1.0");
			assertEquals(loaded.getInformation().getVersion(), "1.0.0");
			while (capabilityIt.hasNext()) {
				CapabilityDescriptor capabilityDescriptor = capabilityIt.next();
				CapabilityProperty prop = capabilityDescriptor.getCapabilityProperties().get(0);
				assertEquals(prop.getName(), "name");
				assertEquals(prop.getValue(), "value");
			}

			em.getTransaction().commit();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			em.getTransaction().rollback();
			logger.error("Exception during testPersistence", ex);
			fail("Exception during testPersistence");
		}
	}

	@Test
	public void testResourceWithNetworkDomains() {
		em.getTransaction().begin();
		ResourceDescriptor config = NetworkResourceDescriptorSupport.createNetworkDescriptorWithNetworkDomains();
		em.persist(config);
		em.getTransaction().commit();
		assertNotNull(config);
		// Test Load
		em.getTransaction().begin();
		ResourceDescriptor loaded = em.find(ResourceDescriptor.class, config.getId());
		assertNotNull(loaded);

		assertEquals(loaded.getFileTopology(), "network/network_diffs_layer.xml");

		NetworkTopology networkTopology = loaded.getNetworkTopology();

		assertNotNull(networkTopology.getNetworkDomains());
		assertEquals(networkTopology.getNetworkDomains().size(), 1);

		/* network description */
		assertNotNull(networkTopology.getDevices());
		assertEquals(networkTopology.getDevices().get(0).getName(), "router:R-AS2-1");
		assertEquals(networkTopology.getDevices().get(1).getName(), "router:R-AS2-2");
		assertEquals(networkTopology.getDevices().get(2).getName(), "router:R-AS2-3");

		em.getTransaction().commit();
	}

	@Test
	public void testResourceNetworkTopology() {
		em.getTransaction().begin();
		ResourceDescriptor config = NetworkResourceDescriptorSupport.createNetworkDescriptor();
		em.persist(config);
		em.getTransaction().commit();
		assertNotNull(config);
		// Test Load
		em.getTransaction().begin();
		ResourceDescriptor loaded = em.find(ResourceDescriptor.class, config.getId());
		assertNotNull(loaded);

		assertEquals(loaded.getFileTopology(), "network/network_example1.xml");
		/* network description */
		NetworkTopology networkTopology = loaded.getNetworkTopology();

		assertNotNull(networkTopology.getDevices());
		assertEquals(networkTopology.getDevices().get(0).getName(), "router:R-AS2-1");
		assertNotNull(networkTopology.getDevices().get(0).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().size(), 3);
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(0).getResource(), "#router:R-AS2-1:lt-1/2/0.51");
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(1).getResource(), "#router:R-AS2-1:lt-1/2/0.100");
		assertEquals(networkTopology.getDevices().get(0).getHasInterfaces().get(2).getResource(), "#router:R-AS2-1:lo0.1");

		assertEquals(networkTopology.getDevices().get(1).getName(), "router:R-AS2-2");
		assertNotNull(networkTopology.getDevices().get(1).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().size(), 3);
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(0).getResource(), "#router:R-AS2-2:lt-1/2/0.102");
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(1).getResource(), "#router:R-AS2-2:lt-1/2/0.101");
		assertEquals(networkTopology.getDevices().get(1).getHasInterfaces().get(2).getResource(), "#router:R-AS2-2:lo0.3");

		assertEquals(networkTopology.getDevices().get(2).getName(), "router:R-AS2-3");
		assertNotNull(networkTopology.getDevices().get(2).getHasInterfaces());
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().size(), 2);
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().get(0).getResource(), "#router:R-AS2-3:lt-1/2/0.103");
		assertEquals(networkTopology.getDevices().get(2).getHasInterfaces().get(1).getResource(), "#router:R-AS2-3:lo0.4");

		assertNotNull(networkTopology.getInterfaces());
		assertEquals(networkTopology.getInterfaces().size(), 8);

		assertEquals(networkTopology.getInterfaces().get(0).getCapacity(), "1.2E+9");
		assertEquals(networkTopology.getInterfaces().get(0).getName(), "router:R-AS2-1:lt-1/2/0.51");
		assertEquals(networkTopology.getInterfaces().get(0).getLinkTo().getName(), "#router:R1:lt-1/2/0.50");

		assertEquals(networkTopology.getInterfaces().get(1).getCapacity(), "1.2E+9");
		assertEquals(networkTopology.getInterfaces().get(1).getName(), "router:R-AS2-1:lt-1/2/0.100");
		assertEquals(networkTopology.getInterfaces().get(1).getLinkTo().getName(), "#router:R-AS2-2:lt-1/2/0.101");

		assertEquals(networkTopology.getInterfaces().get(2).getName(), "router:R-AS2-1:lo0.1");

		assertEquals(networkTopology.getInterfaces().get(3).getCapacity(), "1.2E+9");
		assertEquals(networkTopology.getInterfaces().get(3).getName(), "router:R-AS2-2:lt-1/2/0.101");
		assertEquals(networkTopology.getInterfaces().get(3).getLinkTo().getName(), "#router:R-AS2-1:lt-1/2/0.100");

		assertEquals(networkTopology.getInterfaces().get(4).getCapacity(), "1.2E+9");
		assertEquals(networkTopology.getInterfaces().get(4).getName(), "router:R-AS2-2:lt-1/2/0.102");
		assertEquals(networkTopology.getInterfaces().get(4).getLinkTo().getName(), "#router:R-AS2-3:lt-1/2/0.103");

		assertEquals(networkTopology.getInterfaces().get(5).getName(), "router:R-AS2-2:lo0.3");

		assertEquals(networkTopology.getInterfaces().get(6).getCapacity(), "1.2E+9");
		assertEquals(networkTopology.getInterfaces().get(6).getName(), "router:R-AS2-3:lt-1/2/0.103");
		assertEquals(networkTopology.getInterfaces().get(6).getLinkTo().getName(), "#router:R-AS2-2:lt-1/2/0.102");

		assertEquals(networkTopology.getInterfaces().get(7).getName(), "router:R-AS2-3:lo0.4");

		em.getTransaction().commit();
	}

	@Test
	public void testNetworkResourceReferencesPersistence() {
		em.getTransaction().begin();
		ResourceDescriptor config = NetworkResourceDescriptorSupport.createNetworkDescriptor();
		em.persist(config);
		em.getTransaction().commit();
		assertNotNull(config);
		em.getTransaction().begin();
		ResourceDescriptor loaded = em.find(ResourceDescriptor.class, config.getId());
		assertNotNull(loaded);

		for (String frienlyName : config.getResourceReferences().keySet()) {
			assertTrue(loaded.getResourceReferences().containsKey(frienlyName));
			assertEquals(config.getResourceReferences().get(frienlyName), loaded.getResourceReferences().get(frienlyName));
		}
		em.getTransaction().commit();
	}
}
