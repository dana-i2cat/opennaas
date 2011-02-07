package net.i2cat.mantychore.core.resources.tests.descriptor;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.mantychore.core.persistence.GenericRepository;
import net.i2cat.mantychore.core.resources.ResourceException;
import net.i2cat.mantychore.core.resources.descriptor.ResourceDescriptor;

/**
 * MockResourceDescriptorRepository
 * Mock class for resource descriptor saving
 * @author knguyen
 *
 */
public class MockResourceDescriptorRepository implements GenericRepository<ResourceDescriptor, Long> {
	
    private static Logger logger = LoggerFactory.getLogger(MockResourceDescriptorRepository.class);
    private EntityManagerFactory emFactory = null;
    private EntityManager em = null;

	public MockResourceDescriptorRepository() throws ResourceException {
		try {
        	emFactory = Persistence.createEntityManagerFactory("ResourceCore-test");
        	em = emFactory.createEntityManager();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResourceException("MockResourceDescriptorRepository could not be created");
        }
	}
	
	public int countAll() {
		return 0;
	}

	public int countByExample(ResourceDescriptor arg0) {
		return 0;
	}

	public void delete(ResourceDescriptor arg0) {
	}

	public List<ResourceDescriptor> findAll() {
		return null;
	}

	public List<ResourceDescriptor> findByExample(ResourceDescriptor arg0) {
		return null;
	}

	public ResourceDescriptor findById(Long arg0) {
		return null;
	}

	public List<ResourceDescriptor> findByNamedQuery(String arg0, Object... arg1) {
		return null;
	}

	public List<ResourceDescriptor> findByNamedQueryAndNamedParams(String arg0,
			Map<String, ? extends Object> arg1) {
		return null;
	}

	public Class<ResourceDescriptor> getEntityClass() {
		return null;
	}

	public ResourceDescriptor save(ResourceDescriptor arg0) {
		try {
	        em.getTransaction().begin();
	        em.persist(arg0);
	        em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.debug("Exception during saving ResourceDescriptor");
            ex.printStackTrace();
        }
		return arg0;
	}

}
