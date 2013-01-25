/**
 * 
 */
package org.opennaas.gui.vcpe.services.rest.vcpe;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.gui.vcpe.services.rest.GenericRestService;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;

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
	 * Call a rest service to create a VCPENetwork resource
	 * 
	 * @param request
	 * @return true if the environment has been created
	 * @throws RestServiceException
	 */
	public String logicalForm(VCPENetworkModel request) throws RestServiceException {
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
		return checkResponse(response) ? response.getEntity(String.class) : null;
	}

	/**
	 * Call a rest service to destroy a VCPENetwork resource
	 * 
	 * @param vcpeNetworkId
	 * @throws RestServiceException
	 */
	public Boolean destroyVCPENetwork(String vcpeNetworkId) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling remove VCPENetworkManager service");
			String url = getURL("vcpenetwork/remove/" + vcpeNetworkId);
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
	 * Call a rest service to get a VCPENetworkModel by id = id
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
	 * Call a rest service to get all VCPE Network
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

	/**
	 * Call a rest service to get the Physical Infrastructure
	 * 
	 * @return the physical infrastructure
	 * @throws RestServiceException
	 */
	public VCPENetworkModel getPhysicalInfrastructure(String templateType) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling getPhyInfrastructure VCPENetworkManager service");
			String url = getURL("vcpenetwork/getPhyInfrastructureSuggestion?templateType=" + templateType);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Physical Infrastructure recovered");
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? response.getEntity(VCPENetworkModel.class) : null;
	}

	/**
	 * Call a rest service to get the Logical Infrastructure
	 * 
	 * @return the logical infrastructure
	 * @throws RestServiceException
	 */
	public VCPENetworkModel getLogicalInfrastructure(VCPENetworkModel physicalInfrastructure) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling getLogicalInfrastructureSuggestion VCPENetworkManager service");
			String url = getURL("vcpenetwork/getLogicalInfrastructureSuggestion");
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, physicalInfrastructure);
			LOGGER.info("Logical Infrastructure recovered");
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? response.getEntity(VCPENetworkModel.class) : null;
	}

	/**
	 * Call a rest service to check if the VLAN is free in the environment
	 * 
	 * @param vcpeId
	 * @param vlan
	 * @param ifaceName
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isVLANFree(String vcpeId, String router, String vlan, String ifaceName) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling isVLANFree VCPENetworkManager service");
			String url = getURL("vcpenetwork/isVLANFree?vcpeId=" + vcpeId + "&router=" + router + "&vlan=" + vlan + "&ifaceName=" + ifaceName);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("VLAN is free: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? Boolean.valueOf(response.getEntity(String.class)) : null;
	}

	/**
	 * Call a rest service to check if the IP is free in the environment
	 * 
	 * @param vcpeId
	 * @param ip
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isIPFree(String vcpeId, String router, String ip) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling isIPFree VCPENetworkManager service");
			String url = getURL("vcpenetwork/isIPFree?vcpeId=" + vcpeId + "&router=" + router + "&ip=" + ip);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("IP is free: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? Boolean.valueOf(response.getEntity(String.class)) : null;
	}

	/**
	 * Call a rest service to check if the IP is free in the environment
	 * 
	 * @param vcpeId
	 * @param iface
	 * @param port
	 * @return true if the iface is free
	 * @throws RestServiceException
	 */
	public Boolean isInterfaceFree(String vcpeId, String router, String iface, String port) throws RestServiceException {
		ClientResponse response = null;
		try {
			LOGGER.info("Calling isVLANFree VCPENetworkManager service");
			String url = getURL("vcpenetwork/isInterfaceFree?vcpeId=" + vcpeId + "&router=" + router + "&iface=" + iface + "." + port);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Interface is free: " + response);
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return checkResponse(response) ? Boolean.valueOf(response.getEntity(String.class)) : null;
	}

}
