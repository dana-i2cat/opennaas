package org.opennaas.extensions.router.capability.vrrp;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;

public class VRRPCapability implements IVRRPCapability {
	
	public static final String	CAPABILITY_TYPE	= "vrrp";

	public VRRPCapability(CapabilityDescriptor capabilityDescriptor, String resourceId) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getCapabilityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CapabilityDescriptor getCapabilityDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCapabilityDescriptor(CapabilityDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public Information getCapabilityInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResource(IResource resource) {
		// TODO Auto-generated method stub

	}

}
