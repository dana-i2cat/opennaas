package net.i2cat.mantychore.core.resources.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import net.i2cat.mantychore.core.resources.descriptor.Information;

/**
 * Validate that the Information Object has the mandatory fields initialized
 * @author Scott Campbell (CRC)
 *
 */
public class InformationValidator implements Validator
{

	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return Information.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors e) {
		ValidationUtils.rejectIfEmpty(e, "type", "field.empty", "type field cannot be empty");
		ValidationUtils.rejectIfEmpty(e, "name", "field.empty", "name field cannot be empty");
		ValidationUtils.rejectIfEmpty(e, "version", "field.empty", "version field cannot be empty");
	}

}
