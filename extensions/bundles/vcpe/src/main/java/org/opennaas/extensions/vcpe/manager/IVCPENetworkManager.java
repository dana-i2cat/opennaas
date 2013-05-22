package org.opennaas.extensions.vcpe.manager;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.vcpe.manager.model.VCPEManagerModel;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

@Path("/")
public interface IVCPENetworkManager {

	/**
	 * Create a VCPE infrastructure of VCPEResource from model
	 * 
	 * @return the id if the VCPE has been created
	 * @throws VCPENetworkManagerException
	 */
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String create(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException;

	/**
	 * Update a VCPE infrastructure of VCPEResource from model
	 * 
	 * @return the id if the VCPE has been created
	 * @throws VCPENetworkManagerException
	 */
	@Path("/update")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String update(VCPENetworkModel vcpeNetworkModel) throws VCPENetworkManagerException;

	/**
	 * Remove a VCPE infrastructure of the resource with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return true if the VCPE has been removed, false otherwise
	 * @throws VCPENetworkManagerException
	 */
	@Path("/remove/{id}")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean remove(@PathParam("id") String vcpeNetworkId) throws VCPENetworkManagerException;

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
	 * @Consumes(MediaType.APPLICATION_XML)
	 * @Produces(MediaType.APPLICATION_XML)
	 * @return all the VCPENetworks
	 * @throws VCPENetworkManagerException
	 */
	@Path("/getAllVCPENetworks")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<VCPENetworkModel> getAllVCPENetworks() throws VCPENetworkManagerException;

	/**
	 * Get the VCPEManagerModel model
	 * 
	 * @return the VCPEManagerModel model
	 */
	@Path("/getModel")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public VCPEManagerModel getModel();

	/**
	 * Get a suggestion for the physical infrastructure
	 * 
	 * @param templateType
	 *            whose physical infrastructure is desired.
	 * @return the physical infrastructure
	 * @throws VCPENetworkManagerException
	 */
	@Path("/getPhyInfrastructureSuggestion")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public VCPENetworkModel getPhysicalInfrastructureSuggestion(@QueryParam("templateType") String templateType) throws VCPENetworkManagerException;

	/**
	 * Get a suggestion for the logical infrastructure
	 * 
	 * @param templateType
	 *            whose physical infrastructure is desired.
	 * @param physicalInfrastructure
	 *            supporting the logical infrastructure to obtain.
	 * @return the suggested logical infrastructure
	 * @throws VCPENetworkManagerException
	 */
	@Path("/getLogicalInfrastructureSuggestion")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public VCPENetworkModel getLogicalInfrastructureSuggestion(VCPENetworkModel physicalInfrastructure) throws VCPENetworkManagerException;

	/**
	 * Check if a VLAN is available or not in a interface
	 * 
	 * @param vcpeId
	 * @param router
	 * @param vlan
	 * @param ifaceName
	 * @return true if is available
	 * @throws VCPENetworkManagerException
	 */
	@Path("/isVLANFree")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean isVLANFree(@QueryParam("vcpeId") String vcpeId, @QueryParam("router") String router, @QueryParam("vlan") String vlan,
			@QueryParam("ifaceName") String ifaceName)
			throws VCPENetworkManagerException;

	/**
	 * Check if an IP is available or not in the environment
	 * 
	 * @param vcpeId
	 * @param router
	 * @param iface
	 * @return true if is available
	 * @throws VCPENetworkManagerException
	 */
	@Path("/isIPFree")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean isIPFree(@QueryParam("vcpeId") String vcpeId, @QueryParam("router") String router, @QueryParam("ip") String ip)
			throws VCPENetworkManagerException;

	/**
	 * Check if an interface is available or not in the environment
	 * 
	 * @param vcpeId
	 * @param router
	 * @param iface
	 * @return true if is available
	 * @throws VCPENetworkManagerException
	 */
	@Path("/isInterfaceFree")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean isInterfaceFree(@QueryParam("vcpeId") String vcpeId, @QueryParam("router") String router, @QueryParam("iface") String iface)
			throws VCPENetworkManagerException;

	/**
	 * 
	 * @param resourceId
	 * @return true if the build has finished (either by having completed the task or having failed), false otherwise.
	 * @throws VCPENetworkManagerException
	 *             if there is no building execution for given resource id.
	 */
	@Path("/hasFinishedBuild/{id}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	boolean hasFinishedBuild(@PathParam("id") String resourceId) throws VCPENetworkManagerException;

	/**
	 * This implementation consumes the task, so following invocations to this method with same resourceId throw an exception
	 * 
	 * @param resourceId
	 * @return true if the build has been successful
	 * @throws VCPENetworkManagerException
	 *             if building has failed, or there is no building execution for given resource id.
	 */
	@Path("/getBuildResult/{id}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	boolean getBuildResult(@PathParam("id") String resourceId) throws VCPENetworkManagerException;

	@Path("/getUserFilteredVCPEModel/{id}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	VCPENetworkModel getUserFilteredVCPEModel(@PathParam("id") String vcpeNetworkId);

	@Path("/editFilteredVCPE/{id}")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	VCPENetworkModel editFilteredVCPE(@PathParam("id") String vcpeNetworkId, VCPENetworkModel filteredModel);

}
