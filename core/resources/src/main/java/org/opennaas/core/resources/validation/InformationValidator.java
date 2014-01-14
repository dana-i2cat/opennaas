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

import org.opennaas.core.resources.descriptor.Information;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validate that the Information Object has the mandatory fields initialized
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class InformationValidator implements Validator
{

	public boolean supports(Class<?> clazz) {
		return Information.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors e) {
		ValidationUtils.rejectIfEmpty(e, "type", "field.empty", "type field cannot be empty");
		ValidationUtils.rejectIfEmpty(e, "name", "field.empty", "name field cannot be empty");
		ValidationUtils.rejectIfEmpty(e, "version", "field.empty", "version field cannot be empty");
	}

}
