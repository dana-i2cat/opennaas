package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;

/**
 * @author Jordi Puig
 */
@WebService
public interface IChassisCapabilityService {

	/*
	 * Interfaces
	 */

	/**
	 * Activates given physical interface (iface) so it can receive/send traffic.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to activate (must be a physical one)
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void upPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException;

	/**
	 * Deactivates given physical interface (iface) so it can not receive/send traffic.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to deactivate (must be a physical one)
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void downPhysicalInterface(String resourceId, LogicalPort iface) throws CapabilityException;

	// /**
	// * Creates given logical interface (iface).
	// *
	// * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	// * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device
	// queue
	// * is required for queued actions to take effect.
	// *
	// * @param iface
	// * to be created
	// * @throws CapabilityException
	// * if any error occurred. In that case, queue remains untouched.
	// */
	// public void createSubInterface(String resourceId, NetworkPort iface) throws CapabilityException;
	//
	// /**
	// * Deletes given logical interface (iface).
	// *
	// * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	// * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device
	// queue
	// * is required for queued actions to take effect.
	// *
	// * @param iface
	// * to be deleted
	// * @throws CapabilityException
	// * if any error occurred. In that case, queue remains untouched.
	// */
	// public void deleteSubInterface(String resourceId, NetworkPort iface) throws CapabilityException;
	//
	// /**
	// * Configures the type of encapsulation to use in given iface.
	// *
	// * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	// * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device
	// queue
	// * is required for queued actions to take effect.
	// *
	// * @param iface
	// * to be configured
	// * @param encapsulationType
	// * to use in given iface
	// * @throws CapabilityException
	// * if any error occurred. In that case, queue remains untouched.
	// */
	// public void setEncapsulation(String resourceId, LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException;

	/**
	 * Configures the encapsulation label to use in given iface.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to use given label
	 * @param encapsulationLabel
	 *            to use in given iface
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void setEncapsulationLabel(String resourceId, LogicalPort iface, String encapsulationLabel) throws CapabilityException;

	/*
	 * Logical Routers
	 */
	/**
	 * Creates a logical router.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param logicalRouter
	 *            to be created
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void createLogicalRouter(String resourceId, ComputerSystem logicalRouter) throws CapabilityException;

	/**
	 * Deletes given logical router.
	 * 
	 * @param logicalRouter
	 *            existing logical router to delete.
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void deleteLogicalRouter(String resourceId, ComputerSystem logicalRouter) throws CapabilityException;

	/**
	 * Adds given interfaces to given logical router, thus giving control over them to the logical router.
	 * 
	 * @param logicalRouter
	 *            that will receive the interfaces
	 * @param interfaces
	 *            to be added to the logical router
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void addInterfacesToLogicalRouter(String resourceId, ComputerSystem logicalRouter, List<LogicalPort> interfaces)
			throws CapabilityException;

	/**
	 * Removes given interfaces from given logical router, returning control over them to the physical router.
	 * 
	 * 
	 * @param logicalRouter
	 *            to remove the interfaces from
	 * @param interfaces
	 *            to be removed from the logical router
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void removeInterfacesFromLogicalRouter(String resourceId, ComputerSystem logicalRouter, List<LogicalPort> interfaces)
			throws CapabilityException;

}