package org.opennaas.extensions.vcpe.manager;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Path("/")
public interface IVCPENetworkManager {

	/**
	 * Create a VCPE infrastructure of the resource with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @param router1
	 * @param router2
	 * @return true if the VCPE has been created, false otherwise
	 * @throws VCPENetworkManagerException
	 */
	@Path("/build")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean build(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException;

	/**
	 * Remove a VCPE infrastructure of the resource with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return true if the VCPE has been removed, false otherwise
	 * @throws VCPENetworkManagerException
	 */
	@Path("/destroy/{id}")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean destroy(@PathParam("id") String vcpeNetworkId) throws VCPENetworkManagerException;

	/**
	 * Get the VCPENetwork with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return VCPENetworkModel
	 * @throws VCPENetworkManagerException
	 */
	@Path("/getVCPENetworkById/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public VCPENetworkModel getVCPENetworkById(@PathParam("id") String vcpeNetworkId) throws VCPENetworkManagerException;

	/**
	 * Get all VCPENetworks
	 * 
	 * @return
	 * @throws VCPENetworkManagerException
	 */
	@Path("/getAllVCPENetworks")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<VCPENetworkModel> getAllVCPENetworks() throws VCPENetworkManagerException;

}
