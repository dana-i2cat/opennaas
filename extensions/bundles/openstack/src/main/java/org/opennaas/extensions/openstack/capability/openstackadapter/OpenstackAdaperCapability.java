package org.opennaas.extensions.openstack.capability.openstackadapter;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jclouds.ContextBuilder;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openstack.Activator;
import org.opennaas.extensions.protocols.http.HttpProtocolSession;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrián Roselló Rey (i2CAT)
 * 
 */
public class OpenstackAdaperCapability extends AbstractCapability implements IOpenstackAdapterCapability {

	public static final String	CAPABILITY_TYPE		= "openstackadapter";

	private Log					log					= LogFactory.getLog(OpenstackAdaperCapability.class);
	private String				resourceId			= "";

	private final static String	NOVA_PROVIDER_ID	= "openstack-nova";
	private final static String	NEUTRON_PROVIDER_ID	= "openstack-neutron";

	private NovaApi				novaClient;
	private NeutronApi			neutronClient;

	private IProtocolSession	protocolSession;

	public OpenstackAdaperCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new NCLProvisioner Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				IOpenstackAdapterCapability.class.getName());
		super.activate();

	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getInstanceId(String instanceName, String tenantName) throws CapabilityException {
		try {
			if (StringUtils.isEmpty(instanceName) || StringUtils.isEmpty(tenantName))
				throw new NullPointerException("Instance name is required.");

			protocolSession = getHttpProtocolSession(Activator.getProtocolManagerService().getProtocolSessionManager(resourceId));

			String username = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.USERNAME);
			String password = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PASSWORD);
			String uri = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

			String identity = new StringBuilder().append(tenantName).append(":").append(username).toString();

			novaClient = ContextBuilder.newBuilder(NOVA_PROVIDER_ID).endpoint(uri).credentials(identity, password)
					.buildApi(NovaApi.class);

			Set<String> zones = novaClient.getConfiguredZones();
			for (String zone : zones) {
				ServerApi serverApi = novaClient.getServerApiForZone(zone);

				for (Server server : serverApi.listInDetail().concat())
					if (StringUtils.equals(server.getName(), instanceName))
						return server.getId();

			}

		} catch (ProtocolException p) {
			throw new CapabilityException(p);
		} catch (ActivatorException p) {
			throw new CapabilityException(p);
		}
		throw new CapabilityException("There's no instance with name: " + instanceName);
	}

	@Override
	public String getPortId(String instanceId, String tenantName) throws CapabilityException {

		if (StringUtils.isEmpty(instanceId) || StringUtils.isEmpty(tenantName))
			throw new NullPointerException("InstanceId and tenantName parameters are required.");

		String username = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.USERNAME);
		String password = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PASSWORD);
		String uri = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

		String identity = new StringBuilder().append(tenantName).append(":").append(username).toString();

		neutronClient = ContextBuilder.newBuilder(NEUTRON_PROVIDER_ID).endpoint(uri).credentials(identity, password)
				.buildApi(NeutronApi.class);

		Set<String> zones = neutronClient.getConfiguredRegions();
		for (String zone : zones) {
			PortApi portApi = neutronClient.getPortApi(zone);
			for (Port port : portApi.list().concat()) {
				if (StringUtils.equals(instanceId, port.getDeviceId()))
					return port.getId();
			}
		}

		throw new CapabilityException("There's no port in instance: " + instanceId);

	}

	public String getMACAddress(String portId, String tenantName) throws CapabilityException {

		if (StringUtils.isEmpty(portId) || StringUtils.isEmpty(tenantName))
			throw new NullPointerException("PortId and tenantName parameters are required.");

		String username = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.USERNAME);
		String password = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PASSWORD);
		String uri = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

		String identity = new StringBuilder().append(tenantName).append(":").append(username).toString();

		neutronClient = ContextBuilder.newBuilder(NEUTRON_PROVIDER_ID).endpoint(uri).credentials(identity, password)
				.buildApi(NeutronApi.class);

		Set<String> zones = neutronClient.getConfiguredRegions();
		for (String zone : zones) {
			PortApi portApi = neutronClient.getPortApi(zone);
			for (Port port : portApi.list().concat()) {
				if (StringUtils.equals(port.getId(), portId))
					return port.getMacAddress();
			}
		}

		throw new CapabilityException("There's no port with ID: " + portId);

	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("Not implemented");
	}

	protected HttpProtocolSession getHttpProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		return (HttpProtocolSession) protocolSessionManager.obtainSessionByProtocol(HttpProtocolSession.HTTP_PROTOCOL_TYPE, false);
	}
}
