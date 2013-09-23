package org.opennaas.extensions.openflowswitch.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OpenflowSwitchRepository extends ResourceRepository {

	Log	log	= LogFactory.getLog(OpenflowSwitchRepository.class);

	public OpenflowSwitchRepository(String resourceType) {
		super(resourceType);
	}

	@Override
	protected void checkResourceCanBeStarted(IResource resource)
			throws ResourceException {
		checkResourceHasAnAssociatedContext(resource);
		super.checkResourceCanBeStarted(resource);
	}

	public void capabilityFactoryAdded(ICapabilityFactory capabilityFactory) {
		log.info("Adding factory: " + capabilityFactory.getType());
		this.capabilityFactories.put(capabilityFactory.getType(), capabilityFactory);
	}

	public void capabilityFactoryDeleted(ICapabilityFactory capabilityFactory) {
		if (capabilityFactory != null) {
			log.info("Deleting factory: " + capabilityFactory.getType());
			this.capabilityFactories.remove(capabilityFactory.getType());
		}
	}

	private void checkResourceHasAnAssociatedContext(IResource resource) throws ResourceException {
		IProtocolSessionManager sessionManager;
		try {

			String name = resource.getResourceDescriptor().getInformation().getName();
			String type = resource.getResourceDescriptor().getInformation().getType();
			String resourceId = type + ":" + name;

			sessionManager = getProtocolSessionManager(resource.getResourceDescriptor().getId());
			if (sessionManager.getRegisteredContexts().isEmpty())
				throw new ResourceException(
						"There is no session context for resource " + resourceId + ". A session context is needed for the resource to start.");

			IProtocolSession session = sessionManager.obtainSessionByProtocol("floodlight", false);
			ProtocolSessionContext sessionContext = session.getSessionContext();

			if (!(sessionContext.getSessionParameters().containsKey("protocol.floodlight.switchid")))
				throw new ResourceException("There is no switch id in resource " + resourceId + " session context.");

		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException("Error loading session manager: ", e);
		}
	}

	private IProtocolSessionManager getProtocolSessionManager(String resourceId) throws Exception {
		IProtocolManager protocolManager = Activator.getProtocolManagerService();
		return protocolManager.getProtocolSessionManager(resourceId);
	}

}
