package net.i2cat.mantychore.core.resources.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import net.i2cat.mantychore.core.resources.descriptor.Information;
import net.i2cat.mantychore.core.resources.validation.InformationValidator;

/**
 * Test the InformationValidator class
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class InformationValidatorTest
{
	Logger logger = LoggerFactory.getLogger(InformationValidatorTest.class);

	BindException errors = null;
	Information information = null;
	InformationValidator validator = null;

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
			FieldError fieldError = (FieldError) fieldErrors.get(j);
			logger.debug("Error " + j);
			logger.debug("  Effected Field " + fieldError.getField());
			logger.debug("  Rejected Value: " + fieldError.getRejectedValue());
			logger.debug("  Code: " + fieldError.getCode());
			logger.debug("  Default message: " + fieldError.getDefaultMessage());
		}
	}
}
