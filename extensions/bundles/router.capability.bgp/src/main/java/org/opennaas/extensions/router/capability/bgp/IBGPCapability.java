package org.opennaas.extensions.router.capability.bgp;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.ComputerSystem;

public interface IBGPCapability extends ICapability {

	public void configureBGP(ComputerSystem systemWithBGPAndPolicies) throws CapabilityException;

	public void unconfigureBGP(ComputerSystem systemWithBGPAndPolicies) throws CapabilityException;

}
