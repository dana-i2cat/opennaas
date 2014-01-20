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

import org.opennaas.core.resources.ILifecycle.State;

/**
 * Indicates an operation has not been executed because target is not in an appropiated state.
 * 
 * @author isart
 */
public class IncorrectLifecycleStateException extends Exception {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	State						resourceState;

	public IncorrectLifecycleStateException() {
		super();
	}

	public IncorrectLifecycleStateException(String msg) {
		super(msg);
	}

	public IncorrectLifecycleStateException(String msg, State state) {
		super(msg);
		this.resourceState = state;
	}

	public IncorrectLifecycleStateException(String msg, State state, Throwable cause) {
		super(msg, cause);
		this.resourceState = state;
	}

	public State getResourceState() {
		return resourceState;
	}

	public void setResourceState(State resourceState) {
		this.resourceState = resourceState;
	}
}
