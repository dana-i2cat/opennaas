package org.opennaas.client.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class QueueManagerTest {

	private static final Logger	LOGGER	= Logger.getLogger(QueueManagerTest.class);

	public static void main(String[] args) {
		listIds();
		list();
		modify();
		clear();
		execute();
	}

	private static void listIds() {
		String url = "http://localhost:8888/opennaas/router/lolaM20/queue/listIds";

		GenericType<List<String>> genericType =
				new GenericType<List<String>>() {
				};

		List<String> response;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).post(genericType);

			LOGGER.info("OK!");
			LOGGER.info("Queue has " + response.size() + " actions:");
			for (String id : response) {
				LOGGER.info(id);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

	}

	private static void list() {
		String url = "http://localhost:8888/opennaas/router/lolaM20/queue/list";

		GenericType<List<IAction>> genericType =
				new GenericType<List<IAction>>() {
				};

		List<IAction> response;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).get(genericType);

			LOGGER.info("OK!");
			LOGGER.info("Queue has " + response.size() + " actions:");
			for (IAction a : response) {
				LOGGER.info(a.getActionID());
			}

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

			LOGGER.info("Execution state: " + response.isOk());
			LOGGER.info("Elapsed time: " + response.getTotalTime());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static ModifyParams generateModifyParams() {
		return ModifyParams.newRemoveOperation(0);
	}

}
