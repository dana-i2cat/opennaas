package org.opennaas.extensions.macbridge.ios.resource.repository;

import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MACBridgeIOSRepository extends ResourceRepository {

	Log	log	= LogFactory.getLog(MACBridgeIOSRepository.class);

	@Override
	protected void checkResourceCanBeStarted(IResource resource)
			throws ResourceException {
		checkResourceHasAnAssociatedContext(resource);
		super.checkResourceCanBeStarted(resource);
	}

	private void checkResourceHasAnAssociatedContext(IResource resource) throws ResourceException {
		IProtocolSessionManager sessionManager;
		try {
			sessionManager = getProtocolSessionManager(resource.getResourceDescriptor().getId());
			if (sessionManager.getRegisteredContexts().isEmpty()) {
				String name = resource.getResourceDescriptor().getInformation().getName();
				String type = resource.getResourceDescriptor().getInformation().getType();
				String resourceId = type+":"+name;
				throw new ResourceException(
						"There is no session context for resource " + resourceId + ". A session context is needed for the resource to start.");
			}
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException("Error loading session manager: ", e);
		}
	}

	public MACBridgeIOSRepository(String resourceType) {
		super(resourceType);
	}

	public void capabilityFactoryAdded(ICapabilityFactory capabilityFactory) {
		log.info("Adding factory: " + capabilityFactory.getType());
		this.capabilityFactories.put(capabilityFactory.getType(), capabilityFactory);
	}

	public void capabilityFactoryDeleted(ICapabilityFactory capabilityFactory) {
		log.info("Deleting factory: " + capabilityFactory.getType());
		this.capabilityFactories.remove(capabilityFactory.getType());
	}

	private IProtocolSessionManager getProtocolSessionManager(String resourceId) throws Exception {
		IProtocolManager protocolManager = Activator.getProtocolManagerService();
		return protocolManager.getProtocolSessionManager(resourceId);
	}


}
