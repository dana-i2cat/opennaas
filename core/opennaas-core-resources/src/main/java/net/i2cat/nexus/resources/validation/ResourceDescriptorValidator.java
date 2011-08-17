package net.i2cat.nexus.resources.validation;

import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * Ensures that the resource descriptor is valid
 * @author Ali LAHLOU (Synchromedia, ETS)
 *
 */
public class ResourceDescriptorValidator
{
	
	/**
	 * Validate if a resource descriptor is valid
	 * @param ResourceDescriptor The Resource descriptor that has to be validated
	 * @return True if valid, False if not
	 */
	public static boolean validateDescriptor(ResourceDescriptor resourceConfiguration){
		
		boolean result = true;
		
		Information inf = resourceConfiguration.getInformation();
		Errors err = new BindException( inf, "inf");
		InformationValidator infValidator = new InformationValidator();
		infValidator.validate( inf, err );
		result = !err.hasErrors();
		
		CapabilityDescriptorValidator capValidator = new CapabilityDescriptorValidator();
		List<CapabilityDescriptor> capabilityDescriptors = resourceConfiguration.getCapabilityDescriptors();
		for(int i=0; i<capabilityDescriptors.size(); i++){
			capValidator.validate( capabilityDescriptors.get(i) );
			result = result && !capValidator.hasErrors();
		}
		
		return result;
	}

}