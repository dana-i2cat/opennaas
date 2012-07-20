package org.opennaas.client.rest;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.router.model.LogicalPort;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MacBridgeTest {

	private static final Logger	LOGGER	= Logger.getLogger(MacBridgeTest.class);

	public static void main(String[] args) {
		createVLANConfiguration();
		deleteVLANConfiguration();
		addStaticVLAN();
		deleteStaticVLAN();
	}

	/**
	 * 
	 */
	private static void createVLANConfiguration() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/MACBridgeIOS/catalyst/VLANAwareBridge/createVLANConfiguration";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new VLANConfiguration());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void deleteVLANConfiguration() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/MACBridgeIOS/catalyst/VLANAwareBridge/deleteVLANConfiguration?vlanID=12345678";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).delete(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void addStaticVLAN() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/MACBridgeIOS/catalyst/VLANAwareBridge/VLANAwareBridge/addStaticVLAN";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new LogicalPort());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void deleteStaticVLAN() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/MACBridgeIOS/catalyst/VLANAwareBridge/deleteStaticVLAN?vlanID=12345678";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).delete(ClientResponse.class, new LogicalPort());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

}