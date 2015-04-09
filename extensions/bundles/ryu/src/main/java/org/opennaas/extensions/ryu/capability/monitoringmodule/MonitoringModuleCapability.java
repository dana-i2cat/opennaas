package org.opennaas.extensions.ryu.capability.monitoringmodule;

/*
 * #%L
 * OpenNaaS :: Ryu Resource
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

import java.io.IOException;
import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.protocols.http.HttpProtocolSession;
import org.opennaas.extensions.ryu.Activator;
import org.opennaas.extensions.ryu.alarm.AlarmInformation;
import org.opennaas.extensions.ryu.alarm.IAlarmObserver;
import org.opennaas.extensions.ryu.alarm.IMonitoringModuleAlarmHandler;
import org.opennaas.extensions.ryu.alarm.MonitoringModuleAlarmHandler;
import org.opennaas.extensions.ryu.client.monitoringmodule.IMonitoringModuleCallbackAPI;
import org.opennaas.extensions.ryu.client.monitoringmodule.IMonitoringModuleclient;
import org.opennaas.extensions.ryu.client.monitoringmodule.MonitoringModuleJsonProvider;
import org.osgi.framework.ServiceRegistration;

import com.google.common.net.InetAddresses;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class MonitoringModuleCapability extends AbstractCapability implements IMonitoringModuleCapability {

	private static final String				HOST_KEY		= "org.apache.felix.http.host";
	private static final String				PORT_KEY		= "org.osgi.service.http.port";

	private static final String				HOST_INFO_URL	= "org.ops4j.pax.web";

	public final static String				CAPABILITY_TYPE	= "monitoringmodule";
	private final static String				URL_PREFIX		= "/xifi/raise_alarm";

	public final String						URL_WITH_RESOURCE;

	Log										log				= LogFactory.getLog(MonitoringModuleCapability.class);

	private String							resourceId		= "";

	private IMonitoringModuleclient			monitoringClient;
	private IMonitoringModuleAlarmHandler	monitoringModuleAlarmHandler;

	private HttpProtocolSession				httpProtocolSession;

	private String							host;
	private String							port;

	private ServiceRegistration				callbackRegistration;

	public MonitoringModuleCapability(CapabilityDescriptor descriptor, String resourceId) throws CapabilityException {

		super(descriptor);
		this.resourceId = resourceId;

		try {

			ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getContext());
			host = configurationAdmin.getProperty(HOST_INFO_URL, HOST_KEY);
			port = configurationAdmin.getProperty(HOST_INFO_URL, PORT_KEY);

			if (StringUtils.isEmpty(host) || StringUtils.isEmpty(port))
				throw new CapabilityException(HOST_KEY + " and " + PORT_KEY + " property are required for capability MonitoringModule.");

			if (!InetAddresses.isInetAddress(host) && !StringUtils.equals("localhost", host))
				throw new CapabilityException(HOST_KEY + " must be a valid ip address. Given value: " + host);

			if (!StringUtils.isNumeric(port))
				throw new CapabilityException(PORT_KEY + " must be numeric. Given value: " + host);

			httpProtocolSession = getHttpProtocolSession(Activator.getProtocolManagerService().getProtocolSessionManager(resourceId));
			monitoringClient = httpProtocolSession.getClient(IMonitoringModuleclient.class, new MonitoringModuleJsonProvider());

		} catch (ProtocolException e) {
			throw new CapabilityException(e);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		} catch (IOException e) {
			throw new CapabilityException(e);
		}

		// initialize URL_WITH_RESOURCE
		this.URL_WITH_RESOURCE = URL_PREFIX + "/" + this.resourceId;

		log.debug("Built new MonitoringModule Capability");
	}

	@Override
	public void registerAlarmObservation(String dpid, String port, String threshold, IAlarmObserver alarmObserver) {
		log.info("Registering alarm: [dpid=" + dpid + ",port=" + port + ",threshold=" + threshold + "]");

		// register alarm
		AlarmInformation alarmInformation = new AlarmInformation(threshold, this.host, this.port, URL_WITH_RESOURCE);

		monitoringModuleAlarmHandler.addAlarmObserver(dpid, Integer.valueOf(port), alarmObserver);

		monitoringClient.registerAlarm(dpid, port, alarmInformation);

		log.info("Alarm registered.");
	}

	@Override
	public void unregisterAlarmObservation(String dpid, String port) {
		log.info("Unregistering alarm: [dpid=" + dpid + ",port=" + port + "]");

		monitoringModuleAlarmHandler.removeAlarmObserver(dpid, Integer.valueOf(port));
		monitoringClient.unregisterAlarm(dpid, port);

		log.info("Alarm unregistered");

	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("MonitoringModuleCapability has no actionset.");

	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("MonitoringModuleCapability has no queue.");

	}

	@Override
	public void activate() throws CapabilityException {
		super.activate();

		monitoringModuleAlarmHandler = new MonitoringModuleAlarmHandler();

		// callbackServer = createServer(this.host, this.port, URL_PREFIX);
		// callbackServer.start();
		registerService();
	}

	@Override
	public void deactivate() throws CapabilityException {
		super.deactivate();
		callbackRegistration.unregister();
		// callbackServer.stop();
	}

	private Server createServer(String host, String port, String path) {

		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setResourceClasses(IMonitoringModuleCallbackAPI.class);
		sf.setResourceProvider(IMonitoringModuleCallbackAPI.class, new SingletonResourceProvider(monitoringModuleAlarmHandler));

		sf.setAddress("http://localhost:" + port + path);
		BindingFactoryManager manager = sf.getBus().getExtension(BindingFactoryManager.class);
		JAXRSBindingFactory factory = new JAXRSBindingFactory();
		factory.setBus(sf.getBus());
		manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, factory);

		return sf.create();
	}

	protected HttpProtocolSession getHttpProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		return (HttpProtocolSession) protocolSessionManager.obtainSessionByProtocol(HttpProtocolSession.HTTP_PROTOCOL_TYPE, false);
	}

	protected void registerService() throws CapabilityException {

		Hashtable<String, String> props = new Hashtable<String, String>();

		// Rest
		props.put("service.exported.interfaces", "*");
		props.put("service.exported.configs", "org.apache.cxf.rs");
		props.put("service.exported.intents", "HTTP");
		props.put("org.apache.cxf.rs.httpservice.context", URL_WITH_RESOURCE);
		props.put("org.apache.cxf.rs.address", "/");

		log.info("Registering ws: \n " +
				"in url: " + props.get("org.apache.cxf.rs.address") + "\n" +
				"in context: " + props.get("org.apache.cxf.rs.httpservice.context"));
		callbackRegistration = Activator.getContext().registerService(IMonitoringModuleCallbackAPI.class.getName(), monitoringModuleAlarmHandler,
				props);

	}
}
