package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService
public class ChassisCapabilityServiceImpl extends GenericCapabilityServiceImpl implements IChassisCapabilityService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#upPhysicalInterface(java.lang.String,
	 * org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void upPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException {
		try {
			ICapability chassisCapability = getCapability(resourceId, ChassisCapability.CHASSIS);
			// chassisCapability.sendMessage(arg0, arg1);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}

	}

}
