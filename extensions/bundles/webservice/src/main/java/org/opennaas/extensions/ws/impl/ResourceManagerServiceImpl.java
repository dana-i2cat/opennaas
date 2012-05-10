package org.opennaas.extensions.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.extensions.ws.services.IResourceManagerService;

/**
 * @author Isart Canyameres
 */
@WebService
public class ResourceManagerServiceImpl implements IResourceManagerService {

	private IResourceManager	resourceManager;

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public ResourceIdentifier createResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		return (ResourceIdentifier) resourceManager.createResource(resourceDescriptor).getResourceIdentifier();
	}

	@Override
	public ResourceIdentifier modifyResource(ResourceIdentifier resourceIdentifier, ResourceDescriptor resourceDescriptor) throws ResourceException {
		return (ResourceIdentifier) resourceManager.modifyResource(resourceIdentifier, resourceDescriptor).getResourceIdentifier();
	}

	@Override
	public void removeResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		resourceManager.removeResource(resourceIdentifier);
	}

	@Override
	public List<ResourceIdentifier> listResourcesByType(String type) {
		List<IResource> resources = resourceManager.listResourcesByType(type);
		return getIdentifiers(resources);
	}

	@Override
	public List<ResourceIdentifier> listResources() {
		List<IResource> resources = resourceManager.listResources();
		return getIdentifiers(resources);
	}

	@Override
	public List<String> getResourceTypes() {
		return resourceManager.getResourceTypes();
	}

	@Override
	public ResourceDescriptor getResourceDescriptor(ResourceIdentifier resourceIdentifier) throws ResourceException {
		return resourceManager.getResource(resourceIdentifier).getResourceDescriptor();
	}

	@Override
	public void startResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		resourceManager.startResource(resourceIdentifier);
	}

	@Override
	public void stopResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		resourceManager.stopResource(resourceIdentifier);
	}

	@Override
	public void exportResourceDescriptor(ResourceIdentifier resourceIdentifier, String fileName) throws ResourceException {
		resourceManager.exportResourceDescriptor(resourceIdentifier, fileName);
	}

	@Override
	public ResourceIdentifier getIdentifierFromResourceName(String resourceType, String resourceName) throws ResourceException {
		return (ResourceIdentifier) resourceManager.getIdentifierFromResourceName(resourceType, resourceName);
	}

	@Override
	public String getNameFromResourceID(String ID) throws ResourceException {
		return resourceManager.getNameFromResourceID(ID);
	}

	@Override
	public void forceStopResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		resourceManager.forceStopResource(resourceIdentifier);
	}

	private List<ResourceIdentifier> getIdentifiers(List<IResource> resources) {
		List<ResourceIdentifier> ids = new ArrayList<ResourceIdentifier>(resources.size());
		for (IResource resource : resources) {
			ids.add((ResourceIdentifier) resource.getResourceIdentifier());
		}
		return ids;
	}
}
