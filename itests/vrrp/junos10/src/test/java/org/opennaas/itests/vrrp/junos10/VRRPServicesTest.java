package org.opennaas.itests.vrrp.junos10;

/*
 * #%L
 * OpenNaaS :: iTests :: VRRP :: Junos 10
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

import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.i2cat.netconf.rpc.Operation;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;
import net.i2cat.netconf.rpc.ReplyFactory;
import net.i2cat.netconf.server.Behaviour;
import net.i2cat.netconf.server.Server;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.Service;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.xml.sax.SAXException;

/**
 * @author Julio Carlos Barrera
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VRRPServicesTest {
	private final static Log	log						= LogFactory.getLog(VRRPServicesTest.class);

	private final static String	RESOURCE_INFO_NAME		= "VRRP services test";

	public static final String	CAPABILITY_URI			= "ssh://user:pass@localhost:2222/";
	public static final String	RESOURCE_URI			= "ssh://user:pass@localhost:2222/";

	public static final String	ACTION_NAME				= "junos";
	public static final String	RESOURCE_TYPE			= "router";

	public static final String	QUEUE_CAPABILIY_TYPE	= "queue";
	public static final String	VRRP_CAPABILITY_TYPE	= "vrrp";
	public static final String	CAPABILIY_VERSION		= "10.10";

	protected ICapability		iVRRPCapability;
	protected IResource			routerResource;

	private Server				netconfServer;

	@SuppressWarnings("unused")
	@Inject
	private BundleContext		bundleContext;

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.protocols.netconf)", timeout = 20000)
	private BlueprintContainer	netconfService;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer	routerRepoService;

	@Test
	public void testConfigureVRRP() throws ProtocolException, ResourceException, FileNotFoundException, IOException, SAXException {
		/* configure Netconf server behaviours */
		// two get-config at the beginning
		String config = IOUtils.toString(this.getClass().getResourceAsStream("/netconf/replies/configureVRRP_before.xml"));
		Query bQuery = QueryFactory.newGetConfig("candidate", null, null);
		Reply bReply = ReplyFactory.newGetConfigReply(bQuery, null, config);
		Behaviour behaviour = new Behaviour(bQuery, bReply, true);
		netconfServer.defineBehaviour(behaviour);
		netconfServer.defineBehaviour(behaviour);

		// edit-config with parameters
		config = IOUtils.toString(this.getClass().getResourceAsStream("/netconf/queries/configureVRRP.xml"));
		bQuery = QueryFactory.newEditConfig("candidate", null, null, null, config);
		bReply = ReplyFactory.newOk(bQuery, null);
		behaviour = new Behaviour(bQuery, bReply, true);
		netconfServer.defineBehaviour(behaviour);

		// get-config at the end
		config = IOUtils.toString(this.getClass().getResourceAsStream("/netconf/replies/configureVRRP_after.xml"));
		bQuery = QueryFactory.newGetConfig("candidate", null, null);
		bReply = ReplyFactory.newGetConfigReply(bQuery, null, config);
		behaviour = new Behaviour(bQuery, bReply, true);
		netconfServer.defineBehaviour(behaviour);

		// start resource
		startResource();

		// execute capability
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapability(InitializerTestHelper
				.getCapabilityInformation(VRRP_CAPABILITY_TYPE));
		vrrpCapability.configureVRRP((VRRPProtocolEndpoint) newParamsVRRPGroupWithOneEndpoint("192.168.100.1", "fe-0/3/2", "192.168.1.1",
				"255.255.255.0").getProtocolEndpoint().get(0));
		IQueueManagerCapability queueCapability = (IQueueManagerCapability) routerResource
				.getCapability(InitializerTestHelper.getCapabilityInformation(QUEUE_CAPABILIY_TYPE));
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();

		// assertions
		Assert.assertTrue("Actions must be correctly executed.", queueResponse.isOk());

		List<RPCElement> sentMessages = netconfServer.getStoredMessages();

		log.info("Messages sent to Netconf-server = " + sentMessages.size());
		Assert.assertTrue("Client must have sent at least 3 messages", sentMessages.size() > 3);
		String contractEditConfig = IOUtils.toString(this.getClass().getResourceAsStream("/netconf/queries/configureVRRP.xml"));
		String sentEditConfig = null;
		for (RPCElement rpcElement : sentMessages) {
			if (rpcElement instanceof Query) {
				Query query = (Query) rpcElement;
				if (query.getOperation().equals(Operation.EDIT_CONFIG)) {
					sentEditConfig = query.getConfig();
				}
			}
		}
		// check message sent
		XMLAssert.assertXMLEqual("Edit-config message must be equal to the contract.", sentEditConfig, contractEditConfig);

		// check router model
		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();
		List<LogicalDevice> logicalDevices = routerModel.getLogicalDevices();
		Assert.assertEquals("There must be only 1 LogicalDevice.", 1, logicalDevices.size());
		Assert.assertTrue("LogicalDevice must be an EthernetPort.", logicalDevices.get(0) instanceof EthernetPort);

		EthernetPort ethernetPort = (EthernetPort) logicalDevices.get(0);
		Assert.assertEquals("The EthernetPort must be fe-0/3/2.", "fe-0/3/2", ethernetPort.getName());
		List<ProtocolEndpoint> protocolEndpoints = ethernetPort.getProtocolEndpoint();
		Assert.assertEquals("There must be only 1 ProtocolEndpoint associated with the EthernetPort.", 1, protocolEndpoints.size());
		Assert.assertTrue("ProtocolEndpoint must be an IPProtocolEndpoint.", protocolEndpoints.get(0) instanceof IPProtocolEndpoint);

		IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) protocolEndpoints.get(0);
		Assert.assertEquals("The IPv4 address must be 192.168.1.1.", "192.168.1.1", ipProtocolEndpoint.getIPv4Address());
		Assert.assertEquals("The IPv4 subnet mask must be 255.255.255.0.", "255.255.255.0", ipProtocolEndpoint.getSubnetMask());

		List<Service> hostedServices = routerModel.getHostedService();
		Assert.assertEquals("There must be only 1 hosted Service.", 1, hostedServices.size());

		Service service = hostedServices.get(0);
		Assert.assertTrue("The Service must be a VRRPGroup.", service instanceof VRRPGroup);
		VRRPGroup vrrpGroup = (VRRPGroup) service;

		Assert.assertEquals("The VRRP Name must be 201.", 201, vrrpGroup.getVrrpName());
		Assert.assertEquals("The VRRP virtual IP address must be 192.168.100.1.", "192.168.100.1", vrrpGroup.getVirtualIPAddress());
		Assert.assertEquals("The VRRP virtual link address must be not set.", null, vrrpGroup.getVirtualLinkAddress());

		List<ProtocolEndpoint> protocolEndpoints2 = vrrpGroup.getProtocolEndpoint();
		// List<ServiceAccessPoint> protocolEndpoints2 = ipProtocolEndpoint.getBindedServiceAccessPoints();
		Assert.assertEquals("There must be only 1 ProtocolEndpoint associated with the VRRPGroup.", 1, protocolEndpoints2.size());
		Assert.assertTrue("ProtocolEndpoint must be an VRRPProtocolEndpoint.", protocolEndpoints2.get(0) instanceof VRRPProtocolEndpoint);

		VRRPProtocolEndpoint vrrpProtocolEndpoint = (VRRPProtocolEndpoint) protocolEndpoints2.get(0);
		Assert.assertEquals("The VRRP priority must be 100.", 100, vrrpProtocolEndpoint.getPriority());

		stopResource();
	}

	@Configuration
	public static Option[] configuration() {
		return options(
				/* debug socket */
				// OpennaasExamOptions.openDebugSocket(),
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-router", "opennaas-router-driver-junos", "itests-helpers", "ssh"),
				OpennaasExamOptions.noConsole(),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void initBundles() throws ResourceException, ProtocolException {
		// XMLUnit basic configuration
		XMLUnit.setIgnoreWhitespace(true);

		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");

		// netconf server
		netconfServer = Server.createServerStoringMessages(2222);
		netconfServer.startServer();
	}

	@After
	public void stopBundle() throws Exception {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");

		// netconf server
		netconfServer.stopServer();
	}

	/**
	 * Start router resource with 2 capabilities -> vrrp & queue
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void startResource() throws ResourceException, ProtocolException {
		// Add VRRP Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor vrrpCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				CAPABILIY_VERSION,
				VRRP_CAPABILITY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(vrrpCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor();
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				RESOURCE_URI, RESOURCE_INFO_NAME);

		// Create resource
		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), RESOURCE_URI);

		// Start resource
		resourceManager.startResource(routerResource.getResourceIdentifier());
	}

	/**
	 * Stop and remove the router resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopResource() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(routerResource.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(routerResource.getResourceIdentifier());
	}

	private static VRRPGroup newParamsVRRPGroupWithOneEndpoint(String virtualIPAddress, String interfaceName, String interfaceIPAddress,
			String interfaceSubnetMask) {
		// VRRPGroup
		VRRPGroup vrrpGroup = new VRRPGroup();
		vrrpGroup.setVrrpName(201);
		vrrpGroup.setVirtualIPAddress(virtualIPAddress);

		// VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrProtocolEndpoint1 = new VRRPProtocolEndpoint();
		vrrProtocolEndpoint1.setPriority(100);
		vrrProtocolEndpoint1.setService(vrrpGroup);
		vrrProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV4);
		// IPProtocolEndpoint
		IPProtocolEndpoint ipProtocolEndpoint1 = new IPProtocolEndpoint();
		ipProtocolEndpoint1.setIPv4Address(interfaceIPAddress);
		ipProtocolEndpoint1.setSubnetMask(interfaceSubnetMask);
		ipProtocolEndpoint1.setProtocolIFType(ProtocolIFType.IPV4);
		vrrProtocolEndpoint1.bindServiceAccessPoint(ipProtocolEndpoint1);

		// EthernetPort
		EthernetPort eth1 = new EthernetPort();
		eth1.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth1.setName(interfaceName);
		ipProtocolEndpoint1.addLogiaclPort(eth1);

		return vrrpGroup;
	}

}
