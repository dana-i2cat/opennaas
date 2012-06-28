package bundle;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.cxf.jaxrs.ext.form.Form;
import org.opennaas.core.resources.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		testpost();
		testpost2();
		testpost3();
	}

	/**
	 *
	 */
	private static void testpost() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8888/opennaas/lolaM20/resource/testpost");

			String inputJSON = "{\"A\":\"Metallica\",\"B\":\"Fade To Black\"}";
			String inputXML = "<xml><test></test>";

			ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, inputXML);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private static void testpost2() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8888/opennaas/lolaM20/resource/testpost2");

			ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new Test());

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void testpost3() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8888/opennaas/lolaM20/resource/testpost3");

			String input = "{\"A\":\"Metallica\",\"B\":\"Fade To Black\"}";

			Form form = new Form();
			form.set("test", input);

			ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, input);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void testpost4() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8888/opennaas/lolaM20/ospf/testpost4");

			String input = "{\"A\":\"Metallica\",\"B\":\"Fade To Black\"}";

			ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new Test());

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void testpost5() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8888/opennaas/lolaM20/ospf/testpost4");

			String input = "{\"A\":\"Metallica\",\"B\":\"Fade To Black\"}";

			ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, new Test());

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}