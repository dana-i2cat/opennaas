package org.opennaas.core.resources.mock;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
