package org.opennaas.extensions.ws.impl;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.ws.services.IChassisCapabilityService;

/**
 * @author Jordi Puig
 */
@WebService(portName = "ChassisCapabilityPort", serviceName = "ChassisCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class ChassisCapabilityServiceImpl extends GenericCapabilityService implements IChassisCapabilityService {

	Log	log	= LogFactory.getLog(ChassisCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#createLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void createLogicalRouter(String resourceId, ComputerSystem logicalRouter) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.createLogicalRouter(logicalRouter);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#deleteLogicalRouter(java.lang.String,
	 * org.opennaas.extensions.router.model.ComputerSystem)
	 */
	@Override
	public void deleteLogicalRouter(String resourceId, ComputerSystem logicalRouter) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.deleteLogicalRouter(logicalRouter);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#upPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void upPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.upPhysicalInterface(iface);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#downPhysicalInterface(org.opennaas.extensions.router.model.LogicalPort)
	 */
	@Override
	public void downPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.downPhysicalInterface(iface);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IChassisCapabilityService#setEncapsulationLabel(java.lang.String,
	 * org.opennaas.extensions.router.model.LogicalPort, java.lang.String)
	 */
	@Override
	public void setEncapsulationLabel(String resourceId, LogicalPort iface, String encapsulationLabel) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.setEncapsulationLabel(iface, encapsulationLabel);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
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
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.addInterfacesToLogicalRouter(logicalRouter, interfaces);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
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
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.removeInterfacesFromLogicalRouter(logicalRouter, interfaces);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}

	}

	@Override
	public void createSubInterface(String resourceId, NetworkPort iface) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.createSubInterface(iface);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	@Override
	public void deleteSubInterface(String resourceId, NetworkPort iface) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.deleteSubInterface(iface);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

	@Override
	public void setEncapsulation(String resourceId, LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException {
		try {
			IChassisCapability iChassisCapability = (IChassisCapability) getCapability(resourceId, IChassisCapability.class);
			iChassisCapability.setEncapsulation(iface, encapsulationType);
		} catch (CapabilityException e) {
			log.error(e);
			throw e;
		}
	}

}
