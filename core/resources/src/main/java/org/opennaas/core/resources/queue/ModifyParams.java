package org.opennaas.core.resources.queue;

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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ModifyParams {
	private int			posAction;

	private Operations	QueueOper;
	private Object		params;

	public enum Operations {
		REMOVE, UP, DOWN
	}

	public int getPosAction() {
		return posAction;
	}

	public void setPosAction(int posAction) {
		this.posAction = posAction;
	}

	public Operations getQueueOper() {
		return QueueOper;
	}

	public void setQueueOper(Operations queueOper) {
		QueueOper = queueOper;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Object params) {
		this.params = params;
	}

	/**
	 * @return the params
	 */
	public Object getParams() {
		return params;
	}

	public static ModifyParams newRemoveOperation(int posAction) {
		ModifyParams removeParams = new ModifyParams();
		removeParams.setPosAction(posAction);
		removeParams.setQueueOper(Operations.REMOVE);
		return removeParams;
	}

}
