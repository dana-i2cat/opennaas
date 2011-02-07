package net.i2cat.mantychore.core.resourcemanager.soapendpoint.tests;

import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.core.persistence.GenericRepository;
import net.i2cat.mantychore.core.resources.descriptor.ResourceDescriptor;

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

	public ResourceDescriptor findById(String arg0) {
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
}