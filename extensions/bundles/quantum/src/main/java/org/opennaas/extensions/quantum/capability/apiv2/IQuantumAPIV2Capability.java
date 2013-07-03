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
	public Network createNetwork(@PathParam("tenant_id") String tenant_id, Network network) throws CapabilityException;

	@Path("/networks/{network_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Network showNetwork(@PathParam("network_id") String networkId);

	@Path("/networks/{network_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Network updateNetwork(@PathParam("network_id") String networkId, Network updatedNetwork);

	@Path("/networks/{network_id}")
	@DELETE
	public void deleteNetwork(@PathParam("network_id") String networkId) throws CapabilityException;

	// PORTS CRUD

	@Path("/ports")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Port> listPorts();

	@Path("/ports")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Port createPort(Port port);

	@Path("/ports/{port_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Port showPort(@PathParam("port_id") String portId);

	@Path("/ports/{port_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Port updatePort(@PathParam("port_id") String portId, Port updatedPort);

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
}
