package org.opennaas.extensions.router.capability.bgp;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.BGPService;

public interface IBGPCapability extends ICapability {

	public void configureBGP(BGPService serviceConfiguration) throws CapabilityException;

	public void unconfigureBGP(BGPService serviceConfiguration) throws CapabilityException;

}
