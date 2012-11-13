package org.opennaas.client.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.topology.Interface;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class L2BoDTest {

	private static final Logger	LOGGER	= Logger.getLogger(L2BoDTest.class);

	public static void main(String[] args) {
		requestConnection();
		shutdownConnection();
	}

	/**
	 * 
	 */
	private static void requestConnection() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/bod/l2bod_test/l2bod/requestConnection";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getParameters());
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	private static void shutdownConnection() {
		ClientResponse response = null;
		String url = "http://localhost:8888/opennaas/bod/l2bod_test/l2bod/shutdownConnection";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class,
					new GenericEntity<List<Interface>>(getListInterfaces()) {
					});
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private static List<Interface> getListInterfaces() {
		List<Interface> listInterfaces = new ArrayList<Interface>();
		Interface iface = new Interface();
		Device device = new Device();
		device.setName("MyDevice");
		iface.setDevice(device);
		iface.setName("MyIface");
		listInterfaces.add(iface);
		return listInterfaces;
	}

	/**
	 * @return
	 */
	private static RequestConnectionParameters getParameters() {
		RequestConnectionParameters parameters = new RequestConnectionParameters();
		parameters.capacity = 10;
		parameters.endTime = new DateTime();
		parameters.interface1 = new Interface();
		parameters.interface1.setName("MyIface1");
		parameters.interface1.setDevice(new Device());
		parameters.interface1.getDevice().setName("MyDevice");
		parameters.interface2 = new Interface();
		parameters.startTime = new DateTime();
		parameters.vlanid1 = 10;
		parameters.vlanid2 = 10;
		return parameters;
	}

}