package org.opennaas.extensions.bod.capability.l2bod.ws;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.ws.wrapper.RequestConnectionRequest;
import org.opennaas.extensions.bod.capability.l2bod.ws.wrapper.ShutDownConnectionRequest;

public class L2BoDCapabilityServiceImpl extends L2BoDCapability implements IL2BoDCapabilityService {

	private static final Logger	LOGGER	= Logger.getLogger(L2BoDCapabilityServiceImpl.class);

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public L2BoDCapabilityServiceImpl(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor, resourceId);
	}

	/**
	 * @param request
	 * @return
	 * @throws CapabilityException
	 */
	public Response requestConnection(RequestConnectionRequest request) throws CapabilityException {
		LOGGER.info("Start of requestConnection call");
		requestConnection(request.getParameters());
		LOGGER.info("End of requestConnection call");
		return Response.ok().build();
	}

	/**
	 * @param request
	 * @return
	 * @throws CapabilityException
	 */
	public Response shutDownConnection(ShutDownConnectionRequest request) throws CapabilityException {
		LOGGER.info("Start of shutDownConnection call");
		shutDownConnection(request.getListInterfaces());
		LOGGER.info("End of shutDownConnection call");
		return Response.ok().build();
	}

}
