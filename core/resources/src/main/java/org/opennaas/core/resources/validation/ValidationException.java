package org.opennaas.core.resources.validation;

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

import org.opennaas.core.resources.ResourceException;
import org.springframework.validation.Errors;

/**
 * @author Scott Campbell (CRC)
 * 
 */
public class ValidationException extends ResourceException
{
	private static final long	serialVersionUID	= 2151457358047960610L;
	private List<Errors>		errors;

	public ValidationException(List<Errors> errors) {
		super();
		this.errors = errors;
	}

	public List<Errors> getErrors() {
		return errors;
	}
}
