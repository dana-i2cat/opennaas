package org.opennaas.client.rest;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.network.capability.queue.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class NetQueueTest {

	private static final Logger	LOGGER	= Logger.getLogger(NetQueueTest.class);

	public static void main(String[] args) {
		execute();
	}

	/**
	 * 
	 */
	private static void execute() {
		String url = "http://localhost:8888/opennaas/network/networkdemo/netqueue/execute";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			Response response = webResource.type(MediaType.APPLICATION_XML).get(Response.class);
			LOGGER.info("Number of responses: " + response.getResponse().size());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
}