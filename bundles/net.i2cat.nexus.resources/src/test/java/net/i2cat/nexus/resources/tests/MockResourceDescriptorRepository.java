package net.i2cat.nexus.resources.tests;

import java.util.List;
import java.util.Map;

import net.i2cat.nexus.persistence.GenericRepository;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

public class MockResourceDescriptorRepository implements GenericRepository<ResourceDescriptor, String>{

	@Override
	public int countAll() {
		return 0;
	}

	@Override
	public int countByExample(ResourceDescriptor arg0) {
		return 0;
	}

	@Override
	public void delete(ResourceDescriptor arg0) {
	}

	@Override
	public List<ResourceDescriptor> findAll() {
		return null;
	}

	@Override
	public List<ResourceDescriptor> findByExample(ResourceDescriptor arg0) {
		return null;
	}

	@Override
	public List<ResourceDescriptor> findByNamedQuery(String arg0,
			Object... arg1) {
		return null;
	}

	@Override
	public List<ResourceDescriptor> findByNamedQueryAndNamedParams(String arg0,
			Map<String, ? extends Object> arg1) {

		return null;
	}

	@Override
	public Class<ResourceDescriptor> getEntityClass() {
		return null;
	}

	@Override
	public ResourceDescriptor save(ResourceDescriptor arg0) {
		return arg0;
	}

	@Override
	public ResourceDescriptor findById(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}