package org.opennaas.extensions.roadm.capability.connections;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;

@Path("/")
public interface IConnectionsCapability extends ICapability {

	@Path("/makeConnection")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public void makeConnection(FiberConnection connectionRequest) throws CapabilityException;

	@Path("/removeConnection")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public void removeConnection(FiberConnection connectionRequest) throws CapabilityException;

}
