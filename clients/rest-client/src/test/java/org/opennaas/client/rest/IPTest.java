package org.opennaas.client.rest;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.wrappers.SetIpAddressRequest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class IPTest {

	private static final Logger	LOGGER	= Logger.getLogger(IPTest.class);

	public static void main(String[] args) throws FileNotFoundException, JAXBException {

		setIPv4(generateSetIpRequest());
		setInterfaceDescription(generateLogicalPortWithDescription());
	}

	public static void setIPv4(SetIpAddressRequest request) {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ipv4/setIPv4";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);

			response = webResource.accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML)
					.post(ClientResponse.class, request);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static void setInterfaceDescription(LogicalPort iface) {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/router/lolaM20/ipv4/setInterfaceDescription";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.accept(MediaType.APPLICATION_XML).type(MediaType.APPLICATION_XML).post(ClientResponse.class, iface);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static LogicalPort generateLogicalPortWithDescription() {
		LogicalPort port = generateLogicalPort();
		port.setDescription("Description set through REST");

		return port;
	}

	private static LogicalPort generateLogicalPort() {
		EthernetPort port = new EthernetPort();
		port.setName("fe-0/1/1");
		port.setPortNumber(1);

		return port;
	}

	private static IPProtocolEndpoint generateIP() {
		IPProtocolEndpoint ep = new IPProtocolEndpoint();
		ep.setIPv4Address("192.168.22.1");
		ep.setSubnetMask("255.255.255.0");
		return ep;
	}

	private static SetIpAddressRequest generateSetIpRequest() {
		SetIpAddressRequest req = new SetIpAddressRequest();
		req.setLogicalDevice(generateLogicalPort());
		req.setIpProtocolEndpoint(generateIP());
		return req;
	}

}
