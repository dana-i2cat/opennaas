package org.opennaas.ws.extensions.router.capability.chassis.impl;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService
public class ChassisCapabilityServiceImpl implements IChassisCapabilityService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#upPhysicalInterface(java.lang.String,
	 * org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void upPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException {
		System.out.println("Test");
	}

}
