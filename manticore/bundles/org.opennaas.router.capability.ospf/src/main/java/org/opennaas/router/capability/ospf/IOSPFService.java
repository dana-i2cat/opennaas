package org.opennaas.router.capability.ospf;

import java.util.List;

import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
import net.i2cat.mantychore.model.OSPFProtocolEndpoint;
import net.i2cat.mantychore.model.OSPFService;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;

/**
 * @author Jordi Puig
 * @author Isart Canyameres
 */
public interface IOSPFService {

	/**
	 * Enable OSPF on the router.
	 * 
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response activateOSPF() throws CapabilityException;

	/**
	 * Disable OSPF on the router.
	 * 
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response deactivateOSPF() throws CapabilityException;

	/**
	 * Configure OSPF service.
	 * 
	 * This configuration applies to the OSPF Service itself, but does not affect areas nor interfaces.
	 * 
	 * @param ospfService
	 *            containing all desired configuration details.
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response configureOSPF(OSPFService ospfService) throws CapabilityException;

	/**
	 * Removes all OSPF configuration.
	 * 
	 * @param ospfService
	 *            to be removed.
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response clearOSPFconfiguration(OSPFService ospfService) throws CapabilityException;

	/**
	 * Configures an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 *            containing all desired configuration of the area
	 * @return
	 * @throws CapabilityException
	 */
	public Response configureOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Remove an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 *            to be removed
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response removeOSPFArea(OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPF area
	 * 
	 * @param interfaces
	 *            to be added
	 * @param ospfArea
	 *            where interfaces should be added to.
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response addInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPF area
	 * 
	 * @param interfaces
	 *            to be removed from ospfArea
	 * @param ospfArea
	 *            where interfaces should be removed from.
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response removeInterfacesInOSPFArea(List<LogicalPort> interfaces, OSPFArea ospfArea) throws CapabilityException;

	/**
	 * Enable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 *            to enable ospf in
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response enableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Disable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 *            to disable ospf in
	 * @return
	 * @throws CapabilityException
	 */
	public Response disableOSPFInterfaces(List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the model
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return OSPFService containing full ospf configuration
	 * @throws CapabilityException
	 */
	public OSPFService showOSPFConfiguration() throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the router
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return Response containing the status of the request.
	 * @throws CapabilityException
	 */
	public Response getOSPFConfiguration() throws CapabilityException;

}
