package org.opennaas.extensions.vnmapper.capability.vnmapping;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vnmapper.VNTRequest;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public interface IVNMappingCapability extends ICapability {

	/**
	 * @return
	 * @throws CapabilityException
	 */
	public VNMapperOutput mapVN(VNTRequest request) throws CapabilityException;

}
