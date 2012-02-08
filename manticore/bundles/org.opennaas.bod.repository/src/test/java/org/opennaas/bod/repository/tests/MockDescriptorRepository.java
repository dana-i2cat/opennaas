package org.opennaas.bod.repository.tests;

import java.util.List;
import java.util.Map;

import org.opennaas.core.persistence.GenericRepository;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

/**
 * A DescriptorRepository not doing persistence, nor doing nothing at all.
 *
 * @author isart
 *
 */
public class MockDescriptorRepository implements GenericRepository<ResourceDescriptor, String> {

	@Override
	public int countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countByExample(ResourceDescriptor arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(ResourceDescriptor arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ResourceDescriptor> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourceDescriptor> findByExample(ResourceDescriptor arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceDescriptor findById(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourceDescriptor> findByNamedQuery(String arg0, Object... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourceDescriptor> findByNamedQueryAndNamedParams(String arg0, Map<String, ? extends Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<ResourceDescriptor> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceDescriptor save(ResourceDescriptor arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
