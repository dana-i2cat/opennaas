package org.opennaas.core.resources.capability;

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

import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;

public interface ICapabilityLifecycle extends ICapability {

	/**
	 * Provide a status for the resource instance.
	 */
	public enum State {
		INSTANTIATED, INITIALIZED, ACTIVE, INACTIVE, SHUTDOWN, ERROR
	};

	/**
	 * Returns the current resource state
	 * 
	 * @return state enum object
	 */
	public State getState();

	/**
	 * Initializes this component, the status will be INITIALIZED.
	 * 
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during initialization
	 */
	public void initialize() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

	/**
	 * Activates this component and change state to ACTIVE.
	 * 
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during activation
	 */
	public void activate() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

	/**
	 * Deactivate this component and change state to INACTIVE
	 * 
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during deactivation
	 */
	public void deactivate() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

	/**
	 * Prepares component for Garbage Collection state will be SHUTDOWN until it is collected.
	 * 
	 * @throws IncorrectLifecycleStateException
	 *             if this method is not allowed in current state
	 * @throws ResourceException
	 *             if there is a problem during shutting down
	 */
	public void shutdown() throws IncorrectLifecycleStateException, ResourceException, CorruptStateException;

}
