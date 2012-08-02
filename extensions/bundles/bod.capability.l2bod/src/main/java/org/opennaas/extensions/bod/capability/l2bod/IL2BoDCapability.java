package org.opennaas.extensions.bod.capability.l2bod;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.network.model.topology.Interface;

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
	public void shutDownConnection(List<Interface> listInterfaces) throws CapabilityException;
}
