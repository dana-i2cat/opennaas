package org.opennaas.itests.router.ip;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.router.model.wrappers.SetIpAddressRequest;
import org.opennaas.itests.router.helpers.ParamCreationHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class IPCapabilityIntegrationRestTest extends IPCapabilityIntegrationTest
{
	private final static Log	LOGGER	= LogFactory.getLog(IPCapabilityIntegrationRestTest.class);
	private final static String	WS_PATH	= "http://localhost:8888/opennaas/router/lolaM20/";

	private WebResource			webResource;
	private ClientResponse		response;

	public void testSetIPv4Rest() throws ProtocolException, ResourceException {
		String method = "setIPv4";
		Client client = null;

		try {
			client = Client.create();
			webResource = client.resource(WS_PATH + method);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, getSetIPv4Request());
			LOGGER.info("Response code: " + response.getStatus());
			Assert.assertTrue(response.getStatus() > 199 && response.getStatus() < 299);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	private SetIpAddressRequest getSetIPv4Request() {
		SetIpAddressRequest request = new SetIpAddressRequest();
		request.setIpProtocolEndpoint(ParamCreationHelper.getIPProtocolEndPoint());
		request.setLogicalDevice(ParamCreationHelper.getLogicalPort());
		return request;
	}
}
