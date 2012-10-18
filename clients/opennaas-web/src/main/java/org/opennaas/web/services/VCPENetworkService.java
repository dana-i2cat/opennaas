/**
 * 
 */
package org.opennaas.web.services;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.web.services.rest.RestServiceException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Jordi
 */
public class VCPENetworkService extends GenericRestService {

	private static final Logger	LOGGER	= Logger.getLogger(VCPENetworkService.class);

	/**
	 * Call a rest url to create a VCPENetwork infrastructure
	 * 
	 * @param createVCPENetRequest
	 * 
	 * @param id
	 * @return String
	 * @throws RestServiceException
	 */
	public Boolean createVCPENetwork(VCPENetworkModel request) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling create VCPENetworkManager service");
			String url = getURL("vcpenetwork/create");
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
}
