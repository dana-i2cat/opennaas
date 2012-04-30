package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService
public class ChassisCapabilityServiceImpl extends GenericCapabilityServiceImpl implements IChassisCapabilityService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#createLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void createLogicalRouter(String resourceId, ComputerSystem logicalRouter) throws CapabilityException {
		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.createLogicalRouter(logicalRouter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#deleteLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void deleteLogicalRouter(String resourceId, ComputerSystem logicalRouter) throws CapabilityException {
		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.deleteLogicalRouter(logicalRouter);
	}

}
