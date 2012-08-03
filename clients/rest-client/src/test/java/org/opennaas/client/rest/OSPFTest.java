package org.opennaas.client.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.wrappers.AddInterfacesInOSPFAreaRequest;
import org.opennaas.extensions.router.model.wrappers.RemoveInterfacesInOSPFAreaRequest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class OSPFTest {

	private static final Logger	LOGGER	= Logger.getLogger(OSPFTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		activateOSPF();
		deactivateOSPF();
		configureOSPF();
		clearOSPFconfiguration();
		configureOSPFArea();
		removeOSPFArea();
		addInterfacesInOSPFArea();
		removeInterfacesInOSPFArea();
		enableOSPFInterfaces();
		disableOSPFInterfaces();
		getOSPFConfiguration();
		// showOSPFConfiguration();
	}

	/**
	 * 
	 */
	private static void activateOSPF() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/activateOSPF";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void deactivateOSPF() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/deactivateOSPF";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void configureOSPF() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/configureOSPF";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new OSPFService());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void clearOSPFconfiguration() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/clearOSPFconfiguration";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new OSPFService());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void configureOSPFArea() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/configureOSPFArea";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new OSPFAreaConfiguration());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void removeOSPFArea() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/removeOSPFArea";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new OSPFAreaConfiguration());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void addInterfacesInOSPFArea() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/addInterfacesInOSPFArea";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getAddInterfacesInOSPFAreaRequest());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void removeInterfacesInOSPFArea() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/removeInterfacesInOSPFArea";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getRemoveInterfacesInOSPFAreaRequest());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void enableOSPFInterfaces() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/enableOSPFInterfaces";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class,
					new GenericEntity<List<OSPFProtocolEndpoint>>(getProtocols()) {
					});
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void disableOSPFInterfaces() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/disableOSPFInterfaces";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class,
					new GenericEntity<List<OSPFProtocolEndpoint>>(getProtocols()) {
					});
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void getOSPFConfiguration() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/getOSPFConfiguration";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new OSPFService());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void showOSPFConfiguration() {
		OSPFService response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ospf/showOSPFConfiguration";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).post(OSPFService.class);
			LOGGER.info("Response code: " + response.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static List<OSPFProtocolEndpoint> getProtocols() {
		OSPFProtocolEndpoint endpoint = new OSPFProtocolEndpoint();
		endpoint.setCaption("Test");
		ArrayList<OSPFProtocolEndpoint> endpoints = new ArrayList<OSPFProtocolEndpoint>();
		endpoint.setName("name");
		endpoints.add(endpoint);
		return endpoints;
	}

	/**
	 * @return
	 */
	private static Object getRemoveInterfacesInOSPFAreaRequest() {
		RemoveInterfacesInOSPFAreaRequest request = new RemoveInterfacesInOSPFAreaRequest();
		OSPFArea ospfArea = new OSPFArea();
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		LogicalPort logicalPort = new LogicalPort();
		interfaces.add(logicalPort);
		request.setInterfaces(interfaces);
		request.setOspfArea(ospfArea);
		return request;
	}

	/**
	 * @return
	 */
	private static Object getAddInterfacesInOSPFAreaRequest() {
		AddInterfacesInOSPFAreaRequest request = new AddInterfacesInOSPFAreaRequest();
		OSPFArea ospfArea = new OSPFArea();
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		LogicalPort logicalPort = new LogicalPort();
		interfaces.add(logicalPort);
		request.setInterfaces(interfaces);
		request.setOspfArea(ospfArea);
		return request;
	}

}