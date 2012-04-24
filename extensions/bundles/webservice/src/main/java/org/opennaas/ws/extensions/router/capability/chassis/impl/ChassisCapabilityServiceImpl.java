package org.opennaas.ws.extensions.router.capability.chassis.impl;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService
public class ChassisCapabilityServiceImpl implements IChassisCapabilityService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * 
	 * org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#upPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort
	 * )
	 */
	@Override
	public void upPhysicalInterface(LogicalPort iface) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#downPhysicalInterface(org.opennaas.extensions.router.model.
	 * LogicalPort)
	 */
	@Override
	public void downPhysicalInterface(LogicalPort iface) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#createSubInterface(org.opennaas.extensions.router.model.NetworkPort
	 * )
	 */
	@Override
	public void createSubInterface(NetworkPort iface) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#deleteSubInterface(org.opennaas.extensions.router.model.NetworkPort
	 * )
	 */
	@Override
	public void deleteSubInterface(NetworkPort iface) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#setEncapsulation(org.opennaas.extensions.router.model.LogicalPort
	 * , org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType)
	 */
	@Override
	public void setEncapsulation(LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#setEncapsulationLabel(org.opennaas.extensions.router.model.
	 * LogicalPort, java.lang.String)
	 */
	@Override
	public void setEncapsulationLabel(LogicalPort iface, String encapsulationLabel) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#createLogicalRouter(org.opennaas.extensions.router.model.
	 * ComputerSystem)
	 */
	@Override
	public void createLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#deleteLogicalRouter(org.opennaas.extensions.router.model.
	 * ComputerSystem)
	 */
	@Override
	public void deleteLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#addInterfacesToLogicalRouter(org.opennaas.extensions.router.
	 * model.ComputerSystem, java.util.List)
	 */
	@Override
	public void addInterfacesToLogicalRouter(ComputerSystem logicalRouter, List<LogicalPort> interfaces) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * org.opennaas.ws.extensions.router.capability.chassis.IChassisCapabilityService#removeInterfacesFromLogicalRouter(org.opennaas.extensions.router
	 * .model.ComputerSystem, java.util.List)
	 */
	@Override
	public void removeInterfacesFromLogicalRouter(ComputerSystem logicalRouter, List<LogicalPort> interfaces) throws CapabilityException {
		// TODO Auto-generated method stub

	}

}
