package net.i2cat.nexus.resources.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.validation.InformationValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
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
			FieldError fieldError = (FieldError) fieldErrors.get(j);
			logger.debug("Error " + j);
			logger.debug("  Effected Field " + fieldError.getField());
			logger.debug("  Rejected Value: " + fieldError.getRejectedValue());
			logger.debug("  Code: " + fieldError.getCode());
			logger.debug("  Default message: " + fieldError.getDefaultMessage());
		}
	}
}
