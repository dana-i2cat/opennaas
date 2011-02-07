package net.i2cat.mantychore.core.resourcemanager.soapendpoint;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import net.i2cat.mantychore.core.resources.ResourceException;
import net.i2cat.mantychore.core.resources.descriptor.ResourceDescriptor;

@WebService
public interface IResourceManagerSOAPEndpoint {

	/**
	 * Create a new resource with a given resourceDescriptor
	 * @param resourceDescriptor
	 * @returns the new resource data
	 * @throws ResourceException
	 */
	@WebMethod
    public ResourceData createResource(ResourceDescriptor resourceDescriptor) throws ResourceException;
	
	/**
	 * Modify the existing resource that matches the id
	 * @param resourceDescriptor
	 * @return the modified engine
	 * @throws ResourceException
	 */
	@WebMethod
	public ResourceData modifyResource(ResourceDescriptor resourceDescriptor) throws ResourceException;
	
	/**
	 * Remove the existing resource that matches the id
	 * @param type: resource type
	 * @param id: resource id
	 * @throws ResourceException
	 */
	@WebMethod
	public void removeResource(String type, String id) throws ResourceException;
	
	/**
	 * List all resources in container by type. 
	 * @return
	 */
	@WebMethod
	public List<ResourceData> listReourcesByType(String type);
	
	/**
	 * List all resources in container. 
	 * @return
	 */
	@WebMethod
	public List<ResourceData> listResources();
	
	/**
	 * Get an existing resource
	 * @param type: resource type
	 * @param id: resource id
	 * @return the resource data
	 * @throws ResourceException
	 */
	@WebMethod 
	public ResourceData getResource(String type, String id) throws ResourceException;
	
	@WebMethod
	public void start(String type, String id) throws ResourceException;
	
	@WebMethod
	public void stop(String type, String id) throws ResourceException;
}