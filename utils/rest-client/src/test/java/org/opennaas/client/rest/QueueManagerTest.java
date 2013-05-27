package org.opennaas.client.rest;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class QueueManagerTest {

	private static final Logger	LOGGER	= Logger.getLogger(QueueManagerTest.class);

	public static void main(String[] args) {
		list();
		modify();
		clear();
		execute();
	}

	private static void list() {
		String url = "http://localhost:8888/opennaas/router/lolaM20/queue/getActionsId";
		String response = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
			LOGGER.info("Response: " + response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static void modify() {
		String url = "http://localhost:8888/opennaas/router/lolaM20/queue/modify";
		ClientResponse response;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, generateModifyParams());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static void clear() {
		String url = "http://localhost:8888/opennaas/router/lolaM20/queue/clear";
		ClientResponse response;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void execute() {
		String url = "http://localhost:8888/opennaas/router/lolaM20/queue/execute";
		QueueResponse response;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).post(QueueResponse.class);
			LOGGER.info("Execution state: " + response.isOk() + " - " + "Elapsed time: " + response.getTotalTime());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static ModifyParams generateModifyParams() {
		return ModifyParams.newRemoveOperation(0);
	}

}
