package org.opennaas.core.persistence;

/*
 * #%L
 * OpenNaaS :: Core :: JPA Persistence
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Generic Repository, providing basic CRUD operations
 * 
 * @author Jurgen Lust
 * 
 * @param <T>
 *            the entity type
 * @param <ID>
 *            the primary key type
 */
public interface GenericRepository<T, ID extends Serializable> {
	// ~ Methods ----------------------------------------------------------------

	/**
	 * Get the Class of the entity
	 * 
	 * @return the class
	 */
	Class<T> getEntityClass();

	/**
	 * Find an entity by its primary key
	 * 
	 * @param id
	 *            the primary key
	 * @return the entity
	 */
	T findById(final ID id);

	/**
	 * Load all entities
	 * 
	 * @return the list of entities
	 */
	List<T> findAll();

	/**
	 * Find entities based on an example
	 * 
	 * @param exampleInstance
	 *            the example
	 * @return the list of entities
	 */
	List<T> findByExample(final T exampleInstance);

	/**
	 * Find using a named query
	 * 
	 * @param queryName
	 *            the name of the query
	 * @param params
	 *            the query parameters
	 * 
	 * @return the list of entities
	 */
	List<T> findByNamedQuery(
			final String queryName,
			Object... params
			);

	/**
	 * Find using a named query
	 * 
	 * @param queryName
	 *            the name of the query
	 * @param params
	 *            the query parameters
	 * 
	 * @return the list of entities
	 */
	List<T> findByNamedQueryAndNamedParams(
			final String queryName,
			final Map<String, ? extends Object> params
			);

	/**
	 * Count all entities
	 * 
	 * @return the number of entities
	 */
	int countAll();

	/**
	 * Count entities based on an example
	 * 
	 * @param exampleInstance
	 *            the search criteria
	 * @return the number of entities
	 */
	int countByExample(final T exampleInstance);

	/**
	 * save an entity. This can be either a INSERT or UPDATE in the database
	 * 
	 * @param entity
	 *            the entity to save
	 * 
	 * @return the saved entity
	 */
	T save(final T entity);

	/**
	 * delete an entity from the database
	 * 
	 * @param entity
	 *            the entity to delete
	 */
	void delete(final T entity);
}
