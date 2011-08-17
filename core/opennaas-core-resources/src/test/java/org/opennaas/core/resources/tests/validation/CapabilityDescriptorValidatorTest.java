package org.opennaas.core.resources.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.validation.CapabilityDescriptorValidator;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Test the ModuleDescriptorValidator class
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class CapabilityDescriptorValidatorTest {
	Log								logger					= LogFactory.getLog(CapabilityDescriptorValidatorTest.class);

	CapabilityDescriptorValidator	validator				= null;
	CapabilityDescriptor			capabilityDescriptor	= null;
	List<CapabilityProperty>		capabilityProperties	= null;
	BindException					errors					= null;

	@Before
	public void setup() {
		capabilityDescriptor = new CapabilityDescriptor();
		capabilityProperties = new ArrayList<CapabilityProperty>();
		validator = new CapabilityDescriptorValidator();
		errors = new BindException(capabilityDescriptor, capabilityDescriptor.getClass().getName());
	}

	@Test
	public void testValidationSuceeds() {
		capabilityDescriptor.setCapabilityInformation(new Information("capabilityType", "CapabilityName", "1.0.0"));
		capabilityDescriptor.setCapabilityProperties(capabilityProperties);

		assertTrue(validator.supports(CapabilityDescriptor.class));
		validator.validate(capabilityDescriptor, errors);
		assertFalse(validator.hasErrors());
	}

	@Test
	public void testModuleDescriptorValidationFails() {
		validator.validate(capabilityDescriptor, errors);
		assertTrue(validator.hasErrors());
		// print the field errors for debugging purposes
		printFieldErrors(validator.getErrors());
	}

	private void printFieldErrors(List<Errors> errors) {
		for (int i = 0; i < errors.size(); i++) {
			List<FieldError> fieldErrors = errors.get(i).getFieldErrors();
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
}