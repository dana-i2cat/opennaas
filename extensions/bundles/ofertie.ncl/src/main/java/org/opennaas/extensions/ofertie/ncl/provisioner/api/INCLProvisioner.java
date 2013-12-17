package org.opennaas.extensions.ofertie.ncl.provisioner.api;

import java.util.Collection;
import java.util.List;

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
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Circuit;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
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
	 * @param flowId
	 *            of flow to update
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
	public String updateFlow(@PathParam("id") String flowId, FlowRequest updatedFlowRequest) throws FlowAllocationException, FlowNotFoundException,
			ProvisionerException;

	/**
	 * Deallocates an allocated flow.
	 * 
	 * @param flowId
	 *            id of flow to deallocate
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
	public Collection<Circuit> readAllocatedFlows() throws ProvisionerException;

	/**
	 * Returns implementation of given flow
	 * 
	 * @param flowId
	 *            id of flow to query for
	 * @return Currently allocated NetOFFlow(s) that are the implementation of flow with given flowii
	 * @throws ProvisionerException
	 */
	@Path("/{id}/implementation")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<NetOFFlow> getFlowImplementation(@PathParam("id") String flowId) throws ProvisionerException;

	/**
	 * Returns QoS network requirements for one flow
	 * 
	 * @param flowId
	 * @return allocated Circuit
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}")
	@Produces(MediaType.APPLICATION_XML)
	public Circuit getFlow(@PathParam("flowId") String flowId) throws FlowNotFoundException, ProvisionerException;

	/**
	 * Returns QoS network requirements parameter for one flow
	 * 
	 * @param flowId
	 * @param parameter
	 * @return QoS parameter value
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 */
	@GET
	@Path("/{flowId}/{parameter}")
	@Produces(MediaType.APPLICATION_XML)
	public int getQoSParameter(@PathParam("flowId") String flowId, @PathParam("parameter") String parameter) throws FlowNotFoundException,
			ProvisionerException;

	/**
	 * Updates QoS network requirements parameter for one flow
	 * 
	 * @param flowId
	 * @param parameter
	 * @param value
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@PUT
	@Path("/flows/{flowId}/{parameter}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public void updateQoSParameter(@PathParam("flowId") String flowId, @PathParam("parameter") String parameter, int value)
			throws FlowNotFoundException, ProvisionerException, FlowAllocationException;

	/**
	 * Deletes QoS network requirements parameter for one flow
	 * 
	 * @param flowId
	 * @param parameter
	 * @throws FlowNotFoundException
	 * @throws ProvisionerException
	 * @throws FlowAllocationException
	 */
	@DELETE
	@Path("/flows/{flowId}/{parameter}")
	@Produces(MediaType.APPLICATION_XML)
	public void deleteQoSParameter(@PathParam("flowId") String flowId, @PathParam("parameter") String parameter) throws FlowNotFoundException,
			ProvisionerException, FlowAllocationException;

}
