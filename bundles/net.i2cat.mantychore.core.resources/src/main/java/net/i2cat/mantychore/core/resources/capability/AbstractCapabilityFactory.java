package net.i2cat.mantychore.core.resources.capability;

import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.validation.CapabilityDescriptorValidator;
import net.i2cat.mantychore.core.resources.validation.ValidationException;

/**
 * The AbstractCapabilityFactory is responsible for instantiating and
 * initializing new capabilities for a resource
 * 
 * @author Mathieu Lemay
 * 
 */
public abstract class AbstractCapabilityFactory implements ICapabilityFactory{
	/**
	 * Validator object used to ensure that the capability descriptor has the
	 * required information in it
	 */
	protected CapabilityDescriptorValidator validator = null;

	public ICapability create(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		ICapability capability = null;

		try{
			doCapabilityDescriptorValidation(capabilityDescriptor);
		}catch(ValidationException ex){
			throw new CapabilityException("Error creating capability", ex);
		}

		capability = createCapability(capabilityDescriptor, resourceId);

		return capability;
	}

	private void doCapabilityDescriptorValidation(CapabilityDescriptor capabilityDescriptor)
			throws ValidationException {

		if (validator != null) {
			validator.validate(capabilityDescriptor);

			if (validator.hasErrors()) {
				throw new ValidationException(validator.getErrors());
			}
		}
	}
	
	/**
	 * Creates a capability based on a capability descriptor and returns it.
	 * 
	 * @param capabilityDescriptor
	 * @throws capabilityException
	 */
	public abstract ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId)
			throws CapabilityException;

	/**
	 * @param capabilityDescriptorValidator
	 *            the capabilityDescriptorValidator to set
	 */
	public void setCapabilityDescriptorValidator(CapabilityDescriptorValidator validator) {
		this.validator = validator;
	}
}