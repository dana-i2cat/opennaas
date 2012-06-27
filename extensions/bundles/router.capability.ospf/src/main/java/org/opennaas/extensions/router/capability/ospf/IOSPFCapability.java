package org.opennaas.extensions.router.capability.ospf;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;

/**
 * @author Jordi Puig
 * @author Isart Canyameres
 */
@Path("/ospf")
public interface IOSPFCapability extends ICapability {

	@Path("/testpost")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void testpost(JAXBElement<Test> test);

	@Path("/testpost2")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void testpost2(Test test);

	@Path("/testpost3")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void testpost3(String test);

	@Path("/testpost4")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void testpost4(@QueryParam("test") Test test);

	@Path("/testpost5")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void testpost5(Object test);

	/**
	 * Enable OSPF on the router.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/activate")
	@POST
	public void activateOSPF() throws CapabilityException;

	/**
	 * Disable OSPF on the router.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/deactivate")
	@POST
	public void deactivateOSPF() throws CapabilityException;

	/**
	 * Configure OSPF service.
	 * 
	 * This configuration applies to the OSPF Service itself, but does not affect areas nor interfaces.
	 * 
	 * @param ospfService
	 * @throws CapabilityException
	 */
	@Path("/configure")
	@POST
	public void configureOSPF(@FormParam("ospfService") OSPFService ospfService) throws CapabilityException;

	/**
	 * Removes all OSPF configuration.
	 * 
	 * @param ospfService
	 * @throws CapabilityException
	 */
	@Path("/clear")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void clearOSPFconfiguration(@FormParam("ospfService") OSPFService ospfService) throws CapabilityException;

	/**
	 * Configures an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	@Path("/configureArea")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void configureOSPFArea(@FormParam("ospfAreaConfiguration") OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Remove an OSPF area.
	 * 
	 * @param ospfAreaConfiguration
	 * @throws CapabilityException
	 */
	@Path("/removeArea")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void removeOSPFArea(@FormParam("ospfAreaConfiguration") OSPFAreaConfiguration ospfAreaConfiguration) throws CapabilityException;

	/**
	 * Adds given interfaces to given OSPF area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	@Path("/addInterfacesInArea")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addInterfacesInOSPFArea(@FormParam("interfaces") List<LogicalPort> interfaces, @FormParam("ospfArea") OSPFArea ospfArea)
			throws CapabilityException;

	/**
	 * Remove given interfaces from given OSPF area
	 * 
	 * @param interfaces
	 * @param ospfArea
	 * @throws CapabilityException
	 */
	@Path("/removesInterfacesInArea")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void removeInterfacesInOSPFArea(@FormParam("interfaces") List<LogicalPort> interfaces, @FormParam("ospfArea") OSPFArea ospfArea)
			throws CapabilityException;

	/**
	 * Enable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @throws CapabilityException
	 */
	@Path("/enableInterfaces")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void enableOSPFInterfaces(@FormParam("interfaces") List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Disable OSPF in given interfaces, if they are already configured.
	 * 
	 * @param interfaces
	 * @throws CapabilityException
	 */
	@Path("/disableInterfaces")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void disableOSPFInterfaces(@FormParam("interfaces") List<OSPFProtocolEndpoint> interfaces) throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the router
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @throws CapabilityException
	 */
	@Path("/getConfiguration")
	@POST
	public void getOSPFConfiguration() throws CapabilityException;

	/**
	 * Returns OSPF full configuration from the model
	 * 
	 * This includes service, areas, and interface status.
	 * 
	 * @return ospfService
	 * @throws CapabilityException
	 */
	@Path("/showConfiguration")
	@GET
	public OSPFService showOSPFConfiguration() throws CapabilityException;

}
