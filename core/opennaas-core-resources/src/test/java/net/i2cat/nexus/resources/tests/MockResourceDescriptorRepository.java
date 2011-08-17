package net.i2cat.nexus.resources.tests;

import java.util.List;
import java.util.Map;

import org.opennaas.core.persistence.GenericRepository;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

public class MockResourceDescriptorRepository implements GenericRepository<ResourceDescriptor, String>{

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

	public List<ResourceDescriptor> findByNamedQuery(String arg0,
			Object... arg1) {
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
		return arg0;
	}

	public ResourceDescriptor findById(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}