package org.opennaas.extensions.router.capability.ospfv3;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * @author Jordi Puig
 */
public class OSPFv3CapabilityFactory extends AbstractCapabilityFactory {

	/**
	 * Create the capability
	 */
	@Override
	public ICapability create(IResource resource) throws CapabilityException {

		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(OSPFv3Capability.CAPABILITY_TYPE),
				resource.getResourceDescriptor().getId());
		capability.setResource(resource);
		return capability;
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {

		return new OSPFv3Capability(capabilityDescriptor, resourceId);
	}

}
