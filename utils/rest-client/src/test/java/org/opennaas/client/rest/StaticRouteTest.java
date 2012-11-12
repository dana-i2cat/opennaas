package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class StaticRouteTest {

	private static final Logger	LOGGER	= Logger.getLogger(NetOSPFTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		create();
	}

	/**
	 * 
	 */
	private static void create() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/staticRoute/create?netIdIpAdress=192.0.0.1&maskIpAdress=128.0.0.1&nextHopIpAddress=0.0.0.0";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
}