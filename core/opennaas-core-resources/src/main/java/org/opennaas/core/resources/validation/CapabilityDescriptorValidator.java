package org.opennaas.core.resources.validation;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Basic validation on the CapabilityDescriptor
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class CapabilityDescriptorValidator implements Validator
{
	Errors informationErrors = null;
	Errors descriptorErrors = null;

	public boolean supports(Class<?> clazz) {
		return CapabilityDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * validate the object. This is a conveinence method to calling the validate(Obj, Errors) method of the
	 * Validator interface so the calling class doesn't have to supply it's own Errors object
	 * @param obj
	 */
	public void validate(Object obj) {
		descriptorErrors = new BindException(obj, obj.getClass().getName());
		validate(obj, descriptorErrors);
	}
	
	public void validate(Object obj, Errors e) {
		descriptorErrors = e;
		ValidationUtils.rejectIfEmpty(descriptorErrors, "capabilityInformation", "field.empty",
				"moduleInformation field cannot be empty");

		// Make sure the Information Object is filled out correctly
		Information information = ((CapabilityDescriptor) obj).getCapabilityInformation();
		if (information != null) {
			informationErrors = new BindException(information, information.getClass().getName());
			ValidationUtils.invokeValidator(new InformationValidator(), information,
					informationErrors);
		}
	}

	/**
	 * @return the informationErrors
	 */
	public Errors getInformationErrors() {
		return informationErrors;
	}

	/**
	 * @return the descriptorErrors
	 */
	public Errors getDescriptorErrors() {
		return descriptorErrors;
	}

	public boolean hasErrors() {
		if (descriptorErrors.hasErrors() || informationErrors.hasErrors()) {
			return true;
		}

		else {
			return false;
		}
	}

	public List<Errors> getErrors() {
		List<Errors> list = new ArrayList<Errors>();
		list.add(descriptorErrors);
		if (informationErrors != null) {
			list.add(informationErrors);
		}
		return list;
	}

}