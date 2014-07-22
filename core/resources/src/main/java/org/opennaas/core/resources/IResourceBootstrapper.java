package org.opennaas.core.resources;

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

/**
 * This interface is used to implement the bootstrapping for a resource.
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public interface IResourceBootstrapper {

	/**
	 * calls an operation on the first capability in the execution workflow to initialize the resource. This is usually a Query action.
	 * 
	 * @throws ResourceException
	 */
	public void bootstrap(Resource resource) throws ResourceException;

	/**
	 * Reverts bootstrap operation, leaving given resource in the same state as if bootstrap had never been called.
	 * 
	 * @param resource
	 * @throws ResourceException
	 */
	public void revertBootstrap(Resource resource) throws ResourceException;

	public void resetModel(Resource resource) throws ResourceException;

}
