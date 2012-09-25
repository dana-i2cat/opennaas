package org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.VLANAwareBridgeCapability;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.AddStaticVLANRequest;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.CreateVLANConfigurationRequest;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.DeleteStaticVLANRequest;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.wrapper.DeleteVLANConfigurationRequest;

/**
 * @author Isart Canyameres
 * @author Jordi Puig
 * @author Eduard Grasa
 */
public class VLANAwareBridgeCapabilityServiceImpl extends VLANAwareBridgeCapability implements IVLANAwareBridgeCapabilityService {

	private static final Logger	LOGGER	= Logger.getLogger(VLANAwareBridgeCapabilityServiceImpl.class);

	/**
	 * @param descriptor
	 * @param resourceId
	 */
	public VLANAwareBridgeCapabilityServiceImpl(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor, resourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.IVLANAwareBridgeCapabilityService#createVLANConfiguration(org.opennaas.extensions
	 * .capability.macbridge.vlanawarebridge.ws.wrapper.CreateVLANConfigurationRequest)
	 */
	@Override
	public Response createVLANConfiguration(CreateVLANConfigurationRequest request) throws CapabilityException {
		LOGGER.info("Calling createVLANConfiguration service");
		createVLANConfiguration(request.getVlanConfiguration());
		LOGGER.info("End of createVLANConfiguration call");
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.IVLANAwareBridgeCapabilityService#deleteVLANConfiguration(org.opennaas.extensions
	 * .capability.macbridge.vlanawarebridge.ws.wrapper.DeleteVLANConfigurationRequest)
	 */
	@Override
	public Response deleteVLANConfiguration(DeleteVLANConfigurationRequest request) throws CapabilityException {
		LOGGER.info("Calling deleteVLANConfiguration service");
		deleteVLANConfiguration(request.getVlanId());
		LOGGER.info("End of deleteVLANConfiguration call");
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.IVLANAwareBridgeCapabilityService#addStaticVLAN(org.opennaas.extensions.capability
	 * .macbridge.vlanawarebridge.ws.wrapper.AddStaticVLANRequest)
	 */
	@Override
	public Response addStaticVLAN(AddStaticVLANRequest request) throws CapabilityException {
		LOGGER.info("Calling addStaticVLAN service");
		addStaticVLANRegistrationEntryToFilteringDatabase(request.getVlanRegistration());
		LOGGER.info("End of addStaticVLAN call");
		return Response.ok().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennaas.extensions.capability.macbridge.vlanawarebridge.ws.IVLANAwareBridgeCapabilityService#deleteStaticVLAN(org.opennaas.extensions.
	 * capability.macbridge.vlanawarebridge.ws.wrapper.DeleteStaticVLANRequest)
	 */
	@Override
	public Response deleteStaticVLAN(DeleteStaticVLANRequest request) throws CapabilityException {
		LOGGER.info("Calling deleteStaticVLAN service");
		deleteStaticVLANRegistrationEntryFromFilteringDatabase(request.getVlanId());
		LOGGER.info("End of deleteStaticVLAN call");
		return Response.ok().build();
	}
}
