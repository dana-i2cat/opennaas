package org.opennaas.extensions.vcpe.capability.builder;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public interface IVCPENetworkBuilder extends ICapability {

	public VCPENetworkModel buildVCPENetwork(VCPENetworkModel desiredScenario) throws CapabilityException;

	public void destroyVCPENetwork() throws CapabilityException;

}
