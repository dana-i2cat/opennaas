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

import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;

public interface IQueueingCapability extends ICapability {

	/**
	 * Creates an action given its id.
	 * 
	 * @param actionId
	 * @return
	 * @throws CapabilityException
	 */
	public IAction createAction(String actionId) throws CapabilityException;

	/**
	 * Adds given action at the end of the queue.
	 * 
	 * @param action
	 *            to put in the queue
	 * @throws CapabilityException
	 *             if failed to put action in the queue
	 */
	public void queueAction(IAction action) throws CapabilityException;

	/**
	 * Sends to the queue all actions required to refresh this the model required for this capability.
	 * 
	 * @throws CapabilityException
	 */
	public void sendRefreshActions() throws CapabilityException;

	/**
	 * Returns the list of actions ids that are available for this capability.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public IActionSet getActionSet() throws CapabilityException;

}
