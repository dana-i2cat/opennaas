package org.opennaas.extensions.sdnnetwork.capability.ofprovision;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IOFProvisioningNetworkCapability extends ICapability {

	/**
	 * Allocates a flow in the network
	 * 
	 * @param flowWithRoute
	 *            A Flow with a defined route
	 * @return flowId of created SDNNetworkOFFlow
	 * @throws CapabilityException
	 */
	@Path("/allocateOFFlow")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String allocateOFFlow(SDNNetworkOFFlow flowWithRoute) throws CapabilityException;

	/**
	 * Deallocates an existing flow in the network
	 * 
	 * @param flowId
	 * @throws CapabilityException
	 */
	@Path("/deallocateOFFlow/{flowId}")
	@DELETE
	public void deallocateOFFlow(@PathParam("flowId") String flowId) throws CapabilityException;

	/**
	 * 
	 * @return allocated flows
	 * @throws CapabilityException
	 */
	@Path("/getAllocatedFlows")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Collection<SDNNetworkOFFlow> getAllocatedFlows() throws CapabilityException;

	/**
	 * Updates an allocated flow in the network
	 * 
	 * @param flowId
	 *            of the already allocated flow to update
	 * @param flowWithRoute
	 *            A Flow with a defined route
	 * @return flowId of created SDNNetworkOFFlow
	 * @throws CapabilityException
	 */
	@Path("/updateAllocatedOFFlow/{flowId}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String updateAllocatedOFFlow(@PathParam("flowId") String flowId, SDNNetworkOFFlow flowWithRoute) throws CapabilityException;

	/**
	 * Maps a Floodlight device with an OpenNaaS resource
	 * 
	 * @param deviceId
	 *            of the Floodlight device
	 * @param resourceID
	 *            of the OpenNaaS resource
         * @throws org.opennaas.core.resources.capability.CapabilityException
	 */
	@Path("/mapDeviceResource/{deviceId}/{resourceID}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public void mapDeviceResource(@PathParam("deviceId") String deviceId, @PathParam("resourceID") String resourceID) throws CapabilityException;

        /**
	 * Maps a Floodlight device with an OpenNaaS resource
	 * 
	 * @param deviceId
	 *            of the Floodlight device
         * @return 
         * @throws org.opennaas.core.resources.capability.CapabilityException
	 */
	@Path("/mapDeviceResource/{deviceId}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public String getMapDeviceResource(@PathParam("deviceId") String deviceId) throws CapabilityException;

        /**
         * Get the entire list of devices
         * @return
         * @throws CapabilityException 
         */
        @Path("/mapDeviceResource")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public Response getMapDevices() throws CapabilityException;
        
         /**
         * Get the resource name given the DPID
         * @param deviceId
         * @return
         * @throws CapabilityException 
         */
        @Path("/getDeviceResourceName/{deviceId}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public String getNameDevice(@PathParam("deviceId") String deviceId) throws CapabilityException;
        
        
	/**
	 * Clears the map of Floodlight devices - OpenNaaS resources
	 */
	@Path("/mapDeviceResource")
	@DELETE
	public void clearMap();

}
