package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

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
	public void upPhysicalInterface(String resourceId, LogicalPort iface) {
		// ICapability chassisCapability = getCapability(resourceId, ChassisCapability.CHASSIS);
		// TODO Call the iChassis Capability method.
	}

}
