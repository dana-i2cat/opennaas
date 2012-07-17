package org.opennaas.client.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.wrappers.AddInterfacesToLogicalRouterRequest;
import org.opennaas.extensions.router.model.wrappers.RemoveInterfacesFromLogicalRouterRequest;
import org.opennaas.extensions.router.model.wrappers.SetEncapsulationLabelRequest;
import org.opennaas.extensions.router.model.wrappers.SetEncapsulationRequest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ChassisTest {

	private static final Logger	LOGGER	= Logger.getLogger(ChassisTest.class);

	public static void main(String[] args) {
		upPhysicalInterface();
		downPhysicalInterface();
		createSubInterface();
		deleteSubInterface();
		createLogicalRouter();
		deleteLogicalRouter();
		addInterfacesToLogicalRouter();
		removeInterfacesFromLogicalRouter();
		setEncapsulation();
		setEncapsulationLabel();
	}

	/**
	 * 
	 */
	private static void upPhysicalInterface() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/upPhysicalInterface";
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
	private static void downPhysicalInterface() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/downPhysicalInterface";
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
	private static void createSubInterface() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/createSubInterface";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getEthernetPort());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void deleteSubInterface() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/deleteSubInterface";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getEthernetPort());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void createLogicalRouter() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/createLogicalRouter";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getComputerSystem());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void deleteLogicalRouter() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/deleteLogicalRouter";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getComputerSystem());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void addInterfacesToLogicalRouter() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/addInterfacesToLogicalRouter";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getAddInterfacesToLogicalRouterRequest());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void removeInterfacesFromLogicalRouter() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/removeInterfacesFromLogicalRouter";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getRemoveInterfacesToLogicalRouterRequest());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void setEncapsulation() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/setEncapsulation";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getSetEncapsulationRequest());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void setEncapsulationLabel() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/chassis/setEncapsulationLabel";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getSetEncapsulationLabelRequest());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static SetEncapsulationRequest getSetEncapsulationRequest() {
		SetEncapsulationRequest request = new SetEncapsulationRequest();
		NetworkPort networkPort = new NetworkPort();
		networkPort.setName("ge-2/0/1.2048");
		ProtocolEndpoint endpoint = new ProtocolEndpoint();
		endpoint.setProtocolIFType(ProtocolIFType.LAYER_2_VLAN_USING_802_1Q);
		endpoint.setName("ge-2/0/1.2042");
		List<ProtocolEndpoint> list = new ArrayList<ProtocolEndpoint>();
		list.add(endpoint);
		networkPort.setProtocolEndpoints(list);
		request.setIface(networkPort);
		request.setEncapsulation(ProtocolIFType.IPV4);
		return request;
	}

	/**
	 * @return
	 */
	private static SetEncapsulationLabelRequest getSetEncapsulationLabelRequest() {
		SetEncapsulationLabelRequest request = new SetEncapsulationLabelRequest();
		request.setEncapsulationLabel("ge-2/0/1.2041");

		NetworkPort networkPort = new NetworkPort();

		List<ProtocolEndpoint> list = new ArrayList<ProtocolEndpoint>();

		ProtocolEndpoint endpoint = new ProtocolEndpoint();
		endpoint.setProtocolIFType(ProtocolIFType.LAYER_2_VLAN_USING_802_1Q);
		endpoint.setName("ge-2/0/1.2042");
		list.add(endpoint);

		networkPort.setProtocolEndpoints(list);
		networkPort.setName("ge-2/0/1.2043");
		request.setIface(networkPort);
		return request;
	}

	/**
	 * @return
	 */
	private static AddInterfacesToLogicalRouterRequest getAddInterfacesToLogicalRouterRequest() {
		AddInterfacesToLogicalRouterRequest request = new AddInterfacesToLogicalRouterRequest();
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		LogicalPort logicalPort = new LogicalPort();
		interfaces.add(logicalPort);
		request.setInterfaces(interfaces);
		return request;
	}

	/**
	 * @return
	 */
	private static RemoveInterfacesFromLogicalRouterRequest getRemoveInterfacesToLogicalRouterRequest() {
		RemoveInterfacesFromLogicalRouterRequest request = new RemoveInterfacesFromLogicalRouterRequest();
		List<LogicalPort> interfaces = new ArrayList<LogicalPort>();
		LogicalPort logicalPort = new LogicalPort();
		interfaces.add(logicalPort);
		request.setInterfaces(interfaces);
		return request;
	}

	/**
	 * @return
	 */
	private static ComputerSystem getComputerSystem() {
		ComputerSystem computerSystem = new ComputerSystem();
		computerSystem.setName("Name");
		return computerSystem;
	}

	/**
	 * @return
	 */
	private static EthernetPort getEthernetPort() {
		EthernetPort ethernetPort = new EthernetPort();
		ethernetPort.setName("ge-2/0/1.2048");
		return ethernetPort;
	}

}