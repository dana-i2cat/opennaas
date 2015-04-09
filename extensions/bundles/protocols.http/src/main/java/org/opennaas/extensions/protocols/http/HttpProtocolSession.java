package org.opennaas.extensions.protocols.http;

/*
 * #%L
 * OpenNaaS :: Protocotols :: HTTP
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class HttpProtocolSession implements IProtocolSession {

	public static final String						HTTP_PROTOCOL_TYPE		= "http";

	private ProtocolSessionContext					sessionContext			= null;
	private String									sessionId				= null;
	private Status									status					= null;

	private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;

	private URI										clientUrl;

	public HttpProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		super();
		setSessionId(sessionID);
		setSessionContext(protocolSessionContext);

		this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();

		this.status = Status.DISCONNECTED_BY_USER;

		checkProtocolSessionContext(protocolSessionContext);
	}

	@Override
	public void asyncSend(Object message) throws ProtocolException {
		throw new ProtocolException("Unsuported operation");
	}

	@Override
	public void connect() throws ProtocolException {
		if (status.equals(Status.CONNECTED))
			throw new ProtocolException("Cannot connect because the session is already connected");

		status = Status.CONNECTED;

	}

	@Override
	public void disconnect() throws ProtocolException {
		if (!status.equals(Status.CONNECTED))
			throw new ProtocolException("Cannot disconnect because the session is already disconnected. Current state: " + status);

		status = Status.DISCONNECTED_BY_USER;

	}

	@Override
	public ProtocolSessionContext getSessionContext() {
		return sessionContext;

	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void registerProtocolSessionListener(IProtocolSessionListener protocolSessionListener, IProtocolMessageFilter protocolMessageFilter,
			String idListener) {
		protocolMessageFilters.put(idListener, protocolMessageFilter);
		protocolListeners.put(idListener, protocolSessionListener);
	}

	@Override
	public Object sendReceive(Object message) throws ProtocolException {
		throw new ProtocolException("Unsuported operation");
	}

	@Override
	public void setSessionContext(ProtocolSessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;

	}

	@Override
	public void unregisterProtocolSessionListener(IProtocolSessionListener protocolSessionListener, String idListener) {
		protocolMessageFilters.remove(idListener);
		protocolListeners.remove(idListener);
	}

	public <API> API getClient(Class<API> apiClass, Object provider) {

		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(apiClass.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(clientUrl.toString());

		if (provider != null)
			bean.setProvider(provider);

		bean.setResourceClass(apiClass);
		bean.setClassLoader(classLoader);

		return bean.create(apiClass);
	}

	private void checkProtocolSessionContext(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		String protocol = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		if ((protocol == null) || (protocol.length() == 0) || !protocol.equals(HTTP_PROTOCOL_TYPE)) {
			throw new ProtocolException(
					"Protocols HTTP: Invalid protocol type: " + protocol + ". Protocol type must be " + HTTP_PROTOCOL_TYPE);
		}

		String uri = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		if ((uri == null) || (uri.length() == 0)) {
			throw new ProtocolException(
					"Protocols HTTP : Couldn't get " + ProtocolSessionContext.PROTOCOL_URI + " from protocolSessionContext.");
		}

		if (!StringUtils.startsWith(uri, "http://") && !StringUtils.startsWith(uri, "https://"))
			throw new ProtocolException("Invalid protocol url: HTTPProtocol expects http or https urls.");

		try {
			clientUrl = new URI(uri);
		} catch (URISyntaxException e) {
			throw new ProtocolException(e);
		}
	}

}
