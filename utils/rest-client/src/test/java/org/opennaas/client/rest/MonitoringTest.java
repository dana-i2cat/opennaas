package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MonitoringTest {

	private static final Logger	LOGGER	= Logger.getLogger(MonitoringTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		clearAlarms();
		// getAlarms();
	}

	/**
	 * 
	 */
	private static void clearAlarms() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/roadm/pedrosa/monitoring/clearAlarms";

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	// /**
	// *
	// */
	// private static void getAlarms() {
	// String url = "http://localhost:8888/opennaas/roadm/pedrosa/monitoring/getAlarms";
	// try {
	// Client client = Client.create();
	// WebResource webResource = client.resource(url);
	// List<ResourceAlarm> response = webResource.type(MediaType.APPLICATION_XML)
	// .post(new GenericType<List<ResourceAlarm>>() {
	// });
	// LOGGER.info("El número de resource alarm es de: " + response.size());
	// } catch (Exception e) {
	// LOGGER.error(e.getMessage());
	// }
	// }

}