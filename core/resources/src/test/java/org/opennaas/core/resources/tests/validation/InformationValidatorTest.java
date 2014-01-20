package org.opennaas.core.resources.tests.validation;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.validation.InformationValidator;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

/**
 * Test the InformationValidator class
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class InformationValidatorTest {
	Log						logger		= LogFactory.getLog(InformationValidatorTest.class);

	BindException			errors		= null;
	Information				information	= null;
	InformationValidator	validator	= null;

	@Before
	public void setup() {
		information = new Information();
		validator = new InformationValidator();
		errors = new BindException(information, information.getClass().getName());
	}

	@Test
	public void testValidationSuceeds() {
		information.setName("CapabilityName");
		information.setType("CapabilityType");
		information.setVersion("1.0.0");

		assertTrue(validator.supports(Information.class));
		validator.validate(information, errors);
		assertEquals(0, errors.getErrorCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInformationValidationFails() {
		validator.validate(information, errors);
		assertEquals("field.empty", errors.getFieldError().getCode());
		// print the field errors for debugging purposes
		printFieldErrors(errors.getFieldErrors());
	}

	private void printFieldErrors(List<FieldError> fieldErrors) {
		for (int j = 0; j < fieldErrors.size(); j++) {
			FieldError fieldError = fieldErrors.get(j);
			logger.debug("Error " + j);
			logger.debug("  Effected Field " + fieldError.getField());
			logger.debug("  Rejected Value: " + fieldError.getRejectedValue());
			logger.debug("  Code: " + fieldError.getCode());
			logger.debug("  Default message: " + fieldError.getDefaultMessage());
		}
	}
}
