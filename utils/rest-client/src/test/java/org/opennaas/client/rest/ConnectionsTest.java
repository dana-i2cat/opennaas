package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ConnectionsTest {

	private static final Logger	LOGGER	= Logger.getLogger(ConnectionsTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		makeConnection();
		removeConnection();
	}

	/**
	 * 
	 */
	private static void makeConnection() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/roadm/pedrosa/connections/makeConnection";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getFiberConnection());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void removeConnection() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/roadm/pedrosa/connections/removeConnection";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getFiberConnection());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static FiberConnection getFiberConnection() {
		FiberConnection connection = new FiberConnection();
		FCPort fcPort = new FCPort();
		fcPort.setCaption("MyCaption");
		connection.setDstPort(fcPort);
		return connection;
	}
}