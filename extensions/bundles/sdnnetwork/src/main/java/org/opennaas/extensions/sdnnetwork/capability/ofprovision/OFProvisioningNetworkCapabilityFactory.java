package org.opennaas.extensions.sdnnetwork.capability.ofprovision;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapabilityFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingCapability;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class OFProvisioningNetworkCapabilityFactory extends AbstractCapabilityFactory {
	
	@Override
	public ICapability create(IResource resource) throws CapabilityException {
		ICapability capability = this.create(resource.getResourceDescriptor().getCapabilityDescriptor(OFProvisioningNetworkCapability.CAPABILITY_TYPE),
				resource.getResourceDescriptor().getId());
		capability.setResource(resource);
		return capability;
	}

	@Override
	public ICapability createCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) throws CapabilityException {
		return new OFProvisioningNetworkCapability(capabilityDescriptor, resourceId);
	}

}
