package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
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
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#createLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void createLogicalRouter(String resourceId, ComputerSystem logicalRouter) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.createLogicalRouter(logicalRouter);
		} catch (ResourceException e) {
			// TODO
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#deleteLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void deleteLogicalRouter(String resourceId, ComputerSystem logicalRouter) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.deleteLogicalRouter(logicalRouter);
		} catch (ResourceException e) {
			// TODO
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#upPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void upPhysicalInterface(String resourceId, LogicalPort iface) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.upPhysicalInterface(iface);
		} catch (ResourceException e) {
			// TODO
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#downPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void downPhysicalInterface(String resourceId, LogicalPort iface) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.downPhysicalInterface(iface);
		} catch (ResourceException e) {
			// TODO
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#setEncapsulationLabel(java.lang.String,
	 * org.opennaas.extensions.router.model.LogicalPort, java.lang.String)
	 */
	@Override
	public void setEncapsulationLabel(String resourceId, LogicalPort iface, String encapsulationLabel) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.setEncapsulationLabel(iface, encapsulationLabel);
		} catch (ResourceException e) {
			// TODO
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#addInterfacesToLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem, java.util.List)
	 */
	@Override
	public void addInterfacesToLogicalRouter(String resourceId, ComputerSystem logicalRouter, List<LogicalPort> interfaces) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.addInterfacesToLogicalRouter(logicalRouter, interfaces);
		} catch (ResourceException e) {
			// TODO
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#removeInterfacesFromLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem, java.util.List)
	 */
	@Override
	public void removeInterfacesFromLogicalRouter(String resourceId, ComputerSystem logicalRouter, List<LogicalPort> interfaces) {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CHASSIS);
			iChassisCapability.removeInterfacesFromLogicalRouter(logicalRouter, interfaces);
		} catch (ResourceException e) {
			// TODO
		}
	}

}
