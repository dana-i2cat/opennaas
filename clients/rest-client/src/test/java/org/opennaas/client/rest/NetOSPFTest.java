package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class NetOSPFTest {

	private static final Logger	LOGGER	= Logger.getLogger(NetOSPFTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		activate();
		deactivate();
	}

	/**
	 * 
	 */
	private static void activate() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/netospf/activate";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void deactivate() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/netospf/deactivate";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
}