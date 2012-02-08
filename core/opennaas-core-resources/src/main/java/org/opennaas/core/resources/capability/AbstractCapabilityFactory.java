package org.opennaas.core.resources.capability;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.validation.CapabilityDescriptorValidator;
import org.opennaas.core.resources.validation.ValidationException;

/**
 * The AbstractCapabilityFactory is responsible for instantiating and initializing new capabilities for a resource
 *
 * @author Mathieu Lemay
 *
 */
public abstract class AbstractCapabilityFactory implements ICapabilityFactory {
	/**
	 * Validator object used to ensure that the capability descriptor has the required information in it
	 */
	protected CapabilityDescriptorValidator	validator	= null;

	Log										log			= LogFactory.getLog(AbstractCapabilityFactory.class);

	// FIXME Other method to has identified a factory??
	private String							type		= "";

	public ICapability create(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		ICapability capability = null;

		try {
			log.debug("Validating capability descriptor...");
			doCapabilityDescriptorValidation(capabilityDescriptor);
		} catch (ValidationException ex) {
			throw new CapabilityException("Error creating capability", ex);
		}

		log.debug("Creating new Capability...");
		capability = createCapability(capabilityDescriptor, resourceId);
		log.debug("Created capability!");

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}