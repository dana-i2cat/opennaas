package org.opennaas.extensions.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	Log							log	= LogFactory.getLog(ResourceManagerServiceImpl.class);
	private IResourceManager	resourceManager;

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public ResourceIdentifier createResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		log.info("Start of createResource call");
		ResourceIdentifier resourceIdentifier = (ResourceIdentifier) resourceManager.createResource(resourceDescriptor).getResourceIdentifier();
		log.info("End of createResource call");
		return resourceIdentifier;
	}

	@Override
	public ResourceIdentifier modifyResource(ResourceIdentifier resourceIdentifier, ResourceDescriptor resourceDescriptor) throws ResourceException {
		log.info("Start of modifyResource call");
		ResourceIdentifier id = (ResourceIdentifier) resourceManager.modifyResource(resourceIdentifier, resourceDescriptor).getResourceIdentifier();
		log.info("End of modifyResource call");
		return id;
	}

	@Override
	public void removeResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		log.info("Start of removeResource call");
		resourceManager.removeResource(resourceIdentifier);
		log.info("Start of removeResource call");
	}

	@Override
	public List<ResourceIdentifier> listResourcesByType(String type) {
		log.info("Start of listResourcesByType call");
		List<IResource> resources = resourceManager.listResourcesByType(type);
		List<ResourceIdentifier> ids = getIdentifiers(resources);
		log.info("End of listResourcesByType call");
		return ids;

	}

	@Override
	public List<ResourceIdentifier> listResources() {
		log.info("Start of listResources call");
		List<IResource> resources = resourceManager.listResources();
		List<ResourceIdentifier> ids = getIdentifiers(resources);
		log.info("End of listResources call");
		return ids;
	}

	@Override
	public List<String> getResourceTypes() {
		log.info("Start of getResourceTypes call");
		List<String> resourceTypes = resourceManager.getResourceTypes();
		log.info("End of getResourceTypes call");
		return resourceTypes;
	}

	@Override
	public ResourceDescriptor getResourceDescriptor(ResourceIdentifier resourceIdentifier) throws ResourceException {
		log.info("Start of getResourceDescriptor call");
		ResourceDescriptor resDescriptor = resourceManager.getResource(resourceIdentifier).getResourceDescriptor();
		log.info("End of getResourceDescriptor call");
		return resDescriptor;
	}

	@Override
	public void startResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		log.info("Start of startResource call");
		resourceManager.startResource(resourceIdentifier);
		log.info("End of startResource call");
	}

	@Override
	public void stopResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		log.info("Start of stopResource call");
		resourceManager.stopResource(resourceIdentifier);
		log.info("End of stopResource call");
	}

	@Override
	public void exportResourceDescriptor(ResourceIdentifier resourceIdentifier, String fileName) throws ResourceException {
		log.info("Start of exportResourceDescriptor call");
		resourceManager.exportResourceDescriptor(resourceIdentifier, fileName);
		log.info("End of exportResourceDescriptor call");

	}

	@Override
	public ResourceIdentifier getIdentifierFromResourceName(String resourceType, String resourceName) throws ResourceException {
		log.info("Start of getIdentifierFromResourceName call");
		ResourceIdentifier resIdentifier = (ResourceIdentifier) resourceManager.getIdentifierFromResourceName(resourceType, resourceName);
		log.info("End of getIdentifierFromResourceName call");
		return resIdentifier;
	}

	@Override
	public String getNameFromResourceID(String ID) throws ResourceException {
		log.info("Start of getNameFromResourceID call");
		String name = resourceManager.getNameFromResourceID(ID);
		log.info("End of getNameFromResourceID call");
		return name;
	}

	@Override
	public void forceStopResource(ResourceIdentifier resourceIdentifier) throws ResourceException {
		log.info("Start of forceStopResource call");
		resourceManager.forceStopResource(resourceIdentifier);
		log.info("End of forceStopResource call");
	}

	private List<ResourceIdentifier> getIdentifiers(List<IResource> resources) {
		log.info("Start of getIdentifiers call");
		List<ResourceIdentifier> ids = new ArrayList<ResourceIdentifier>(resources.size());
		for (IResource resource : resources) {
			ids.add((ResourceIdentifier) resource.getResourceIdentifier());
		}
		log.info("End of getIdentifiers call");
		return ids;
	}

}
