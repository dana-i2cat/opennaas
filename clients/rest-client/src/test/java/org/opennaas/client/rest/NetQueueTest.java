package org.opennaas.client.rest;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.queue.QueueResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
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
		GenericType<Map<String, QueueResponse>> genericType =
				new GenericType<Map<String, QueueResponse>>() {
				};

		String url = "http://localhost:8888/opennaas/network/networkdemo/netqueue/execute";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			webResource.type(MediaType.APPLICATION_XML).get(genericType);
			LOGGER.info("OK!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

}