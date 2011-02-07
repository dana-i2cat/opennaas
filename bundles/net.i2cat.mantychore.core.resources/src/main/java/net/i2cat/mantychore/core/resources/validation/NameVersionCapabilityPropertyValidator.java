package net.i2cat.mantychore.core.resources.validation;

import org.springframework.validation.Errors;

import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;

/**
 * Ensure that the CapabilityDescriptor has a name and a version CapabilityProperty in
 * the properties list
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class NameVersionCapabilityPropertyValidator extends CapabilityDescriptorValidator
{

	@Override
	public void validate(Object obj, Errors e) {
		super.validate(obj, e);

		CapabilityDescriptor descriptor = (CapabilityDescriptor) obj;
		if (descriptor.getProperty("name") == null) {
			descriptorErrors.rejectValue("capabilityProperties", "property.missing",
					"name property missing in CapabilityProperties list");
		}
		if (descriptor.getProperty("version") == null) {
			descriptorErrors.rejectValue("capabilityProperties", "property.missing",
					"version property missing in CapabilityProperties list");
		}
	}
}
