package org.opennaas.extensions.sampleresource.capability.example;

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
	public String sayHello(String userName) throws CapabilityException;

}
