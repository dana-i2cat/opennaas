package org.opennaas.extensions.quantum.capability.apiv2;

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

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.quantum.model.Attachment;
import org.opennaas.extensions.quantum.model.Network;
import org.opennaas.extensions.quantum.model.Port;
import org.opennaas.extensions.quantum.model.Subnet;

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

	@Path("/networks")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Network> listNetworks();

	@Path("/tenants/{tenant_id}/networks")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Network createNetwork(@PathParam("tenant_id") String tenantId, Network network) throws CapabilityException;

	@Path("/networks/{network_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Network showNetwork(@PathParam("network_id") String networkId);

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

	@Path("/ports")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Port> listPorts();

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Port createPort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, Port port) throws CapabilityException;

	@Path("/ports/{port_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Port showPort(@PathParam("port_id") String portId);

	@Path("/tenants/{tenant_id}/networks/{network_id}/ports/{port_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Port updatePort(@PathParam("tenant_id") String tenantId, @PathParam("network_id") String networkId, @PathParam("port_id") String portId,
			Port updatedPort) throws CapabilityException;

	@Path("/ports/{port_id}")
	@DELETE
	public void removePort(@PathParam("port_id") String portId);

	// SUBNETS CRUD

	@Path("/subnets")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Subnet> listSubnets();

	@Path("/subnets")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Subnet createSubnet(Subnet subnet);

	@Path("/subnets/{subnet_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Subnet showSubnet(@PathParam("subnet_id") String subnetId);

	@Path("/subnets/{subnet_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Subnet updateSubnet(@PathParam("subnet_id") String subnetId, Subnet updatedSubnet);

	@Path("/subnets/{subnet_id}")
	@DELETE
	public void removeSubnet(@PathParam("subnet_id") String subnetId);

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
