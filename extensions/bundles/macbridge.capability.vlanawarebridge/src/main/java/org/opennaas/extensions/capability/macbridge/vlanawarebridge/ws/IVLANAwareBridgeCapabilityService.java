package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.AddStaticVLANRequest;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.CreateVLANConfigurationRequest;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.DeleteStaticVLANRequest;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.DeleteVLANConfigurationRequest;

/**
 * A capability that partially implements the management functions specified in the standard IEEE 802.1q
 * 
 * @author Eduard Grasa
 */
@Path("/")
public interface IVLANAwareBridgeCapabilityService extends IVLANAwareBridgeCapability {

	/**
	 * Add a new VLAN Configuration to the VLAN database
	 * 
	 * @param vlanConfiguration
	 * @throws CapabilityException
	 */
	@POST
	@Path("/createVLANConfiguration")
	@Consumes(MediaType.APPLICATION_XML)
	public Response createVLANConfiguration(CreateVLANConfigurationRequest request) throws CapabilityException;

	/**
	 * Delete a VLAN Configuration from the VLAN database
	 * 
	 * @param vlanId
	 *            the id of the VLAN
	 * @throws CapabilityException
	 */
	@DELETE
	@Path("/deleteVLANConfiguration")
	public Response deleteVLANConfiguration(DeleteVLANConfigurationRequest request) throws CapabilityException;

	/**
	 * 
	 * @param entry
	 * @throws CapabilityException
	 */
	@POST
	@Path("/addStaticVLAN")
	@Consumes(MediaType.APPLICATION_XML)
	public Response addStaticVLAN(AddStaticVLANRequest request) throws CapabilityException;

	/**
	 * @param vlanID
	 * @throws CapabilityException
	 */
	@DELETE
	@Path("/deleteStaticVLAN")
	public Response deleteStaticVLAN(DeleteStaticVLANRequest request) throws CapabilityException;
}
