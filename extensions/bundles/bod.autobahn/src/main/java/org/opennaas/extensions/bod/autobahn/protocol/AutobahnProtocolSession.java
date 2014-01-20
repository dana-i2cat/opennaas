package org.opennaas.extensions.bod.autobahn.protocol;

/*
 * #%L
 * OpenNaaS :: BoD :: Autobahn driver
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

import static com.google.common.base.Preconditions.checkState;
import static org.opennaas.core.resources.protocol.IProtocolSession.Status.CONNECTED;
import static org.opennaas.core.resources.protocol.IProtocolSession.Status.DISCONNECTED_BY_USER;
import static org.opennaas.core.resources.protocol.ProtocolSessionContext.PROTOCOL_URI;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import net.geant.autobahn.administration.Administration;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import com.google.common.base.Strings;

public class AutobahnProtocolSession implements IProtocolSession
{
	private final static QName		USER_ACCESS_POINT_SERVICE	=
																		new QName("http://useraccesspoint.autobahn.geant.net/",
																				"UserAccessPointService");
	private final static QName		USER_ACCESS_POINT_PORT		=
																		new QName("http://useraccesspoint.autobahn.geant.net/",
																				"UserAccessPointPort");
	private final static QName		ADMINISTRATION_SERVICE		=
																		new QName("http://administration.autobahn.geant.net/",
																				"AdministrationService");
	private final static QName		ADMINISTRATION_PORT			=
																		new QName("http://administration.autobahn.geant.net/",
																				"AdministrationPort");

	private final Log				log							= LogFactory.getLog(AutobahnProtocolSession.class);

	private ProtocolSessionContext	protocolSessionContext;
	private String					sessionID;

	private UserAccessPoint			uapService;
	private Administration			administrationService;

	private Status					status;

	public AutobahnProtocolSession(ProtocolSessionContext protocolSessionContext,
			String sessionID)
			throws ProtocolException
	{
		this.protocolSessionContext = protocolSessionContext;
		this.sessionID = sessionID;
		this.status = DISCONNECTED_BY_USER;
	}

	@Override
	public void asyncSend(Object requestMessage) throws ProtocolException
	{
		throw new UnsupportedOperationException("asyncSend not supported by AutobahnProtocolSession");
	}

	@Override
	public Object sendReceive(Object requestMessage) throws ProtocolException
	{
		throw new UnsupportedOperationException("sendReceive not supported by AutobahnProtocolSession");
	}

	@Override
	public void connect() throws ProtocolException
	{
		uapService =
				createUserAccessPointService(getServiceUri() + "uap2");
		administrationService =
				createAdministrationService(getServiceUri() + "administration2");
		status = CONNECTED;
	}

	@Override
	public void disconnect() throws ProtocolException
	{
		status = DISCONNECTED_BY_USER;
		uapService = null;
		administrationService = null;
	}

	@Override
	public ProtocolSessionContext getSessionContext()
	{
		return protocolSessionContext;
	}

	@Override
	public String getSessionId()
	{
		return sessionID;
	}

	@Override
	public Status getStatus()
	{
		return status;
	}

	@Override
	public void
			registerProtocolSessionListener(IProtocolSessionListener protocolSessionListener,
					IProtocolMessageFilter protocolMessageFilter,
					String idListener)
	{
	}

	@Override
	public void
			unregisterProtocolSessionListener(IProtocolSessionListener protocolSessionListener,
					String idListener)
	{
	}

	@Override
	public void setSessionContext(ProtocolSessionContext context)
	{
		this.protocolSessionContext = context;
	}

	@Override
	public void setSessionId(String sessionId)
	{
		this.sessionID = sessionId;
	}

	public UserAccessPoint getUserAccessPointService()
	{
		checkState(status == CONNECTED);
		return uapService;
	}

	public Administration getAdministrationService()
	{
		checkState(status == CONNECTED);
		return administrationService;
	}

	private String getServiceUri()
			throws ProtocolException
	{
		Map<String, Object> parameters =
				protocolSessionContext.getSessionParameters();
		String uri = (String) parameters.get(PROTOCOL_URI);
		if (Strings.isNullOrEmpty(uri)) {
			throw new ProtocolException(PROTOCOL_URI + " is missing in protocol session context.");
		}
		if (!uri.endsWith("/")) {
			uri = uri + "/";
		}
		return uri;
	}

	private <T> T createSoapService(String uri,
			QName serviceName,
			QName portName,
			Class<T> clazz)
			throws ProtocolException
	{
		/*
		 * The JAXWS SPI uses the context class loader to locate an implementation. We therefore make sure the context class loader is set to our
		 * class loader.
		 */
		Thread thread = Thread.currentThread();
		ClassLoader oldLoader = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(getClass().getClassLoader());
			Service service = Service.create(serviceName);
			service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, uri);
			return service.getPort(portName, clazz);
		} catch (WebServiceException e) {
			throw new ProtocolException("Failed to create Autobahn session: " +
					e.getMessage(), e);
		} finally {
			thread.setContextClassLoader(oldLoader);
		}
	}

	private UserAccessPoint createUserAccessPointService(String uri)
			throws ProtocolException
	{
		return createSoapService(uri,
				USER_ACCESS_POINT_SERVICE,
				USER_ACCESS_POINT_PORT,
				UserAccessPoint.class);
	}

	private Administration createAdministrationService(String uri)
			throws ProtocolException
	{
		return createSoapService(uri,
				ADMINISTRATION_SERVICE,
				ADMINISTRATION_PORT,
				Administration.class);
	}
}