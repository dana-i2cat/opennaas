package org.opennaas.extensions.itests.helpers;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ILifecycle;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class InitializerTestHelper {

	/**
	 * Stops all resources in give resourceManager
	 * 
	 * @param resourceManager
	 * @throws ResourceException
	 *             if fails to stop a resource.
	 */
	public static void stopResources(IResourceManager resourceManager) throws ResourceException {

		for (IResource resource : resourceManager.listResources()) {
			resourceManager.stopResource(resource.getResourceIdentifier());
		}
	}

	/**
	 * Remove all resources from given resourceManager.
	 * 
	 * It stops active resources prior removing them.
	 * 
	 * @param resourceManager
	 * @throws ResourceException
	 *             if fails to remove (or stop) a resource.
	 */
	public static void removeResources(IResourceManager resourceManager) throws ResourceException {
		List<IResource> resources = resourceManager.listResources();
		for (int i = resources.size() - 1; i >= 0; i--) {
			IResource resource = resources.get(i);
			if (resource.getState().equals(ILifecycle.State.ACTIVE))
				resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	public static IResource initResource(String name, String type, ArrayList<String> capabilitiesId, IResourceManager resourceManager,
			IProtocolManager protocolManager, ProtocolSessionContext context) throws Exception {
		IResource resource = null;

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
				.newResourceDescriptor(name, type, capabilitiesId);

		resource = resourceManager.createResource(resourceDescriptor);

		// TODO Check this identifier
		IProtocolSessionManager protocolSessionManager = protocolManager
				.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(), context);

		return resource;

	}

	public static int containsCapability(IResource resource, String idCapability) {

		int pos = 0;
		for (ICapability capability : resource.getCapabilities()) {
			if (capability.getCapabilityInformation().getType().equals(idCapability)) {
				return pos;
			}
			pos++;
		}
		return -1;

	}

	public static Information getCapabilityInformation(String type) {
		Information information = new Information();
		information.setType(type);
		return information;
	}

	public static IProtocolSessionManager addSessionContext(IProtocolManager protocolManager, String resourceId, String resourceURI)
			throws ProtocolException {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, resourceURI);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");

		protocolSessionManager.registerContext(protocolSessionContext);

		return protocolSessionManager;
	}

}
