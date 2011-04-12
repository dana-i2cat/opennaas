package net.i2cat.nexus.resources.descriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.nexus.persistence.GenericOSGiJpaRepository;

public class ResourceDescriptorRepository extends GenericOSGiJpaRepository<ResourceDescriptor, String>{
	
    private static Logger logger = LoggerFactory.getLogger(ResourceDescriptorRepository.class);
    
	public ResourceDescriptorRepository(){
		super();
	}
	
	public List<ResourceDescriptor> getResourceDescriptors(String type){
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("type", type);
		return this.findByNamedQueryAndNamedParams("resourceDescriptor.findByType", queryParams);
	}

	@Override
	public ResourceDescriptor save(ResourceDescriptor resourceDescriptor) {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().merge(resourceDescriptor);
//			getEntityManager().persist(resourceDescriptor);
			getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
        	getEntityManager().getTransaction().rollback();
            logger.debug("Exception during saving ResourceDescriptor");
            ex.printStackTrace();
        }
		return resourceDescriptor;
	}
	
	@Override
	public void delete(ResourceDescriptor resourceDescriptor) {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().remove(resourceDescriptor);
			getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
        	getEntityManager().getTransaction().rollback();
            logger.debug("Exception while removing ResourceDescriptor");
            ex.printStackTrace();
        }
	}
}