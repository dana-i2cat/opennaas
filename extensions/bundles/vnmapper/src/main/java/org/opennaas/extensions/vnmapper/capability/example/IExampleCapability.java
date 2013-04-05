package org.opennaas.extensions.vnmapper.capability.example;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vnmapper.VNTRequest;

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
	public VNMapperOutput sayHello(VNTRequest request) throws CapabilityException;

}
