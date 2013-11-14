package org.opennaas.extensions.vrf.capability.routing;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
public class RoutingCapabilityFactory extends AbstractCapabilityFactory {

	/**
	 * Create the capability
	 */
	@Override
	public ICapability create(IResource resource) throws CapabilityException {

		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(RoutingCapability.CAPABILITY_TYPE),
				resource.getResourceDescriptor().getId());
		capability.setResource(resource);
		return capability;
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {

		return new RoutingCapability(capabilityDescriptor, resourceId);
	}

}
