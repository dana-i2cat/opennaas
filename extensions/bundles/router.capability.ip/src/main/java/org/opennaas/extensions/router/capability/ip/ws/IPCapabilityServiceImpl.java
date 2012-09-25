package org.opennaas.extensions.router.capability.ip.ws;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.router.capability.ip.IPCapability;
import org.opennaas.extensions.router.capability.ip.ws.wrapper.SetInterfaceDescriptionRequest;
import org.opennaas.extensions.router.capability.ip.ws.wrapper.SetIpAddressRequest;

/**
 * @author Jordi
 */
public class IPCapabilityServiceImpl extends IPCapability implements IIPCapabilityService {

	private static final Logger	LOGGER	= Logger.getLogger(IPCapabilityServiceImpl.class);

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public IPCapabilityServiceImpl(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor, resourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.ws.capability.ip.IIPCapabilityService#setIPv4(org.opennaas.extensions.router.model.wrappers.SetIpAddressRequest)
	 */
	@Override
	public Response setIPv4(SetIpAddressRequest request) throws CapabilityException {
		LOGGER.info("Start setIPv4 service");
		setIPv4(request.getLogicalDevice(), request.getIpProtocolEndpoint());
		LOGGER.info("End of setIPv4 service");
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.ws.capability.ip.IIPCapabilityService#setInterfaceDescription(org.opennaas.ws.capability.ip.requests.SetInterfaceDescriptionRequest
	 * )
	 */
	@Override
	public Response setInterfaceDescription(SetInterfaceDescriptionRequest request) throws CapabilityException {
		LOGGER.info("Calling setIPv4 service");
		setInterfaceDescription(request.getIface());
		LOGGER.info("End of setIPv4 call");
		return Response.ok().build();
	}
}
