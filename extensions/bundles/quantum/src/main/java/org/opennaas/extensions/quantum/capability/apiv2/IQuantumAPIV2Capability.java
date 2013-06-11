package org.opennaas.extensions.quantum.capability.apiv2;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.quantum.model.QuantumModel;

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
	public QuantumModel listNetworks();

	@Path("/networks")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel createNetwork(QuantumModel network);

	@Path("/networks/{network_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel showNetwork(@PathParam("network_id") String networkId);

	@Path("/networks/{network_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel updateNetwork(@PathParam("network_id") String networkId, QuantumModel updatedNetwork);

	@Path("/networks/{network_id}")
	@DELETE
	public void deleteNetwork(@PathParam("network_id") String networkId);

	// PORTS CRUD

	@Path("/ports")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel listPorts();

	@Path("/ports")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel createPort(QuantumModel port);

	@Path("/ports/{port_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel showPort(@PathParam("port_id") String portId);

	@Path("/ports/{port_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel updatePort(@PathParam("port_id") String portId, QuantumModel updatedPort);

	@Path("/ports/{port_id}")
	@DELETE
	public void removePort(@PathParam("port_id") String portId);

	// SUBNETS CRUD

	@Path("/subnets")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel listSubnets();

	@Path("/subnets")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel createSubnet(QuantumModel subnet);

	@Path("/subnets/{subnet_id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel showSubnet(@PathParam("subnet_id") String subnetId);

	@Path("/subnets/{subnet_id}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public QuantumModel updateSubnet(@PathParam("subnet_id") String subnetId, QuantumModel updatedSubnet);

	@Path("/subnets/{subnet_id}")
	@DELETE
	public void removeSubnet(@PathParam("subnet_id") String subnetId);
}
