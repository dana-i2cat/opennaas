package org.opennaas.extensions.openflowswitch.capability;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.openflowswitch.model.OFForwardingRule;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
@Path("/")
public interface IOpenflowForwardingCapability extends ICapability {

	@POST
	@Path("/createOFFForwardingRule")
	@Consumes(MediaType.APPLICATION_XML)
	public void createOpenflowForwardingRule(OFForwardingRule forwardingRule);

	@DELETE
	@Path("/removeOFForwardingRule")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeOpenflowForwardingRule(String flowId);

	@GET
	@Path("/getOFForwardingRules")
	@Produces(MediaType.APPLICATION_XML)
	public List<OFForwardingRule> getOpenflowForwardingRules() throws CapabilityException;

}
