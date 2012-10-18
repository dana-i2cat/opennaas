/**
 * 
 */
package org.opennaas.web.services;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.web.entities.VCPENetwork;
import org.opennaas.web.services.rest.RestServiceException;
import org.opennaas.web.utils.Constants;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Jordi
 */
public class ResourceService extends GenericRestService {

	private static final Logger	LOGGER	= Logger.getLogger(ResourceService.class);

	/**
	 * Call a rest url to stop a resource
	 * 
	 * @param id
	 * @return true if resource has been stopped
	 * @throws RestServiceException
	 */
	public Boolean stopResource(String id) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling stop resource service");
			String url = getURL("resources/stop/" + id);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to start a resource
	 * 
	 * @param id
	 * @return true if resource has been stopped
	 * @throws RestServiceException
	 */
	public Boolean startResource(String id) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling start resource service");
			String url = getURL("resources/start/" + id);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to create a resource
	 * 
	 * @param descriptor
	 * @return id
	 * @throws RestServiceException
	 */
	public String createResource(ResourceDescriptor descriptor) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling create resource service");
			String url = getURL("resources/create");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, descriptor);
			LOGGER.info("Created resource with id: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? response.getEntity(String.class) : null;
	}

	/**
	 * Call a rest url to update a resource
	 * 
	 * @param descriptor
	 * @return true if resource has been updated
	 * @throws RestServiceException
	 */
	public Boolean updateResource(ResourceDescriptor descriptor) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling update resource service");
			String url = getURL("resources/modify");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.post(ClientResponse.class, descriptor);
			LOGGER.info("Updated resource with id: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to delete a resource
	 * 
	 * @param id
	 * @throws RestServiceException
	 */
	public Boolean deleteResource(String id) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling delete resource service");
			String url = getURL("resources/delete/" + id);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to get a resource by id = id
	 * 
	 * @param id
	 * @return VCPENetwork
	 */
	public Resource getResourceById(String id) {
		Resource response = null;
		try {
			LOGGER.info("Calling getById resource service");
			String url = getURL("resources/getResource/" + id);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(Resource.class);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

	/**
	 * Call a rest url to get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 */
	public List<VCPENetwork> getAllResources() {
		List<VCPENetwork> response = null;
		try {
			LOGGER.info("Calling getAllResources service");
			String url = getURL("resources/getAllResources/" + Constants.RESOURCE_VCPENET_TYPE);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(new GenericType<List<VCPENetwork>>() {
			});
			LOGGER.info("Returned: " + response.size() + " VCPENetwork");
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}
}
