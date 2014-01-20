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

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

/**
 * Basic interface all Action must implements
 * 
 * @author Evelyn Torras
 * 
 */
public interface IAction {

	/**
	 *
	 */
	public IModel getModelToUpdate();

	/**
	 *
	 */

	public void setModelToUpdate(IModel modelToUpdate);

	/**
	 *
	 */
	public Object getParams();

	/**
	 *
	 */
	public void setParams(Object params);

	/**
	 *
	 */
	public String getActionID();

	/**
	 *
	 */
	public void setActionID(String actionID);

	/**
	 *
	 */
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException;

	/**
	 * What it is the utility for this method
	 * 
	 * @param params
	 * @return
	 * @throws ActionException
	 */
	public boolean checkParams(Object params) throws ActionException;
}
