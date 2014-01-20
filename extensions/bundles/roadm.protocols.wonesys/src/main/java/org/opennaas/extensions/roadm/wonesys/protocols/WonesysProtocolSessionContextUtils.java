package org.opennaas.extensions.roadm.wonesys.protocols;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Protocol
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

		// check if URIs query contains mock=true
		try {

			URI uri = new URI(uriStr);
			String query = uri.getQuery();

			if (query != null) {

				if (query.startsWith("?"))
					query = query.substring(1);

				if (query.isEmpty())
					return false;

				Map<String, String> queryParams = getQueryMap(query);

				if (queryParams.containsKey("mock")) {
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
