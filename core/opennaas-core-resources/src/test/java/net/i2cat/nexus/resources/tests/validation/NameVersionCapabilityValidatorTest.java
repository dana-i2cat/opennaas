package net.i2cat.nexus.resources.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.CapabilityProperty;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.validation.NameVersionCapabilityPropertyValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Test that the NameVersionCapabilityValidator works properly
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class NameVersionCapabilityValidatorTest {
	Log										logger					= LogFactory.getLog(NameVersionCapabilityValidatorTest.class);

	NameVersionCapabilityPropertyValidator	validator				= null;
	CapabilityDescriptor					capabilityDescriptor	= null;
	List<CapabilityProperty>				capabilityProperties	= null;
	BindException							errors					= null;

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
