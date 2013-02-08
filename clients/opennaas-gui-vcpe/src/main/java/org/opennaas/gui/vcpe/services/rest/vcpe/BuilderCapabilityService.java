/**
 * 
 */
package org.opennaas.gui.vcpe.services.rest.vcpe;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.gui.vcpe.services.rest.GenericRestService;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;

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
	 * Call a rest url to update the ips
	 * 
	 * @param request
	 * @return true if the environment has been created
	 * @throws RestServiceException
	 */
	public Boolean updateIpsVCPENetwork(VCPENetworkModel request) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling update ips VCPENetworkBuilderCapability service");
			String url = getURL("vcpenet/" + request.getName() + "/vcpenet_ip/updateIps");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, request);
			LOGGER.info("ips updated: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to update the vrrp ip address
	 * 
	 * @param vcpeNetwork
	 * @return true if the vrrp ip address has been created
	 * @throws RestServiceException
	 */
	public Boolean updateVRRPIp(VCPENetworkModel request) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling update vrrp ip VCPENetworkBuilderCapability service");
			String url = getURL("vcpenet/" + request.getName() + "/vcpenet_vrrp/updateVRRPIp");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, request);
			LOGGER.info("vrrp ip updated: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response);
	}

	/**
	 * Call a rest url to change the priority vrrp
	 * 
	 * @param vcpeNetwork
	 * @return new model
	 * @throws RestServiceException
	 */
	public VCPENetworkModel changeVRRPPriority(VCPENetworkModel request) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling change priority vrrp VCPENetworkBuilderCapability service");
			String url = getURL("vcpenet/" + request.getName() + "/vcpenet_vrrp/changeVRRPPriority");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, request);
			LOGGER.info("priority vrrp changed: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? response.getEntity(VCPENetworkModel.class) : null;
	}

}
