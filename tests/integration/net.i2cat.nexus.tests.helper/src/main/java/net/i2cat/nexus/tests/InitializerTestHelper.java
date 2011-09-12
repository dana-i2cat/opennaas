package net.i2cat.nexus.tests;

import java.util.ArrayList;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

public class InitializerTestHelper {
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
