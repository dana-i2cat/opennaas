package net.i2cat.mantychore.core.resources.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.descriptor.CapabilityProperty;
import net.i2cat.mantychore.core.resources.descriptor.Information;
import net.i2cat.mantychore.core.resources.validation.NameVersionCapabilityPropertyValidator;

/**
 * Test that the NameVersionCapabilityValidator works properly
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class NameVersionCapabilityValidatorTest
{
	Logger logger = LoggerFactory.getLogger(NameVersionCapabilityValidatorTest.class);

	NameVersionCapabilityPropertyValidator validator = null;
	CapabilityDescriptor capabilityDescriptor = null;
	List<CapabilityProperty> capabilityProperties = null;
	BindException errors = null;

	@Before
	public void setup() {
		capabilityDescriptor = new CapabilityDescriptor();
		validator = new NameVersionCapabilityPropertyValidator();
		errors = new BindException(capabilityDescriptor, capabilityDescriptor.getClass().getName());
	}

	@Test
	public void testValidationSuceeds() {
		capabilityDescriptor.setCapabilityInformation(new Information("capabilityType", "capabilityName", "1.0.0"));
		CapabilityProperty nameProperty = new CapabilityProperty("name", "capabilityName");
		capabilityDescriptor.getCapabilityProperties().add(nameProperty);
		CapabilityProperty versionProperty = new CapabilityProperty("version", "1.0.0");
		capabilityDescriptor.getCapabilityProperties().add(versionProperty);

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

	@SuppressWarnings("unchecked")
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
