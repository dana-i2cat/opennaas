package net.i2cat.mantychore.models.internal.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import net.i2cat.mantychore.models.RouterModel;

public class RouterModelValidator implements Validator {

	public boolean supports(Class clazz) {
		if (clazz.equals(RouterModel.class))
			return true;

		return false;
	}

	public void validate(Object obj, Errors errors) {
		// TODO Auto-generated method stub
	}
}
