package org.opennaas.extensions.vnmapper.capability.example;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public interface IExampleCapability extends ICapability {

	/**
	 * @return
	 * @throws CapabilityException
	 */
	public MappingResult sayHello(VNTRequest request) throws CapabilityException;

}
