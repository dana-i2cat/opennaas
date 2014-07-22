package org.opennaas.core.resources.descriptor;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.RollbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.persistence.GenericOSGiJpaRepository;
import org.opennaas.core.persistence.PersistenceException;

public class ResourceDescriptorRepository extends GenericOSGiJpaRepository<ResourceDescriptor, String> {

	private static Log	logger	= LogFactory.getLog(ResourceDescriptorRepository.class);

	public ResourceDescriptorRepository() {
		super();
	}

	public List<ResourceDescriptor> getResourceDescriptors(String type) {
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("type", type);
		return this.findByNamedQueryAndNamedParams("resourceDescriptor.findByType", queryParams);
	}

	@Override
	public ResourceDescriptor save(ResourceDescriptor resourceDescriptor) {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().merge(resourceDescriptor);
			// getEntityManager().persist(resourceDescriptor);
			getEntityManager().getTransaction().commit();
		} catch (RollbackException ex) {
			getEntityManager().getTransaction().rollback();
			throw new PersistenceException("Exception while saving resourceDescriptor. Save operation has been rolledBack.", ex);
		}
		return resourceDescriptor;
	}

	@Override
	public void delete(ResourceDescriptor resourceDescriptor) {
		try {
			getEntityManager().getTransaction().begin();
			ResourceDescriptor attachedResourceDescriptor = findById(resourceDescriptor.getId());
			getEntityManager().remove(attachedResourceDescriptor);
			getEntityManager().getTransaction().commit();
		} catch (RollbackException ex) {
			getEntityManager().getTransaction().rollback();
			throw new PersistenceException("Exception while removing ResourceDescriptor. Delete operation has been rolledBack.", ex);
		}
	}
}