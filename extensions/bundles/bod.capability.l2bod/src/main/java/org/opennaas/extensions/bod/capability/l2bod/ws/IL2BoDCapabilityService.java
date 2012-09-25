package org.opennaas.extensions.bod.capability.l2bod.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.ws.wrapper.RequestConnectionRequest;
import org.opennaas.extensions.bod.capability.l2bod.ws.wrapper.ShutDownConnectionRequest;

@Path("/")
public interface IL2BoDCapabilityService extends IL2BoDCapability {

	/**
	 * @throws CapabilityException
	 */
	@Path("/requestConnection")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response requestConnection(RequestConnectionRequest request) throws CapabilityException;

	/**
	 * @throws CapabilityException
	 */
	@Path("/shutdownConnection")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response shutDownConnection(ShutDownConnectionRequest request) throws CapabilityException;
}
