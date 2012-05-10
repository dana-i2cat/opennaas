package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;

/**
 * @author Jordi Puig
 */

@WebService(portName = "OSPFCapabilityPort", serviceName = "OSPFCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IOSPFCapabilityService {

	/**
	 * Enable OSPF on the router.
	 * 
	 * 
	 * @throws CapabilityException
	 */
	public void activateOSPF(String resourceId) throws CapabilityException;

	/**
	 * Disable OSPF on the router.
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void deactivateOSPF(String resourceId) throws CapabilityException;

	/**
	 * Configure OSPF service.
	 * 
	 * This configuration applies to the OSPF Service itself, but does not affect areas nor interfaces.
	 * 
	 * @param resourceId
	 * @param ospfService
	 * @throws CapabilityException
	 */
	public void configureOSPF(String resourceId, OSPFService ospfService) throws CapabilityException;

	/**
	 * Removes all OSPF configuration.
	 * 
	 * @param resourceId
	 * @param ospfService
	 * @throws CapabilityException
	 */
	public void clearOSPFconfiguration(String resourceId, OSPFService ospfService) throws CapabilityException;

	/**
	 * Configures an OSPF area.
	 * 
	 * @param resourceId
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	public void configureOSPFArea(String resourceId, OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Remove an OSPF area.
	 * 
	 * @param resourceId
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	public void removeOSPFArea(String resourceId, OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPF area
	 * 
	 * @param resourceId
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	public void addInterfacesInOSPFArea(String resourceId, List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPF area
	 * 
	 * @param resourceId
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	public void removeInterfacesInOSPFArea(String resourceId, List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException;

	/**
	 * Enable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param resourceId
	 * @param interfaces
	 * @throws CapabilityException
	 */
	public void enableOSPFInterfaces(String resourceId, List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Disable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param resourceId
	 * @param interfaces
	 * @throws CapabilityException
	 */
	public void disableOSPFInterfaces(String resourceId, List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the router
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @param resourceId
	 * @throws CapabilityException
	 */
	public void getOSPFConfiguration(String resourceId) throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the model
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @param resourceId
	 * @return ospfService
	 * @throws CapabilityException
	 */
	public OSPFService showOSPFConfiguration(String resourceId) throws CapabilityException;

}