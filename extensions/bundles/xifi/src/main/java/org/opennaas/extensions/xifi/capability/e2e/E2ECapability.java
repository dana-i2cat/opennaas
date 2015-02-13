package org.opennaas.extensions.xifi.capability.e2e;

/*
 * #%L
 * OpenNaaS :: XIFI
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.abno.capability.linkprovisioning.protocol.ABNOProtocolSession;
import org.opennaas.extensions.abno.repository.ABNORepository;
import org.opennaas.extensions.openstack.capability.openstackadapter.OpenstackAdaperCapability;
import org.opennaas.extensions.openstack.repository.OpenstackRepository;
import org.opennaas.extensions.protocols.http.HttpProtocolSession;
import org.opennaas.extensions.ryu.capability.monitoringmodule.MonitoringModuleCapability;
import org.opennaas.extensions.xifi.Activator;
import org.opennaas.extensions.xifi.capability.e2e.api.ConnectEndsRequest;
import org.opennaas.extensions.xifi.capability.e2e.model.Region;
import org.opennaas.extensions.xifi.capability.e2e.model.XIFIConfiguration;

/**
 * XIFI {@link IE2ECapability} implementation
 * 
 * @author Julio Carlos Barrera
 *
 */
public class E2ECapability extends AbstractCapability implements IE2ECapability {

	private static final Log		log						= LogFactory.getLog(E2ECapability.class);

	private String					resourceId				= "";

	// configuration file and properties
	private static final String		XIFI_FILE				= "xifi.regions";
	private static final String		ABNO_ENDPOINT			= "abno.endpoint";
	private static final String		REGION					= "region";
	private static final String		REGION_RYU				= ".ryu";
	private static final String		REGION_OPENSTACK		= ".openstack";
	private static final String		REGION_OPENSTACK_USER	= REGION_OPENSTACK + ".user";
	private static final String		REGION_OPENSTACK_PWD	= REGION_OPENSTACK + ".password";
	private static final String		REGION_OPENSTACK_TENANT	= REGION_OPENSTACK + ".tenant";
	private static final String		REGION_AUTOBAHN_IFACE	= ".autobahn.iface";

	private XIFIConfiguration		xifiConfiguration;

	// resources
	private IResource				abno;
	private Map<Region, IResource>	ryuResourcesMap			= new HashMap<Region, IResource>();
	private Map<Region, IResource>	openStackResourcesMap	= new HashMap<Region, IResource>();

