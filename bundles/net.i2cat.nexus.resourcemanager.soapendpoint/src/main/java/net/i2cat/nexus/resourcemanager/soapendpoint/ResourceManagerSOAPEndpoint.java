package net.i2cat.nexus.resourcemanager.soapendpoint;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceIdentifier;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

@WebService
public class ResourceManagerSOAPEndpoint implements IResourceManagerSOAPEndpoint {
	Logger logger = LoggerFactory.getLogger(ResourceManagerSOAPEndpoint.class);
    private IResourceManager resourceManager = null;
    
	public ResourceManagerSOAPEndpoint(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public ResourceManagerSOAPEndpoint(){
	}

	@WebMethod
	public ResourceData createResource(ResourceDescriptor resourceDescriptor) throws ResourceException{
		IResource resource = resourceManager.createResource(resourceDescriptor);
		return new ResourceData(resource);
	}
	
	@WebMethod
	public ResourceData modifyResource(ResourceDescriptor resourceDescriptor) throws ResourceException{
		IResource resource = resourceManager.modifyResource(resourceDescriptor);
		return new ResourceData(resource);
	}
	
	@WebMethod
	public void removeResource(String type, String id) throws ResourceException{
		resourceManager.removeResource(new ResourceIdentifier(type, id));
	}
	
	@WebMethod
	public List<ResourceData> listReourcesByType(String type){
		List<IResource> resources = resourceManager.listResourcesByType(type);
		return resourceToResourceData(resources);
	}
	
	private List<ResourceData> resourceToResourceData(List<IResource> resources){
		List<ResourceData> resourcesData = new ArrayList<ResourceData>();
		for(IResource resource:resources){
			resourcesData.add(new ResourceData(resource));
		}
		return resourcesData;
	}
	
	@WebMethod
	public List<ResourceData> listResources(){
		List<IResource> resources = resourceManager.listResources();
		return resourceToResourceData(resources);
	}
	
	@WebMethod 
	public ResourceData getResource(String type, String id) throws ResourceException{
		IResource resource = resourceManager.getResource(new ResourceIdentifier(type, id));
		return new ResourceData(resource);
	}
	
	@WebMethod
	public void start(String type, String id) throws ResourceException{
		IResource resource = resourceManager.getResource(new ResourceIdentifier(type, id));
		resource.start();
	}
	
	@WebMethod
	public void stop(String type, String id) throws ResourceException{
		IResource resource = resourceManager.getResource(new ResourceIdentifier(type, id));
		resource.stop();
	}
}