package org.opennaas.extensions.roadm.wonesys.protocols;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class WonesysProtocolSessionContextUtils {

	public static final String	HOST_IP_ADDRESS	= "protocol.hostip";
	public static final String	HOST_PORT		= "protocol.hostport";

	public static String getHost(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		/*
		 * URI = [scheme:][//authority][path][?query][#fragment]; authority = [user-info@]host[:port]
		 */

		Map<String, Object> params = protocolSessionContext.getSessionParameters();
		String uriStr = (String) params.get(ProtocolSessionContext.PROTOCOL_URI);

		if (uriStr == null) {
			throw new ProtocolException("Invalid uri");
		}

		try {

			URI uri = new URI(uriStr);
			String host = uri.getHost();
			return host;

		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}

	public static int getPort(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		Map<String, Object> params = protocolSessionContext.getSessionParameters();
		String uriStr = (String) params.get(ProtocolSessionContext.PROTOCOL_URI);

		if (uriStr == null) {
			throw new ProtocolException("Invalid ProtocolSessionContext. It should contain a protocol uri.");
		}

		try {

			URI uri = new URI(uriStr);
			int port = uri.getPort();
			return port;

		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}

	public static boolean isMock(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		Map<String, Object> params = protocolSessionContext.getSessionParameters();

		// check if contains sessionParameter protocol.mock = true
		String isMock = (String) protocolSessionContext.getSessionParameters().get("protocol.mock");
		if (isMock != null && isMock.equals("true")) {
			return true;
		}

		String uriStr = (String) params.get(ProtocolSessionContext.PROTOCOL_URI);
		if (uriStr == null) {
			throw new ProtocolException("Invalid ProtocolSessionContext. It should contain a protocol uri.");
		}

		//check if URIs query contains mock=true
		try {

			URI uri = new URI(uriStr);
			String query = uri.getQuery();

			if (query != null) {

				if (query.startsWith("?"))
					query = query.substring(1);

				if (query.isEmpty())
					return false;

				Map<String, String> queryParams = getQueryMap(query);

				if (queryParams.containsKey("mock")){
					return queryParams.get("mock").equals("true");
				}
			}
			return false;

		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}


	private static Map<String, String> getQueryMap(String query) {
	    String[] params = query.split("&");
	    Map<String, String> map = new HashMap<String, String>();
	    for (String param : params)
	    {
	    	String[] nameValue = param.split("=");
	    	if (nameValue.length == 2) {
	    		String name = nameValue[0];
		        String value = nameValue[1];
		        map.put(name, value);
	    	}
	    }
	    return map;
	}


}
