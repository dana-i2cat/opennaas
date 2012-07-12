package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkConnection;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class NetworkBasicTest {

	private static final Logger	LOGGER	= Logger.getLogger(NetworkBasicTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		addResource();
		removeResource();
		l2attach();
		l2detach();
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
			response = webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
					.post(ClientResponse.class, "f90b386c-87be-40bd-bfa6-45d9f10a69ac");
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
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, "f90b386c-87be-40bd-bfa6-45d9f10a69ac");
			LOGGER.info("OK!: " + response.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 *
	 */
	private static void l2attach() {
		NetworkConnection response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/basicNetwork/l2attach";

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML).post(NetworkConnection.class, getInterfaces());
			LOGGER.info("Network Connection: " + response.getName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static Link getInterfaces() {
		Link link = new Link();
		Interface interface1 = new Interface();
		Interface interface2 = new Interface();
		interface1.setName("router:lolaM20:fe-0/0/2.22");
		interface2.setName("router:lolaM20:192.168.14.1");
		link.setSource(interface1);
		link.setSink(interface1);
		return link;
	}

	/**
	 *
	 */
	private static void l2detach() {
		NetworkConnection response = null;
		String url = "http://localhost:8888/opennaas/network/networkdemo/basicNetwork/l2detach";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML).post(NetworkConnection.class, getInterfaces());
			LOGGER.info("Network Connection: " + response.getName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

}