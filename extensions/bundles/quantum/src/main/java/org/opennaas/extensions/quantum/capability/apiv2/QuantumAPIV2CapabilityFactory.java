package org.opennaas.extensions.quantum.capability.apiv2;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class QuantumAPIV2CapabilityFactory extends AbstractCapabilityFactory {

	@Override
	public ICapability create(IResource resource) throws CapabilityException {

		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(QuantumAPIV2Capability.CAPABILITY_TYPE),
				resource.getResourceDescriptor().getId());
		capability.setResource(resource);
		return capability;
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {

		return new QuantumAPIV2Capability(capabilityDescriptor, resourceId);
	}

}
