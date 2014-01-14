package org.opennaas.core.resources.action;

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

public interface IActionSet {

	public String getActionSetId();

	public void setActionSetId(String actionSetId);

	public List<String> getActionNames();

	/**
	 * Returns the name of the action that is called to start up capabilities using actions in this actionset. This action should update the model
	 * allowing all actions in this actionSet to be operative. An action with this signature should be available through
	 * obtainAction(getRefreshActionName()), unless getStartUpRefreshActionName() returns null
	 */
	public List<String> getRefreshActionName();

	/**
	 * 
	 * @param actionId
	 * @return Action with given actionId present in this actionSet, or null if there is no action with given Id
	 * @throws ActionException
	 *             if there was a problem instantiating the action
	 */
	public Action obtainAction(String actionId) throws ActionException;
}
