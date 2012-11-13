package org.opennaas.client.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class VCPENetworkManagerTest {

	private static final Logger	LOGGER	= Logger.getLogger(VCPENetworkManagerTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		create();
		getVCPENetworkById();
		getAllVCPENetworks();
	}

	/**
	 * 
	 */
	private static void getAllVCPENetworks() {
		List<VCPENetworkModel> response = null;
		String url = "http://localhost:8888/opennaas/vcpenetwork/getAllVCPENetworks";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(new GenericType<List<VCPENetworkModel>>() {
			});
			LOGGER.info("List of VCPENetwork recovered with: " + response.size() + " items");
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void getVCPENetworkById() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/vcpenetwork/getVCPENetworkById/e6a1f44a-4bd5-40bf-8189-d795de03e4f4";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response: " + response);
			VCPENetworkModel model = response.getEntity(VCPENetworkModel.class);
			model.hashCode();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void create() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/vcpenetwork/create";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, getRequest());
			LOGGER.info("Response: " + response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static VCPENetworkModel getRequest() {
		VCPENetworkModel request = new VCPENetworkModel();
		request.setVcpeNetworkId("name");
		List<VCPENetworkElement> elements = new ArrayList<VCPENetworkElement>();
		request.setElements(elements);
		elements.add(new Router());
		elements.add(new Router());
		return request;
	}
}