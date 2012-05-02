package org.opennaas.extensions.router.capability.ospf;

import java.util.List;

import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;

/**
 * @author Jordi Puig
 * @author Isart Canyameres
 */
public interface IOSPFService extends ICapability {

	/**
	 * Enable OSPF on the router.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public Response activateOSPF() throws CapabilityException;

	/**
	 * Disable OSPF on the router.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public Response deactivateOSPF() throws CapabilityException;

	/**
	 * Configure OSPF service.
	 * 
	 * This configuration applies to the OSPF Service itself, but does not affect areas nor interfaces.
	 * 
	 * @param ospfService
	 * @return
	 * @throws CapabilityException
	 */
	public Response configureOSPF(OSPFService ospfService) throws CapabilityException;

	/**
	 * Removes all OSPF configuration.
	 * 
	 * @param ospfService
	 * @return
	 * @throws CapabilityException
	 */
	public Response clearOSPFconfiguration(OSPFService ospfService) throws CapabilityException;

	/**
	 * Configures an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 * @return
	 * @throws CapabilityException
	 */
	public Response configureOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Remove an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 * @return
	 * @throws CapabilityException
	 */
	public Response removeOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPF area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @return
	 * @throws CapabilityException
	 */
	public Response addInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPF area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @return
	 * @throws CapabilityException
	 */
	public Response removeInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException;

	/**
	 * Enable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @return
	 * @throws CapabilityException
	 */
	public Response enableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Disable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @return
	 * @throws CapabilityException
	 */
	public Response disableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the model
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public OSPFService showOSPFConfiguration() throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the router
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return ospfService
	 * @throws CapabilityException
	 */
	public Response getOSPFConfiguration() throws CapabilityException;

}
