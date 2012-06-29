package bundle;

import java.io.FileNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.opennaas.core.resources.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	public static void main(String[] args) throws FileNotFoundException, JAXBException {
		testpost();

	}

	/**
	 * 
	 */
	private static void testpost() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8888/opennaas/resource/testpost");

			Test t = new Test();
			t.setA("Jordi");
			t.setB("Puig");
			ClientResponse response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, t);

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