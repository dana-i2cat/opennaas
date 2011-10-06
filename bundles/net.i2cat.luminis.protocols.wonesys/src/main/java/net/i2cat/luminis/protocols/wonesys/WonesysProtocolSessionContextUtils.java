package net.i2cat.luminis.protocols.wonesys;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.ws.ProtocolException;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class WonesysProtocolSessionContextUtils {

	public static final String	HOST_IP_ADDRESS	= "protocol.hostip";
	public static final String	HOST_PORT		= "protocol.hostport";

	public static String getHost(ProtocolSessionContext protocolSessionContext) {

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

	public static int getPort(ProtocolSessionContext protocolSessionContext) {

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

}
