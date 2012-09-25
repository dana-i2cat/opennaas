package org.opennaas.extensions.network.capability.basic.ws;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.network.capability.basic.Activator;
import org.opennaas.extensions.network.capability.basic.NetworkBasicCapability;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.AddResourceRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.L2AttachRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.L2DettachRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.RemoveResourceRequest;

public class NetworkBasicCapabilityServiceImpl extends NetworkBasicCapability implements INetworkBasicCapabilityService {

	private static final Logger	LOGGER	= Logger.getLogger(NetworkBasicCapabilityServiceImpl.class);

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public NetworkBasicCapabilityServiceImpl(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor, resourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.ws.INetworkBasicCapabilityService#addResource(org.opennaas.extensions.network.capability.basic
	 * .ws.wrapper.AddResourceRequest)
	 */
	@Override
	public Response addResource(AddResourceRequest request) throws CapabilityException {
		try {
			LOGGER.info("Start addResource service");
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResource iResource = resourceManager.getResourceById(request.getResourceId());
			addResource(iResource);
			LOGGER.info("End of addResource service");
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.ws.INetworkBasicCapabilityService#removeResource(org.opennaas.extensions.network.capability
	 * .basic.ws.wrapper.RemoveResourceRequest)
	 */
	@Override
	public Response removeResource(RemoveResourceRequest request) throws CapabilityException {
		try {
			LOGGER.info("Start removeResource service");
			IResourceManager resourceManager = Activator.getResourceManagerService();
			IResource iResource = resourceManager.getResourceById(request.getResourceId());
			removeResource(iResource);
			LOGGER.info("End of removeResource service");
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (ResourceException e) {
			throw new CapabilityException(e);
		}
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.ws.INetworkBasicCapabilityService#l2attach(org.opennaas.extensions.network.capability.basic
	 * .ws.wrapper.L2AttachRequest)
	 */
	@Override
	public Response l2attach(L2AttachRequest request) throws CapabilityException {
		LOGGER.info("Start L2AttachRequest service");
		l2attach(request.getLink().getSource(), request.getLink().getSink());
		LOGGER.info("End of L2AttachRequest service");
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.network.capability.basic.ws.INetworkBasicCapabilityService#l2detach(org.opennaas.extensions.network.capability.basic
	 * .ws.wrapper.L2DettachRequest)
	 */
	@Override
	public Response l2detach(L2DettachRequest request) throws CapabilityException {
		LOGGER.info("Start l2detach service");
		l2detach(request.getLink().getSource(), request.getLink().getSink());
		LOGGER.info("End of l2detach service");
		return Response.ok().build();
	}

}
