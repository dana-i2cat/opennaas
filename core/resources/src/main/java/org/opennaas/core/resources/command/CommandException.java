package org.opennaas.core.resources.command;

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

import org.opennaas.core.resources.action.ActionException;

public class CommandException extends ActionException {

	/**
	 *
	 */
	private static final long	serialVersionUID	= -4678410903548522139L;

	public CommandException(Exception e) {
		super(e);
	}

	public CommandException() {
		super();
	}

	public CommandException(String msg) {
		super(msg);
	}

	public CommandException(String msg, Exception e) {
		super(msg, e);
	}

}
