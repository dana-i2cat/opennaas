package net.i2cat.nexus.tests;

import java.util.ArrayList;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class InitializerTestHelper {
	public static void stopResources(IResourceManager resourceManager) throws ResourceException {
		/* delete used resources in the resource Manager */
		for (IResource resource : resourceManager.listResources()) {
			resourceManager.stopResource(resource.getResourceIdentifier());
		}
	}

	public static void removeResources(IResourceManager resourceManager) throws ResourceException {
		/* delete used resources in the resource Manager */
		for (IResource resource : resourceManager.listResources()) {
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

}
