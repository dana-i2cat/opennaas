package org.opennaas.extensions.bod.capability.l2bod;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

@Path("/")
public interface IL2BoDCapability extends ICapability {

	/**
	 * @throws CapabilityException
	 */
	@Path("/requestConnection")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void requestConnection(RequestConnectionParameters parameters) throws CapabilityException;

	/**
	 * @throws CapabilityException
	 */
	@Path("/shutdownConnection")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void shutDownConnection(RequestConnectionParameters parameters) throws CapabilityException;

	/**
	 * @throws CapabilityException
	 */
	@Path("/shutdownConnectionLink")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void shutDownConnection(BoDLink link) throws CapabilityException;
}
