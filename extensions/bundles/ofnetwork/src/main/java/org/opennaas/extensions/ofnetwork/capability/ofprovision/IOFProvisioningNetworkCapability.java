package org.opennaas.extensions.ofnetwork.capability.ofprovision;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Path("/")
public interface IOFProvisioningNetworkCapability extends ICapability {

	/**
	 * Allocates given flows
	 * 
	 * @param flows
	 *            to be allocated
	 * @throws CapabilityException
	 */
	@Path("/allocateFlows")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void allocateFlows(List<NetOFFlow> flows) throws CapabilityException;

	/**
	 * Deallocates given allocated flows
	 * 
	 * @param flows
	 * @throws CapabilityException
	 */
	@Path("/deallocateFlows/")
	@POST
	public void deallocateFlows(List<NetOFFlow> flows) throws CapabilityException;

	/**
	 * 
	 * @return allocated flows in the network
	 * @throws CapabilityException
	 */
	@Path("/getAllocatedFlows")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Set<NetOFFlow> getAllocatedFlows() throws CapabilityException;

	/**
	 * Replaces given current flows with desired ones.
	 * 
	 * @param current
	 *            flows to be replaced
	 * @param desired
	 *            replacement flows
	 * @return
	 * @throws CapabilityException
	 */
	@Path("/replaceFlows/")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void replaceFlows(List<NetOFFlow> current, List<NetOFFlow> desired) throws CapabilityException;

}
