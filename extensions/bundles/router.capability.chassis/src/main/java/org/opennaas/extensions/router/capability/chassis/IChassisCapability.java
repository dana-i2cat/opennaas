package org.opennaas.extensions.router.capability.chassis;

/*
 * #%L
 * OpenNaaS :: Router :: Chassis Capability
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfoList;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capabilities.api.model.chassis.LogicalRoutersNamesList;
import org.opennaas.extensions.router.capabilities.api.model.chassis.SetEncapsulationLabelRequest;
import org.opennaas.extensions.router.capabilities.api.model.chassis.SetEncapsulationRequest;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;

public interface IChassisCapability extends ICapability {

	/*
	 * Interfaces
	 */

	/**
	 * Returns a list of interfaces names
	 * 
	 * @return
	 */
	@GET
	@Path("/interfaces")
	@Produces(MediaType.APPLICATION_XML)
	public InterfacesNamesList getInterfacesNames() throws CapabilityException;

	/**
	 * Returns interfaces info of a given interface
	 * 
	 * @return
	 */
	@GET
	@Path("/interfaces/info")
	@Produces(MediaType.APPLICATION_XML)
	public InterfaceInfo getInterfaceInfo(@QueryParam("interfaceName") String interfaceName) throws CapabilityException;

	/**
	 * Returns interfaces info
	 * 
	 * @return
	 */
	public InterfaceInfoList getInterfacesInfo() throws CapabilityException;

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
	public void upPhysicalInterface(LogicalPort iface) throws CapabilityException;

	@PUT
	@Path("/interfaces/status/up")
	public void upPhysicalInterface(@QueryParam("ifaceName") String ifaceName) throws CapabilityException;

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
	public void downPhysicalInterface(LogicalPort iface) throws CapabilityException;

	@PUT
	@Path("/interfaces/status/down")
	public void downPhysicalInterface(@QueryParam("ifaceName") String ifaceName) throws CapabilityException;

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
	public void createSubInterface(NetworkPort iface) throws CapabilityException;

	@POST
	@Path("/interfaces")
	@Consumes(MediaType.APPLICATION_XML)
	public void createSubInterface(InterfaceInfo interfaceInfo) throws CapabilityException;

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
	public void deleteSubInterface(NetworkPort iface) throws CapabilityException;

	@DELETE
	@Path("/interfaces")
	public void deleteSubInterface(@QueryParam("ifaceName") String ifaceName) throws CapabilityException;

	/*
	 * Logical Routers
	 */
	/**
	 * Returns a list of logical router names
	 * 
	 * @return
	 */
	@GET
	@Path("/logicalrouter")
	@Produces(MediaType.APPLICATION_XML)
	public LogicalRoutersNamesList getLogicalRoutersNames();

	/**
	 * Returns a list of logical routers
	 * 
	 * @return
	 */
	public List<ComputerSystem> getLogicalRouters();

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
	public void createLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException;

	@POST
	@Path("/logicalrouter/{logicalRouterName}")
	@Consumes(MediaType.APPLICATION_XML)
	public void createLogicalRouter(@PathParam("logicalRouterName") String logicalRouterName, InterfacesNamesList interfacesNamesList)
			throws CapabilityException;

	/**
	 * Deletes given logical router.
	 * 
	 * @param logicalRouter
	 *            existing logical router to delete.
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	public void deleteLogicalRouter(ComputerSystem logicalRouter) throws CapabilityException;

	@DELETE
	@Path("/logicalrouter/{logicalRouterName}")
	@Consumes(MediaType.APPLICATION_XML)
	public void deleteLogicalRouter(@PathParam("logicalRouterName") String logicalRouterName) throws CapabilityException;

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
	public void addInterfacesToLogicalRouter(ComputerSystem logicalRouter, List<? extends LogicalPort> interfaces) throws CapabilityException;

	@PUT
	@Path("/logicalrouter/{logicalRouterName}/interfaces")
	@Consumes(MediaType.APPLICATION_XML)
	public void addInterfacesToLogicalRouter(@PathParam("logicalRouterName") String logicalRouterName, InterfacesNamesList interfacesNamesList)
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
	public void removeInterfacesFromLogicalRouter(ComputerSystem logicalRouter, List<? extends LogicalPort> interfaces) throws CapabilityException;

	@PUT
	@Path("/logicalrouter/{logicalRouterName}/interfaces/delete")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeInterfacesFromLogicalRouter(@PathParam("logicalRouterName") String logicalRouterName, InterfacesNamesList interfacesNamesList)
			throws CapabilityException;

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
