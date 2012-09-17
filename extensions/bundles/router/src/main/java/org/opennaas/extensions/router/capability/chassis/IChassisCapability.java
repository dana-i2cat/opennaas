package org.opennaas.extensions.router.capability.chassis;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.wrappers.AddInterfacesToLogicalRouterRequest;
import org.opennaas.extensions.router.model.wrappers.RemoveInterfacesFromLogicalRouterRequest;
import org.opennaas.extensions.router.model.wrappers.SetEncapsulationLabelRequest;
import org.opennaas.extensions.router.model.wrappers.SetEncapsulationRequest;

public interface IChassisCapability extends ICapability {

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
	@POST
	@Path("/upPhysicalInterface")
	@Consumes(MediaType.APPLICATION_XML)
	public void upPhysicalInterface(LogicalPort iface) throws CapabilityException;

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
	@POST
	@Path("/downPhysicalInterface")
	@Consumes(MediaType.APPLICATION_XML)
	public void downPhysicalInterface(LogicalPort iface) throws CapabilityException;

	/**
	 * Creates given logical interface (iface).
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to be created
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/createSubInterface")
	@Consumes(MediaType.APPLICATION_XML)
	public void createSubInterface(NetworkPort iface) throws CapabilityException;

	/**
	 * Deletes given logical interface (iface).
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to be deleted
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/deleteSubInterface")
	@Consumes(MediaType.APPLICATION_XML)
	public void deleteSubInterface(NetworkPort iface) throws CapabilityException;

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
	@POST
	@Path("/createLogicalRouter")
	@Consumes(MediaType.APPLICATION_XML)
	public void createLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException;

	/**
	 * Deletes given logical router.
	 * 
	 * @param logicalRouter
	 *            existing logical router to delete.
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/deleteLogicalRouter")
	@Consumes(MediaType.APPLICATION_XML)
	public void deleteLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException;

	/**
	 * Adds given interfaces to given logical router, thus giving control over them to the logical router.
	 * 
	 * @param request
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/addInterfacesToLogicalRouter")
	@Consumes(MediaType.APPLICATION_XML)
	public void addInterfacesToLogicalRouter(AddInterfacesToLogicalRouterRequest request) throws CapabilityException;

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
	public void addInterfacesToLogicalRouter(ComputerSystem logicalRouter, List<LogicalPort> interfaces) throws CapabilityException;

	/**
	 * Removes given interfaces from given logical router, returning control over them to the physical router.
	 * 
	 * 
	 * @param request
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/removeInterfacesFromLogicalRouter")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeInterfacesFromLogicalRouter(RemoveInterfacesFromLogicalRouterRequest request) throws CapabilityException;

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
	public void removeInterfacesFromLogicalRouter(ComputerSystem logicalRouter, List<LogicalPort> interfaces) throws CapabilityException;

	/**
	 * Configures the type of encapsulation to use in given iface.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param request
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/setEncapsulation")
	@Consumes(MediaType.APPLICATION_XML)
	public void setEncapsulation(SetEncapsulationRequest request) throws CapabilityException;

	/**
	 * Configures the type of encapsulation to use in given iface.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to be configured
	 * @param encapsulationType
	 *            to use in given iface
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void setEncapsulation(LogicalPort iface, ProtocolIFType encapsulationType) throws CapabilityException;

	/**
	 * Configures the encapsulation label to use in given iface.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param request
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@POST
	@Path("/setEncapsulationLabel")
	@Consumes(MediaType.APPLICATION_XML)
	public void setEncapsulationLabel(SetEncapsulationLabelRequest request) throws CapabilityException;

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
	public void setEncapsulationLabel(LogicalPort iface, String encapsulationLabel) throws CapabilityException;

}
