package org.opennaas.extensions.vnmapper.capability.vnmapping;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vnmapper.VNTRequest;

/**
 * 
 * @author Elisabeth Rigol
 * @author Adrian Rosello
 * 
 */
public interface IVNMappingCapability extends ICapability {

	/**
	 * @return
	 * @throws CapabilityException
	 */
	public VNMapperOutput mapVN(String resourceId, VNTRequest request) throws CapabilityException;

}
