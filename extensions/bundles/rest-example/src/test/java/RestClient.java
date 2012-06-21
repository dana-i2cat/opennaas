package org.opennaas.ws.rest;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	public static void main(String[] args) {

		Client client = Client.create();
		WebResource resource = client.resource("http://localhost:9000/opennaas/resourceDescriptor/greeting?name=test-rest");

		resource.type(MediaType.APPLICATION_JSON_TYPE);
		resource.accept(MediaType.APPLICATION_JSON_TYPE);

		// ClientResponse response = resource.post(ClientResponse.class, alumno);

		String response = resource.get(String.class);

		// ClientResponse response = resource.delete(ClientResponse.class);

		if (response != null) {
			System.out.println("Success! " + response);
		} else {
			System.out.println("ERROR! " + response);

		}

	}
}