package org.opennaas.client.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.core.protocols.sessionmanager.ListResponse;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class PSMTest {

	private static final Logger	LOGGER		= Logger.getLogger(IPTest.class);

	private static final String	BASE_URL	= "http://localhost:8888/opennaas/router/lolaM20/protocolSessionManager/";

	public static void main(String[] args) throws ProtocolException, URISyntaxException {

		String routerName1 = "lolaM20";
		String routerName2 = "myreM7i";

		List<String> sessionIds = getAllProtocolSessionIds();
		String sessionId = sessionIds.get(0);

		isLocked(sessionId);
		destroyProtocolSession(sessionId);

		ProtocolSessionContext context = createProtocolSessionContext();

		registerContext(context, routerName1);
		List<ProtocolSessionContext> contexts = getRegisteredContexts(routerName1);
		unregisterContext(contexts.get(0), routerName1);

		registerContext(context, routerName2);
		List<ProtocolSessionContext> contexts2 = getRegisteredContexts(routerName2);
		unregisterContext(contexts2.get(0), routerName2);

		registerContext(context, routerName1);
		unregisterContext((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL), routerName1);
		contexts = getRegisteredContexts(routerName1);

	}

	static void registerContext(ProtocolSessionContext context, String routerName) throws ProtocolException, URISyntaxException {
		ClientResponse response = null;
		String methodPath = "context/register";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/" + routerName + "/protocolSessionManager/" + methodPath, null, null);

		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, context);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	static void unregisterContext(ProtocolSessionContext context, String routerName) throws ProtocolException, URISyntaxException {
		ClientResponse response = null;
		String methodPath = "context/unregister";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/" + routerName + "/protocolSessionManager/" + methodPath, null, null);
		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, context);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	static void unregisterContext(String protocol, String routerName) throws ProtocolException, URISyntaxException {

		ClientResponse response = null;
		String methodPath = "context/" + protocol;
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/" + routerName + "/protocolSessionManager/" + methodPath, null, null);
		String url = uri.toASCIIString();
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).delete(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	static List<ProtocolSessionContext> getRegisteredContexts(String routerName) throws URISyntaxException {
		List<ProtocolSessionContext> response = null;
		String methodPath = "context/";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/" + routerName + "/protocolSessionManager/" + methodPath, null, null);
		String url = uri.toASCIIString();

		GenericType<List<ProtocolSessionContext>> genericType =
				new GenericType<List<ProtocolSessionContext>>() {
				};
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(genericType);
			// LOGGER.info("Response code: " + response.getStatus());
			LOGGER.info("Found " + response.size() + " protocolSessionContexts:");
			for (ProtocolSessionContext ctx : response) {
				LOGGER.info("Found a protocolSessionContext");
				LOGGER.info("Protocol: " + ctx.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));
				LOGGER.info("URL: " + ctx.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return response;
	}

	static List<String> getAllProtocolSessionIds() throws URISyntaxException {
		ListResponse response = null;
		String methodPath = "session/";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/lolaM20/protocolSessionManager/" + methodPath, null, null);
		String url = uri.toASCIIString();
		List<String> ids = null;

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ListResponse.class);
			ids = new ArrayList<String>();
			LOGGER.info("Found " + response.getList().size() + " ProtocolSessions:");
			for (Object id : response.getList()) {
				LOGGER.info("SessionId: " + id.toString());
				ids.add(id.toString());
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return ids;
	}

	static void destroyProtocolSession(String sessionID) throws ProtocolException, URISyntaxException {
		LOGGER.info("Destroying protocolSession");
		ClientResponse response = null;
		String methodPath = "session/" + sessionID;
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/lolaM20/protocolSessionManager/" + methodPath, null, null);
		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).delete(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	static void isLocked(String sessionId) throws ProtocolException, URISyntaxException {
		String methodPath = "session/" + sessionId + "/locked";
		URI uri = new URI("http", null, "localhost", 8888, "/opennaas/router/lolaM20/protocolSessionManager/" + methodPath, null, methodPath);
		String url = uri.toASCIIString();

		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			String response = webResource.get(String.class);
			LOGGER.info("session " + sessionId + " isLocked=" + response);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static ProtocolSessionContext createProtocolSessionContext() {

		ProtocolSessionContext ctx = new ProtocolSessionContext();
		ctx.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		ctx.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@router.my.net/netconf");

		return ctx;
	}
}
