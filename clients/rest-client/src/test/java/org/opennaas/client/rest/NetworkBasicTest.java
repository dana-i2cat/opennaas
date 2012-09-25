package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.AddResourceRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.L2AttachRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.L2DettachRequest;
import org.opennaas.extensions.network.capability.basic.ws.wrapper.RemoveResourceRequest;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class NetworkBasicTest {

	private static final Logger	LOGGER	= Logger.getLogger(NetworkBasicTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		addResource();
		l2attach();
		l2detach();
		removeResource();
	}

	/**
	 *
	 */
	private static void l2attach() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/basicNetwork/l2attach";

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getL2AttachRequest());
			LOGGER.info("OK!: " + response.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 *
	 */
	private static void addResource() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/basicNetwork/addResource";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			AddResourceRequest request = new AddResourceRequest();
			request.setResourceId("05a00fe3-be5c-4d32-8557-c65eb30be595");
			response = webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
					.post(ClientResponse.class, request);
			LOGGER.info("OK!: " + response.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 *
	 */
	private static void removeResource() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/basicNetwork/removeResource";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			RemoveResourceRequest request = new RemoveResourceRequest();
			request.setResourceId("05a00fe3-be5c-4d32-8557-c65eb30be595");
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, request);
			LOGGER.info("OK!: " + response.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 *
	 */
	private static void l2detach() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/basicNetwork/l2detach";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getL2DettachRequest());
			LOGGER.info("OK!: " + response.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static L2AttachRequest getL2AttachRequest() {
		L2AttachRequest request = new L2AttachRequest();
		Link link = new Link();
		request.setLink(link);
		Interface interface1 = new Interface();
		Interface interface2 = new Interface();
		interface1.setName("router:lolaM20:fe-0/0/2.22");
		interface2.setName("router:lolaM20:192.168.14.1");
		link.setSource(interface1);
		link.setSink(interface1);
		return request;
	}

	/**
	 * @return
	 */
	private static L2DettachRequest getL2DettachRequest() {
		L2DettachRequest request = new L2DettachRequest();
		Link link = new Link();
		request.setLink(link);
		Interface interface1 = new Interface();
		Interface interface2 = new Interface();
		interface1.setName("router:lolaM20:fe-0/0/2.22");
		interface2.setName("router:lolaM20:192.168.14.1");
		link.setSource(interface1);
		link.setSink(interface1);
		return request;
	}

}