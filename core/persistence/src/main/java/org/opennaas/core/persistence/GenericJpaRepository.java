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
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.openjpa.persistence.criteria.OpenJPACriteriaBuilder;
import org.springframework.beans.factory.annotation.Required;

/**
 * JPA implementation of the GenericRepository. Note that this implementation also expects OpenJPA as JPA implementation. That's because we use the
 * Criteria API.
 * 
 * @author Jurgen Lust
 * 
 * @param <T>
 *            The persistent type
 * @param <ID>
 *            The primary key type
 */
public class GenericJpaRepository<T, ID extends Serializable>
		implements GenericRepository<T, ID>
{
	private final Class<T>	entityClass;
	private EntityManager	entityManager;

	public GenericJpaRepository(final Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@SuppressWarnings("unchecked")
	public GenericJpaRepository() {
		entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	private OpenJPACriteriaBuilder getCriteriaBuilder() {
		return (OpenJPACriteriaBuilder) entityManager.getCriteriaBuilder();
	}

	@Override
	public int countAll() {
		OpenJPACriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<T> entities = query.from(entityClass);
		query.select(builder.count(entities));

		return (int) (long) entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public int countByExample(final T exampleInstance) {
		OpenJPACriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<T> entities = query.from(entityClass);
		query.where(builder.qbe(entities, exampleInstance));
		query.select(builder.count(entities));

		return (int) (long) entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public List<T> findAll() {
		OpenJPACriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);

		query.from(entityClass);

		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public List<T> findByExample(final T exampleInstance) {
		OpenJPACriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);

		Root<T> entities = query.from(entityClass);
		query.where(builder.qbe(entities, exampleInstance));

		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public T findById(final ID id) {
		return entityManager.find(entityClass, id);
	}

	@Override
	public List<T> findByNamedQuery(final String name, Object... params) {
		TypedQuery<T> query = entityManager.createNamedQuery(name, entityClass);

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}

		return query.getResultList();
	}

	@Override
	public List<T> findByNamedQueryAndNamedParams(final String name,
			final Map<String, ? extends Object> params)
	{
		TypedQuery<T> query = entityManager.createNamedQuery(name, entityClass);

		for (Map.Entry<String, ? extends Object> param : params.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}

		return query.getResultList();
	}

	/**
	 * @see be.bzbit.framework.domain.repository.GenericRepository#getEntityClass()
	 */
	@Override
	public Class<T> getEntityClass() {
		return entityClass;
	}

	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}

	@Override
	public T save(T entity) {
		return entityManager.merge(entity);
	}

	@Required
	@PersistenceContext
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
