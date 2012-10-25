/**
 * 
 */
package org.opennaas.web.services.rest.vcpe;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.web.services.rest.GenericRestService;
import org.opennaas.web.services.rest.RestServiceException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Jordi
 */
public class BuilderCapabilityService extends GenericRestService {

	private static final Logger	LOGGER	= Logger.getLogger(BuilderCapabilityService.class);

	/**
	 * Call a rest url to build a VCPENetwork environment
	 * 
	 * @param request
	 * @return true if the environment has been created
	 * @throws RestServiceException
	 */
	public Boolean updateIpsVCPENetwork(VCPENetworkModel request) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling update VCPENetworkBuilder service");
			String url = getURL("vcpenet/" + request.getVcpeNetworkName() + "/vcpenet_builder/updateIps");
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
