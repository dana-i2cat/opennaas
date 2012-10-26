/**
 * 
 */
package org.opennaas.web.services;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Jordi
 */
public class VCPENetworkService extends GenericRestService {

	private static final Logger	LOGGER	= Logger.getLogger(VCPENetworkService.class);

	/**
	 * Call a rest url to build a VCPENetwork environment
	 * 
	 * @param request
	 * @return true if the environment has been created
	 * @throws RestServiceException
	 */
	public Boolean buildVCPENetwork(VCPENetworkModel request) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling create VCPENetworkManager service");
			String url = getURL("vcpenetwork/build");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, request);
			LOGGER.info("VCPENetwork created: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to destroy a VCPENetwork environment
	 * 
	 * @param vcpeNetworkId
	 * @throws RestServiceException
	 */
	public Boolean destroyVCPENetwork(String vcpeNetworkId) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling remove VCPENetworkManager service");
			String url = getURL("vcpenetwork/destroy/" + vcpeNetworkId);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("VCPENetwork created: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to get a VCPENetworkModel by id = id
	 * 
	 * @param id
	 * @return VCPENetwork
	 * @throws RestServiceException
	 */
	public VCPENetworkModel getVCPENetworkById(String id) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling getById VCPENetworkManager service");
			String url = getURL("vcpenetwork/getVCPENetworkById/" + id);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("VCPENetwork recovered");
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? response.getEntity(VCPENetworkModel.class) : null;
	}

	/**
	 * Call a rest url to get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 * @throws RestServiceException
	 * @throws UniformInterfaceException
	 * @throws ClientHandlerException
	 */
	public List<VCPENetworkModel> getAllVCPENetworks() throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling getAllVCPENetworks service");
			String url = getURL("vcpenetwork/getAllVCPENetworks");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("List of VCPENetwork recovered");
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? response.getEntity(new GenericType<List<VCPENetworkModel>>() {
		}) : null;
	}
}
