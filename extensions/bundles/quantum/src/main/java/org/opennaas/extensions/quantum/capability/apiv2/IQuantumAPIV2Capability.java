package org.opennaas.extensions.quantum.capability.apiv2;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.quantum.model.Attachment;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.Port;

/**
 * Quantum Networking API v2.0<br />
 * Based on <a href="http://docs.openstack.org/api/openstack-network/2.0/content/API_Operations.html">OpenStack Networking Documentation - API
 * Operations</a>
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IQuantumAPIV2Capability extends ICapability {

	// NETWORKS CRUD

	@Path("/tenants/{tenant_id}/networks")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Network createNetwork(@PathParam("tenant_id") String tenantId, Network network) throws CapabilityException;

	@Path("/tenants/{tenant_id}/networks/{network_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Network updateNetwork(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, Network updatedNetwork)
			throws CapabilityException;

	@Path("/tenants/{tenant_id}/networks/{network_id}")
	@DELETE
	public void deleteNetwork(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId) throws CapabilityException;

	// PORTS CRUD

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Port createPort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, Port port) throws CapabilityException;

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports/{port_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Port updatePort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, @PathParam("port_id") String portId,
			Port updatedPort) throws CapabilityException;

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports/{port_id}")
	@DELETE
	public void removePort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, @PathParam("port_id") String portId)
			throws CapabilityException;

	// ATTACHMENTS CRUD

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports/{port_id}/attachment")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Attachment createAttachment(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId,
			@PathParam("port_id") String portId, Attachment attachment) throws CapabilityException;

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports/{port_id}/attachment")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Attachment updateAttachment(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId,
			@PathParam("port_id") String portId, Attachment attachment) throws CapabilityException;

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports/{port_id}/attachment/{attachment_id}")
	@DELETE
	public void removeAttachment(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId,
			@PathParam("port_id") String portId, @PathParam("attachment_id") String attachmentId);
}
