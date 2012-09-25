package org.opennaas.extensions.router.capability.ip.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.capability.ip.ws.wrapper.SetInterfaceDescriptionRequest;
import org.opennaas.extensions.router.capability.ip.ws.wrapper.SetIpAddressRequest;

@Path("/")
public interface IIPCapabilityService extends IIPCapability {

	/**
	 * @param request
	 * @return Response
	 * @throws CapabilityException
	 */
	@POST
	@Path("/setIPv4")
	@Consumes(MediaType.APPLICATION_XML)
	public Response setIPv4(SetIpAddressRequest request) throws CapabilityException;

	/**
	 * @param request
	 * @return Response
	 * @throws CapabilityException
	 */
	@POST
	@Path("/setInterfaceDescription")
	@Consumes(MediaType.APPLICATION_XML)
	public Response setInterfaceDescription(SetInterfaceDescriptionRequest request) throws CapabilityException;

}
