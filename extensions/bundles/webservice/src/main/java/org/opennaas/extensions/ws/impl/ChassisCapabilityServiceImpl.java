package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#upPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void upPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException {
		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.upPhysicalInterface(iface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#downPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void downPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException {
		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.downPhysicalInterface(iface);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#setEncapsulationLabel(java.lang.String,
	 * org.opennaas.extensions.router.model.LogicalPort, java.lang.String)
	 */
	@Override
	public void setEncapsulationLabel(String resourceId, LogicalPort iface, String encapsulationLabel) throws CapabilityException {

		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.setEncapsulationLabel(iface, encapsulationLabel);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#addInterfacesToLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem, java.util.List)
	 */
	@Override
	public void addInterfacesToLogicalRouter(String resourceId, ComputerSystem logicalRouter, List<LogicalPort> interfaces)
			throws CapabilityException {
		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.addInterfacesToLogicalRouter(logicalRouter, interfaces);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#removeInterfacesFromLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem, java.util.List)
	 */
	@Override
	public void removeInterfacesFromLogicalRouter(String resourceId, ComputerSystem logicalRouter, List<LogicalPort> interfaces)
			throws CapabilityException {
		IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
		iChassisCapability.removeInterfacesFromLogicalRouter(logicalRouter, interfaces);

	}

	// @Override
	// public void createSubInterface(String resourceId, NetworkPort iface) throws CapabilityException {
	// IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
	// iChassisCapability.createSubInterface(iface);
	// }
	//
	// @Override
	// public void deleteSubInterface(String resourceId, NetworkPort iface) throws CapabilityException {
	// IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
	// iChassisCapability.deleteSubInterface(iface);
	// }
	//
	// @Override
	// public void setEncapsulation(String resourceId, LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException {
	// IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, ChassisCapability.CAPABILITY_TYPE);
	// iChassisCapability.setEncapsulation(iface, encapsulationType);
	// }

}