	public E2ECapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new XIFI E2E Capability");
	}

	@Override
	public void connectEnds(ConnectEndsRequest connectEndsRequest) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), E2ECapability.class.getName());
		super.activate();

		// read XIFI Configuration
		try {
			readAndCheckConfiguration();
		} catch (Exception e) {
			log.error("Error reading XIFI configuration.", e);
			throw new CapabilityException(e);
		}
		log.info("XIFI configuration read:\n" + xifiConfiguration);

		// initialize resources
		try {
			log.info("Initializing XIFI resources...");
			initializeResources();
		} catch (Exception e) {
			log.error("Error initializing XIFI resources.", e);
			throw new CapabilityException(e);
		}
		log.info("XIFI resources initilized.");
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("This capability does not contain actionset.");
	}

	private void initializeResources() throws ActivatorException, ResourceException, ProtocolException {
		IResourceManager resourceManager = Activator.getResourceManagerService();
		IProtocolManager protocolManager = Activator.getProtocolManagerService();

		// ABNO resource
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor("internal", "1.0.0", "linkprovisioning", null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		Information information = new Information();
		information.setType(ABNORepository.ABNO_RESOURCE_TYPE);
		information.setName("abno1");
		resourceDescriptor.setInformation(information);

		abno = resourceManager.createResource(resourceDescriptor);

		ProtocolSessionContext psc = new ProtocolSessionContext();
		psc.addParameter(ProtocolSessionContext.AUTH_TYPE, "noatuh");
		psc.addParameter(ProtocolSessionContext.PROTOCOL, ABNOProtocolSession.ABNO_PROTOCOL_TYPE);
		psc.addParameter(ProtocolSessionContext.PROTOCOL_URI, xifiConfiguration.getAbnoEndpoint());
		protocolManager.getProtocolSessionManager(abno.getResourceIdentifier().getId()).registerContext(psc);

		resourceManager.startResource(abno.getResourceIdentifier());

		// Ryu and OpenStack per region
		for (Region region : xifiConfiguration.getRegions()) {
			// Ryu resource
			resourceDescriptor = new ResourceDescriptor();

			capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
			capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptorWithoutActionset(MonitoringModuleCapability.CAPABILITY_TYPE));
			resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

			information = new Information();
			information.setType("ryu");
			information.setName("ryu-" + region.getName());
			resourceDescriptor.setInformation(information);

			ryuResourcesMap.put(region, resourceManager.createResource(resourceDescriptor));

			psc = new ProtocolSessionContext();
			psc.addParameter(ProtocolSessionContext.AUTH_TYPE, "noauth");
			psc.addParameter(ProtocolSessionContext.PROTOCOL, HttpProtocolSession.HTTP_PROTOCOL_TYPE);
			psc.addParameter(ProtocolSessionContext.PROTOCOL_URI, region.getRyuEndpoint());
			protocolManager.getProtocolSessionManager(ryuResourcesMap.get(region).getResourceIdentifier().getId()).registerContext(psc);

			resourceManager.startResource(ryuResourcesMap.get(region).getResourceIdentifier());

			// OpenStack resource
			resourceDescriptor = new ResourceDescriptor();

			capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
			capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptorWithoutActionset(OpenstackAdaperCapability.CAPABILITY_TYPE));
			resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

			information = new Information();
			information.setType(OpenstackRepository.OPENSTACK_RESOURCE_TYPE);
			information.setName(OpenstackRepository.OPENSTACK_RESOURCE_TYPE + "-" + region.getName());
			resourceDescriptor.setInformation(information);

			openStackResourcesMap.put(region, resourceManager.createResource(resourceDescriptor));

			psc = new ProtocolSessionContext();
			psc.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");
			psc.addParameter(ProtocolSessionContext.PROTOCOL, HttpProtocolSession.HTTP_PROTOCOL_TYPE);
			psc.addParameter(ProtocolSessionContext.PROTOCOL_URI, region.getOpenstackEndpoint());
			psc.addParameter(ProtocolSessionContext.USERNAME, region.getOpenstackUser());
			psc.addParameter(ProtocolSessionContext.PASSWORD, region.getOpenstackPassword());
			protocolManager.getProtocolSessionManager(openStackResourcesMap.get(region).getResourceIdentifier().getId()).registerContext(psc);

			resourceManager.startResource(openStackResourcesMap.get(region).getResourceIdentifier());
		}
	}

	private void readAndCheckConfiguration() throws Exception {
		log.info("Reading XIFI configuration from file " + XIFI_FILE + "...");

		xifiConfiguration = new XIFIConfiguration();

		// get configuration file properties
		ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getContext());
		try {
			Properties properties = ConfigurationAdminUtil.getProperties(Activator.getContext(), XIFI_FILE);

			xifiConfiguration.setABNOEndpoint(configurationAdmin.getProperty(XIFI_FILE, ABNO_ENDPOINT));

			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = (String) entry.getKey();
				log.trace("Analizing property: " + entry);

				if (key.startsWith(REGION + ".") && key.lastIndexOf('.') == ((REGION + ".").length() - 1)) {

					String regionName = (String) entry.getValue();
					log.trace("Region found: " + regionName);

					// create the Region
					Region region = new Region();
					region.setName(regionName);

					String regionKey = key.substring((REGION + ".").length());

					// fill fields
					String ryuEndpoint = configurationAdmin.getProperty(XIFI_FILE, REGION + "." + regionKey + REGION_RYU);
					region.setRyuEndpoint(ryuEndpoint);

					String openStackEndpoint = configurationAdmin.getProperty(XIFI_FILE, REGION + "." + regionKey + REGION_OPENSTACK);
					region.setOpenstackEndpoint(openStackEndpoint);
					String openStackUser = configurationAdmin.getProperty(XIFI_FILE, REGION + "." + regionKey + REGION_OPENSTACK_USER);
					region.setOpenstackUser(openStackUser);
					String openStackPassword = configurationAdmin.getProperty(XIFI_FILE, REGION + "." + regionKey + REGION_OPENSTACK_PWD);
					region.setOpenstackPassword(openStackPassword);
					String openStackTenant = configurationAdmin.getProperty(XIFI_FILE, REGION + "." + regionKey + REGION_OPENSTACK_TENANT);
					region.setOpenstackTenant(openStackTenant);

					String autobahnIface = configurationAdmin.getProperty(XIFI_FILE, REGION + "." + regionKey + REGION_AUTOBAHN_IFACE);
					region.setAutoBAHNInterface(autobahnIface);

					// add the region
					xifiConfiguration.getRegions().add(region);
				}
			}

		} catch (IOException e) {
			log.error("Error getting configuration!");
			throw e;
		}
		log.info("XIFI configuration read.");

		// check configuration
		log.info("Checking XIFI configuration...");
		if (xifiConfiguration.getAbnoEndpoint() == null)
			throw new Exception("ABNO Endpoint not set!");
		for (Region region : xifiConfiguration.getRegions()) {
			if (region.getName() == null)
				throw new Exception("Region name not set!");
			if (region.getRyuEndpoint() == null)
				throw new Exception("Ryu endpoint for region " + region.getName() + " not properly set!");
			if (region.getOpenstackEndpoint() == null || region.getOpenstackUser() == null || region.getOpenstackPassword() == null || region
					.getOpenstackTenant() == null)
				throw new Exception("OpenStack endpoint for region " + region.getName() + " not properly set!");
		}

		log.info("XIFI configuration checked.");
	}
}
