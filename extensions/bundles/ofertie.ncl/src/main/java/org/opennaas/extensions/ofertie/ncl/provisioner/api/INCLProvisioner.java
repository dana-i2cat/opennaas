package org.opennaas.extensions.ofertie.ncl.provisioner.api;

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

import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowNotFoundException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Flow;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat) 
 *
 */
@Path("/flows")
public interface INCLProvisioner {
	
	/**
	 * Allocates a flow.
	 * 
	 * @param flowRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws ProvisionerException
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String allocateFlow(FlowRequest flowRequest) throws FlowAllocationException, ProvisionerException;
	
	/**
	 * Updates already allocated flow having flowId. May cause re-allocating the flow.
	 * 
	 * @param flowId of flow to update
	 * @param updatedFlowRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String updateFlow(@PathParam("id") String flowId, FlowRequest updatedFlowRequest) throws FlowAllocationException, FlowNotFoundException, ProvisionerException;
	
	/**
	 * Deallocates an allocated flow.
	 * 
	 * @param flowId id of flow to deallocate
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@Path("/{id}")
	@DELETE
	public void deallocateFlow(@PathParam("id") String flowId) throws FlowNotFoundException, ProvisionerException;
	
	/**
	 * Returns currently allocated flows.
	 * 
	 * @return Currently allocated flows
	 * @throws ProvisionerException
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Collection<Flow> readAllocatedFlows() throws ProvisionerException;
	

}
