package org.opennaas.extensions.genericnetwork.capability.nclprovisioner;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@Path("/")
public interface INCLProvisionerCapability {

	/**
	 * Allocates a flow.
	 * 
	 * @param qosPolicyRequest
	 * @return flowId of allocated flow
	 * @throws AllocationException
	 * @throws ProvisionerException
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String allocateFlow(PathRequest pathRequest) throws CapabilityException;

}
