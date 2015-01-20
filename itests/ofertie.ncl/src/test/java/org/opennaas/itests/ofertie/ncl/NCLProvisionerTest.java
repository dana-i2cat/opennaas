package org.opennaas.itests.ofertie.ncl;

/*
 * #%L
 * OpenNaaS :: iTests :: OFERTIE NCL
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.genericnetwork.actionsets.internal.circuitprovisioning.CircuitProvisioningActionsetImplementation;
import org.opennaas.extensions.genericnetwork.capability.circuitaggregation.CircuitAggregationCapability;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.CircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.NCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.capability.nettopology.NetTopologyCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingParamsMapping;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.driver.NetworkConnectionImplementationId;
import org.opennaas.extensions.ofertie.ncl.helpers.QosPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.notification.INCLNotifierClient;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.ClientManager;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup.FloodlightMockClientFactory;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
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
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrian Rosello (i2cat)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NCLProvisionerTest {

	private IResource					sdnNetResource;

	private QosPolicyRequest			qosPolicyRequest;

	private Map<String, IResource>		switches;

	private static final String			SWITCH_1_NAME						= "s1";
	private static final String			SWITCH_2_NAME						= "s2";
	private static final String			SWITCH_3_NAME						= "s3";
	private static final String			SWITCH_4_NAME						= "s4";
	private static final String			SWITCH_5_NAME						= "s5";

	private static final String			SWITCH_1_ID							= "00:00:00:00:00:00:00:01";
	private static final String			SWITCH_2_ID							= "00:00:00:00:00:00:00:02";
	private static final String			SWITCH_3_ID							= "00:00:00:00:00:00:00:03";
	private static final String			SWITCH_4_ID							= "00:00:00:00:00:00:00:04";
	private static final String			SWITCH_5_ID							= "00:00:00:00:00:00:00:05";

	private static final String			FLOODLIGHT_ACTIONSET_NAME			= "floodlight";
	private static final String			FLOODLIGHT_ACTIONSET_VERSION		= "0.90";
	private static final String			FLOODLIGHT_PROTOCOL					= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;

	private static final String			OFSWITCH_RESOURCE_TYPE				= "openflowswitch";
	private static final String			SWITCH_ID_NAME						= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private static final String			MOCK_URI							= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String			PROTOCOL_URI						= "http://dev.ofertie.i2cat.net:8080";

	private static final String			CIRCUIT_AGGREGATION_CAPABILITY_TYPE	= CircuitAggregationCapability.CAPABILITY_TYPE;
	private static final String			PATHFINDING_CAPABILITY_TYPE			= PathFindingCapability.CAPABILITY_TYPE;
	private static final String			NETTOPOLOGY_CAPABILITY_TYPE			= NetTopologyCapability.CAPABILITY_TYPE;
	private static final String			NCLPROVISIONER_CAPABILITY_TYPE		= NCLProvisionerCapability.CAPABILITY_TYPE;

	private static final String			INTERNAL_ACTIONSET_NAME				= "internal";
	private static final String			CAPABILITY_VERSION					= "1.0.0";

	private static final String			SDN_RESOURCE_NAME					= "sdnNetwork";
	private static final String			GENERICNET_RESOURCE_TYPE			= "genericnetwork";

	/* FLOW REQUEST PARAMS */
	private static final String			SRC_IP_ADDRESS						= "192.168.10.10";
	private static final String			DST_IP_ADDRESS						= "192.168.10.11";
	private static final int			SRC_PORT							= 0;
	private static final int			DST_PORT							= 1;
	private static final int			TOS									= 0;
	private static final int			QOS_MIN_LATENCY						= 5;
	private static final int			QOS_MAX_LATENCY						= 10;
	private static final int			QOS_MIN_JITTER						= 2;
	private static final int			QOS_MAX_JITTER						= 4;
	private static final int			QOS_MIN_THROUGHPUT					= 100;
	private static final int			QOS_MAX_THROUGHPUT					= 1000;
	private static final int			QOS_MIN_PACKET_LOSS					= 0;
	private static final int			QOS_MAX_PACKET_LOSS					= 1;

	private static final String			ROUTE_FILES_URI						= "/route5switches.xml";
	private static final String			ROUTES_MAPPING_URI					= "/mapping5switches.xml";
	private static final String			TOPOLOGY_FILE_URI					= "/topologies/topology5switches.xml";

	private static final String			WS_URI								= "http://localhost:8888/opennaas/ofertie/ncl";
	private static final String			WS_USERNAME							= "admin";
	private static final String			WS_PASSWORD							= "123456";

	@Inject
	protected BundleContext				context;

	// /// ENDPOINT LISTENERS //// //
	private WSEndpointListenerHandler	pathFinderListener;
	private WSEndpointListenerHandler	topologyListener;
	private WSEndpointListenerHandler	nclProvListener;
	private WSEndpointListenerHandler	circuitProvListener;

	private WSEndpointListenerHandler	ofswitch1Listener;
	private WSEndpointListenerHandler	ofswitch2Listener;
	private WSEndpointListenerHandler	ofswitch3Listener;
	private WSEndpointListenerHandler	ofswitch4Listener;
	private WSEndpointListenerHandler	ofswitch5Listener;

	private static final String			PATHFINDING_CONTEXT					= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + PATHFINDING_CAPABILITY_TYPE;
	private static final String			CIRCUIT_PROV_CONTEXT				= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + CircuitProvisioningCapability.CAPABILITY_TYPE;
	private static final String			NCLPROV_CONTEXT						= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + NCLPROVISIONER_CAPABILITY_TYPE;
	private static final String			TOPOLOGY_CONTEXT					= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + NETTOPOLOGY_CAPABILITY_TYPE;

	private static final String			SWITCH1_FORWARDING_CONTEXT			= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_1_NAME + "/offorwarding";
	private static final String			SWITCH2_FORWARDING_CONTEXT			= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_2_NAME + "/offorwarding";
	private static final String			SWITCH3_FORWARDING_CONTEXT			= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_3_NAME + "/offorwarding";
	private static final String			SWITCH4_FORWARDING_CONTEXT			= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_4_NAME + "/offorwarding";
	private static final String			SWITCH5_FORWARDING_CONTEXT			= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_5_NAME + "/offorwarding";

	// mock sdn notifications
	ClientManager						mockClientManager;
	INCLNotifierClient					mockSdnClient;

	/**
	 * Make sure blueprint for specified bundle has finished its initialization
	 */
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch)", timeout = 50000)
	private BlueprintContainer			switchBlueprintContainer;
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)", timeout = 50000)
	private BlueprintContainer			floodlightDriverBundleContainer;
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.genericnetwork)", timeout = 50000)
	private BlueprintContainer			genericNetworkBlueprintContainer;
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.ofertie.ncl)", timeout = 50000)
	private BlueprintContainer			nclBlueprintContainer;

	@Inject
	private IProtocolManager			protocolManager;

	@Inject
	private IResourceManager			resourceManager;

	@Inject
	private INCLProvisioner				provisioner;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-openflowswitch", "opennaas-genericnetwork",
						"opennaas-openflowswitch-driver-floodlight", "opennaas-ofertie-ncl", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void createResources() throws Exception {
		createSwitches();
		createSDNNetwork();
		qosPolicyRequest = generateSampleFlowRequest();

		// mock and inject clientManager returning mocked sdnClient
		mockSdnClient = PowerMockito.mock(INCLNotifierClient.class);
		mockClientManager = PowerMockito.mock(ClientManager.class);
		Field f = provisioner.getClass().getDeclaredField("clientManager");
		f.setAccessible(true);
		f.set(provisioner, mockClientManager);

		PowerMockito.when(mockClientManager.getClient(Mockito.anyString())).thenReturn(mockSdnClient);

	}

	@After
	public void deleteResources() throws Exception {
		resourceManager.destroyAllResources();

		pathFinderListener.waitForEndpointToBeUnpublished();
		nclProvListener.waitForEndpointToBeUnpublished();
		circuitProvListener.waitForEndpointToBeUnpublished();
		topologyListener.waitForEndpointToBeUnpublished();
		ofswitch1Listener.waitForEndpointToBeUnpublished();
		ofswitch2Listener.waitForEndpointToBeUnpublished();
		ofswitch3Listener.waitForEndpointToBeUnpublished();
		ofswitch4Listener.waitForEndpointToBeUnpublished();
		ofswitch5Listener.waitForEndpointToBeUnpublished();

	}

	@Test
	public void test() throws Exception {
		testAllocateDeallocate(provisioner);
	}

	@Test
	public void wsTest() throws Exception {
		INCLProvisioner provisionerClient = InitializerTestHelper.createRestClient(WS_URI,
				INCLProvisioner.class, null, WS_USERNAME, WS_PASSWORD);
		testAllocateDeallocate(provisionerClient);
	}

	public void testAllocateDeallocate(INCLProvisioner provisioner) throws Exception {

		// 0) ALLOCATE FLOW
		String circuitId = provisioner.allocateFlow(qosPolicyRequest);

		// 1) check the qosPolicyRequest has been stored in the NCLProvisioner model.
		Map<String, QosPolicyRequest> flows = provisioner.readAllocatedFlows().getQoSPolicyRequests();
		Assert.assertNotNull("NCLProvisioner must contain allocated flows", flows);
		Assert.assertEquals("NCLProvisioner must contain an allocated flow.", 1, flows.size());
		Assert.assertTrue("NCLProvisioner must contain an allocated flow with the id it returned.", flows.keySet().contains(circuitId));

		QosPolicyRequest allocatedRequest = flows.get(circuitId);
		Assert.assertEquals("NCLProvisioner must contain the requested qosPolicyRequest", qosPolicyRequest, allocatedRequest);

		// 2) check the correct circuit has been created in the network.
		GenericNetworkModel sdnNetModel = (GenericNetworkModel) sdnNetResource.getModel();
		Map<String, Circuit> netAllocatedCircuits = sdnNetModel.getAllocatedCircuits();
		Assert.assertNotNull("Generic network should contain allocated circuits.", netAllocatedCircuits);
		Assert.assertEquals("Generic network should contain an allocated circuit.", 1, netAllocatedCircuits.size());
		Assert.assertTrue("Generic network should contain an allocated circuit with the id it returned.",
				netAllocatedCircuits.keySet().contains(circuitId));

		Circuit circuit = netAllocatedCircuits.get(circuitId);
		QosPolicyRequest parsedRequest = QosPolicyRequestParser.fromCircuit(circuit);
		Assert.assertEquals("Circuit stored in the generic network should be a translation of the qosPolicyRequest.", qosPolicyRequest, parsedRequest);
		Assert.assertEquals("Circuit should contain route with id 1.", "1", circuit.getRoute().getId());

		// 3) check the circuit implementation in the network

		Map<String, List<NetworkConnectionImplementationId>> sdnCircuitImplementation = sdnNetModel.getCircuitImplementation();
		Assert.assertNotNull("Generic network should contain circuit implementation.", sdnCircuitImplementation);
		Assert.assertEquals("Generic network should contain a circuit implementation.", 1, sdnCircuitImplementation.size());

		List<NetworkConnectionImplementationId> generatedCircuitImplementation = sdnCircuitImplementation.get(circuitId);
		Assert.assertNotNull("Circuit implementation should not be null.", generatedCircuitImplementation);
		Assert.assertEquals("Circuit implentation should consists of 4 network connections.", 4, generatedCircuitImplementation.size());

		List<String> netConnectionNames = new ArrayList<String>();
		for (NetworkConnectionImplementationId netConnImpl : generatedCircuitImplementation)
			netConnectionNames.add(netConnImpl.getDeviceId());

		Assert.assertTrue("Circuit implementation should pass through switch s5",
				netConnectionNames.contains(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_5_NAME));
		Assert.assertTrue("Circuit implementation should pass through switch s4",
				netConnectionNames.contains(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_4_NAME));
		Assert.assertTrue("Circuit implementation should pass through switch s2",
				netConnectionNames.contains(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_2_NAME));
		Assert.assertTrue("Circuit implementation should pass through switch s1",
				netConnectionNames.contains(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_1_NAME));
		Assert.assertFalse("Circuit implementation should not pass through switch s3",
				netConnectionNames.contains(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_3_NAME));

		// 4) Check flows of switches
		for (NetworkConnectionImplementationId netConnImpl : generatedCircuitImplementation)
		{
			IResource switchResource = getSwitchResourceFromName(netConnImpl.getDeviceId().split(":")[1]);
			IOpenflowForwardingCapability ofCapab =
					(IOpenflowForwardingCapability) switchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
			List<OFFlow> switchFlows = ofCapab.getOpenflowForwardingRules();

			// Get flow in switches s5
			if (netConnImpl.getDeviceId().equals(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_5_NAME)) {
				Assert.assertNotNull("Switch s5 should contain forwarding rules.", switchFlows);
				Assert.assertEquals("Switch s5 should contain a forwarding rule.", 1, switchFlows.size());

				OFFlow switchFlow = switchFlows.get(0);
				Assert.assertEquals(SRC_IP_ADDRESS, switchFlow.getMatch().getSrcIp());
				Assert.assertEquals("3", switchFlow.getMatch().getIngressPort());
				Assert.assertEquals("2", switchFlow.getActions().get(0).getValue());
			}
			// Get flow in switches s4
			else if (netConnImpl.getDeviceId().equals(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_4_NAME)) {
				Assert.assertNotNull("Switch s4 should contain forwarding rules.", switchFlows);
				Assert.assertEquals("Switch s4 should contain a forwarding rule.", 1, switchFlows.size());

				OFFlow switchFlow = switchFlows.get(0);
				Assert.assertEquals(SRC_IP_ADDRESS, switchFlow.getMatch().getSrcIp());
				Assert.assertEquals("3", switchFlow.getMatch().getIngressPort());
				Assert.assertEquals("1", switchFlow.getActions().get(0).getValue());
			}
			// Get flow in switches s1
			else if (netConnImpl.getDeviceId().equals(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_1_NAME)) {
				Assert.assertNotNull("Switch s1 should contain forwarding rules.", switchFlows);
				Assert.assertEquals("Switch s1 should contain a forwarding rule.", 1, switchFlows.size());

				OFFlow switchFlow = switchFlows.get(0);
				Assert.assertEquals(SRC_IP_ADDRESS, switchFlow.getMatch().getSrcIp());
				Assert.assertEquals("3", switchFlow.getMatch().getIngressPort());
				Assert.assertEquals("1", switchFlow.getActions().get(0).getValue());

			}
			// Get flow in switch s2
			else if (netConnImpl.getDeviceId().equals(OFSWITCH_RESOURCE_TYPE + ":" + SWITCH_2_NAME)) {
				Assert.assertNotNull("Switch s2 should contain forwarding rules.", switchFlows);
				Assert.assertEquals("Switch s2 should contain a forwarding rule.", 1, switchFlows.size());

				OFFlow switchFlow = switchFlows.get(0);
				Assert.assertEquals(SRC_IP_ADDRESS, switchFlow.getMatch().getSrcIp());
				Assert.assertEquals("1", switchFlow.getMatch().getIngressPort());
				Assert.assertEquals("3", switchFlow.getActions().get(0).getValue());
			}
			// Get flow in switch s3
			else
				Assert.assertEquals("Switch s3 should not contain forwarding rules.", 0, switchFlows.size());

		}
		// 5) DEALLOCATE FLOW

		provisioner.deallocateFlow(circuitId);
		flows = provisioner.readAllocatedFlows().getQoSPolicyRequests();
		Assert.assertTrue("There should not be allocated flows.", flows.isEmpty()); // Get flows in SDN network allocatedNetFlows =

		// Get allocated flow in SDN network
		netAllocatedCircuits = sdnNetModel.getAllocatedCircuits();
		Assert.assertEquals("There should be not allocated circuits in the network", 0, sdnNetModel.getAllocatedCircuits().size());
		Assert.assertEquals("There should be not allocated circuits implementations in the network", 0, sdnNetModel.getCircuitImplementation().size());

		// Get flow in switches
		IResource switchResource = getSwitchResourceFromName(SWITCH_5_NAME);
		IOpenflowForwardingCapability ofCapab = (IOpenflowForwardingCapability) switchResource
				.getCapabilityByInterface(IOpenflowForwardingCapability.class);
		List<OFFlow> switchFlows = ofCapab.getOpenflowForwardingRules();
		Assert.assertEquals("Switch s5 should not contain forwarding rules.", 0, switchFlows.size());

		switchResource = getSwitchResourceFromName(SWITCH_4_NAME);
		ofCapab = (IOpenflowForwardingCapability) switchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
		switchFlows = ofCapab.getOpenflowForwardingRules();
		Assert.assertEquals("Switch s4 should not contain forwarding rules.", 0, switchFlows.size());

		switchResource = getSwitchResourceFromName(SWITCH_1_NAME);
		ofCapab = (IOpenflowForwardingCapability) switchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
		switchFlows = ofCapab.getOpenflowForwardingRules();
		Assert.assertEquals("Switch s1 should not contain forwarding rules.", 0, switchFlows.size());

		switchResource = getSwitchResourceFromName(SWITCH_3_NAME);
		ofCapab = (IOpenflowForwardingCapability) switchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
		switchFlows = ofCapab.getOpenflowForwardingRules();
		Assert.assertEquals("Switch s3 should not contain forwarding rules.", 0, switchFlows.size());

		switchResource = getSwitchResourceFromName(SWITCH_2_NAME);
		ofCapab = (IOpenflowForwardingCapability) switchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
		switchFlows = ofCapab.getOpenflowForwardingRules();
		Assert.assertEquals("Switch s2 should not contain forwarding rules.", 0, switchFlows.size());

	}

	private IResource getSwitchResourceFromName(String deviceName) {
		return switches.get(deviceName);
	}

	private QosPolicyRequest generateSampleFlowRequest() {
		QosPolicyRequest req = new QosPolicyRequest();

		Source source = new Source();
		source.setAddress(SRC_IP_ADDRESS);
		source.setPort(String.valueOf(SRC_PORT));
		req.setSource(source);

		Destination destination = new Destination();
		destination.setAddress(DST_IP_ADDRESS);
		destination.setPort(String.valueOf(DST_PORT));
		req.setDestination(destination);

		req.setLabel(String.valueOf(TOS));

		QosPolicy qosPolicy = new QosPolicy();

		Latency latency = new Latency();
		latency.setMin(String.valueOf(QOS_MIN_LATENCY));
		latency.setMax(String.valueOf(QOS_MAX_LATENCY));
		qosPolicy.setLatency(latency);

		Jitter jitter = new Jitter();
		jitter.setMin(String.valueOf(QOS_MIN_JITTER));
		jitter.setMax(String.valueOf(QOS_MAX_JITTER));
		qosPolicy.setJitter(jitter);

		Throughput throughput = new Throughput();
		throughput.setMin(String.valueOf(QOS_MIN_THROUGHPUT));
		throughput.setMax(String.valueOf(QOS_MAX_THROUGHPUT));
		qosPolicy.setThroughput(throughput);

		PacketLoss packetLoss = new PacketLoss();
		packetLoss.setMin(String.valueOf(QOS_MIN_PACKET_LOSS));
		packetLoss.setMax(String.valueOf(QOS_MAX_PACKET_LOSS));
		qosPolicy.setPacketLoss(packetLoss);

		req.setQosPolicy(qosPolicy);

		return req;
	}

	private void createSwitches() throws ResourceException, ProtocolException, InterruptedException {
		switches = new HashMap<String, IResource>();

		ofswitch1Listener = new WSEndpointListenerHandler();
		ofswitch1Listener.registerWSEndpointListener(SWITCH1_FORWARDING_CONTEXT, context);
		switches.put(SWITCH_1_NAME, createSwitch(SWITCH_1_ID, SWITCH_1_NAME));
		ofswitch1Listener.waitForEndpointToBePublished();

		ofswitch2Listener = new WSEndpointListenerHandler();
		ofswitch2Listener.registerWSEndpointListener(SWITCH2_FORWARDING_CONTEXT, context);
		switches.put(SWITCH_2_NAME, createSwitch(SWITCH_2_ID, SWITCH_2_NAME));
		ofswitch2Listener.waitForEndpointToBePublished();

		ofswitch3Listener = new WSEndpointListenerHandler();
		ofswitch3Listener.registerWSEndpointListener(SWITCH3_FORWARDING_CONTEXT, context);
		switches.put(SWITCH_3_NAME, createSwitch(SWITCH_3_ID, SWITCH_3_NAME));
		ofswitch3Listener.waitForEndpointToBePublished();

		ofswitch4Listener = new WSEndpointListenerHandler();
		ofswitch4Listener.registerWSEndpointListener(SWITCH4_FORWARDING_CONTEXT, context);
		switches.put(SWITCH_4_NAME, createSwitch(SWITCH_4_ID, SWITCH_4_NAME));
		ofswitch4Listener.waitForEndpointToBePublished();

		ofswitch5Listener = new WSEndpointListenerHandler();
		ofswitch5Listener.registerWSEndpointListener(SWITCH5_FORWARDING_CONTEXT, context);
		switches.put(SWITCH_5_NAME, createSwitch(SWITCH_5_ID, SWITCH_5_NAME));
		ofswitch5Listener.waitForEndpointToBePublished();
	}

	/**
	 * Creates a GenericNetwork resource with NCLProvisionerCapability, PathFindingCapability and CircuitProvisioningCapability.
	 * 
	 * @throws ResourceException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void createSDNNetwork() throws ResourceException, InterruptedException, IOException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor circuitAggregationDescriptor = generateCircuitAggregationCapabilityDescriptor();
		CapabilityDescriptor topologyDescriptor = generateTopologyCapabilityDescriptor();
		CapabilityDescriptor pathFindingDescriptor = generatePathFindingCapabilityDescriptor();
		CapabilityDescriptor circuitProvisioningDescriptor = generateCircuitProvisioningCapabilityDescriptor();
		CapabilityDescriptor nclProvisioningDescriptor = generateNCLProvisioningCapabilityDescriptor();

		lCapabilityDescriptors.add(circuitAggregationDescriptor);
		lCapabilityDescriptors.add(pathFindingDescriptor);
		lCapabilityDescriptors.add(topologyDescriptor);
		lCapabilityDescriptors.add(circuitProvisioningDescriptor);
		lCapabilityDescriptors.add(nclProvisioningDescriptor);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, GENERICNET_RESOURCE_TYPE,
				MOCK_URI, SDN_RESOURCE_NAME);

		sdnNetResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		topologyListener = new WSEndpointListenerHandler();
		nclProvListener = new WSEndpointListenerHandler();
		circuitProvListener = new WSEndpointListenerHandler();
		pathFinderListener = new WSEndpointListenerHandler();

		topologyListener.registerWSEndpointListener(TOPOLOGY_CONTEXT, context);
		nclProvListener.registerWSEndpointListener(NCLPROV_CONTEXT, context);
		circuitProvListener.registerWSEndpointListener(CIRCUIT_PROV_CONTEXT, context);
		pathFinderListener.registerWSEndpointListener(PATHFINDING_CONTEXT, context);

		resourceManager.startResource(sdnNetResource.getResourceIdentifier());
		topologyListener.waitForEndpointToBePublished();
		nclProvListener.waitForEndpointToBePublished();
		circuitProvListener.waitForEndpointToBePublished();
		pathFinderListener.waitForEndpointToBePublished();

	}

	private CapabilityDescriptor generateNCLProvisioningCapabilityDescriptor() {
		CapabilityDescriptor nclProvDescriptor = ResourceHelper.newCapabilityDescriptorWithoutActionset(NCLPROVISIONER_CAPABILITY_TYPE);

		return nclProvDescriptor;
	}

	private CapabilityDescriptor generateCircuitAggregationCapabilityDescriptor() throws IOException {

		CapabilityDescriptor topologyDescriptor = ResourceHelper.newCapabilityDescriptor(INTERNAL_ACTIONSET_NAME,
				CAPABILITY_VERSION, CIRCUIT_AGGREGATION_CAPABILITY_TYPE, MOCK_URI);

		CapabilityProperty useAggregation = new CapabilityProperty();

		useAggregation.setName(CircuitAggregationCapability.USE_AGGREGATION_PROPERTY);
		useAggregation.setValue("false");

		topologyDescriptor.getCapabilityProperties().add(useAggregation);

		return topologyDescriptor;
	}

	private CapabilityDescriptor generateTopologyCapabilityDescriptor() throws IOException {

		CapabilityDescriptor topologyDescriptor = ResourceHelper.newCapabilityDescriptor(INTERNAL_ACTIONSET_NAME,
				CAPABILITY_VERSION, NETTOPOLOGY_CAPABILITY_TYPE, MOCK_URI);

		CapabilityProperty topologyFile = new CapabilityProperty();

		String topologyFileAbsolutePath = obtainTopologyAbsolutePath();

		topologyFile.setName(NetTopologyCapability.TOPOLOGY_FILE);
		topologyFile.setValue(topologyFileAbsolutePath);

		topologyDescriptor.getCapabilityProperties().add(topologyFile);

		return topologyDescriptor;
	}

	private CapabilityDescriptor generatePathFindingCapabilityDescriptor() throws IOException {
		CapabilityDescriptor pathFindingDescriptor = ResourceHelper.newCapabilityDescriptor(INTERNAL_ACTIONSET_NAME,
				CAPABILITY_VERSION, PATHFINDING_CAPABILITY_TYPE, MOCK_URI);

		CapabilityProperty routeURIProperty = new CapabilityProperty();
		routeURIProperty.setName(PathFindingParamsMapping.ROUTES_FILE_KEY);
		routeURIProperty.setValue(readFile(ROUTE_FILES_URI));

		CapabilityProperty mapURImapURIProperty = new CapabilityProperty();
		mapURImapURIProperty.setName(PathFindingParamsMapping.ROUTES_MAPPING_KEY);
		mapURImapURIProperty.setValue(readFile(ROUTES_MAPPING_URI));

		pathFindingDescriptor.getCapabilityProperties().add(routeURIProperty);
		pathFindingDescriptor.getCapabilityProperties().add(mapURImapURIProperty);

		return pathFindingDescriptor;
	}

	private CapabilityDescriptor generateCircuitProvisioningCapabilityDescriptor() {
		CapabilityDescriptor circuitProvisioningDescriptor = ResourceHelper.newCapabilityDescriptor(
				CircuitProvisioningActionsetImplementation.ACTIONSET_ID, CAPABILITY_VERSION, CircuitProvisioningCapability.CAPABILITY_TYPE, MOCK_URI);

		return circuitProvisioningDescriptor;
	}

	private IResource createSwitch(String switchId, String switchName) throws ResourceException, ProtocolException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(FLOODLIGHT_ACTIONSET_NAME,
				FLOODLIGHT_ACTIONSET_VERSION, OpenflowForwardingCapability.CAPABILITY_TYPE, MOCK_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFSWITCH_RESOURCE_TYPE,
				MOCK_URI, switchName);

		IResource switchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, switchId);
		sessionParameters.put(ProtocolSessionContext.AUTH_TYPE, "noauth");

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, switchResource.getResourceIdentifier().getId(), PROTOCOL_URI,
				FLOODLIGHT_PROTOCOL, sessionParameters);

		// Start resource
		resourceManager.startResource(switchResource.getResourceIdentifier());

		// COMMENT THIS LINE BELOW TO LAUNCH THE TEST IN THE REAL ENVIRONMENT
		// BE SURE TO HAVE PROTOCOL_URI POINTING TO YOUR FLOODLIGHT CONTROLLER
		prepareClientForSwitch(switchResource);

		return switchResource;
	}

	/**
	 * Overrides IFloodlightStaticFlowPusherClient in floodlight protocol session, with a FloodlightMockClient.
	 * 
	 * @param switchResource
	 * @throws ProtocolException
	 */
	private void prepareClientForSwitch(IResource switchResource) throws ProtocolException {

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(switchResource.getResourceIdentifier().getId());

		FloodlightProtocolSession session = (FloodlightProtocolSession) sessionManager.obtainSessionByProtocol(
				FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE, false);

		session.setClientFactory(new FloodlightMockClientFactory());
		IFloodlightStaticFlowPusherClient client = session.getClientFactory().createClient(session.getSessionContext());
		session.setFloodlightClient(client);

	}

	private String readFile(String url) throws IOException {
		InputStream input = this.getClass().getResourceAsStream(url);
		File tmp = File.createTempFile(url, ".tmp.xml");
		tmp.deleteOnExit();

		IOUtils.copy(input, new FileOutputStream(tmp));
		return tmp.getAbsolutePath();
	}

	private String obtainTopologyAbsolutePath() throws IOException {
		InputStream input = this.getClass().getResourceAsStream(TOPOLOGY_FILE_URI);
		File tmp = File.createTempFile("nettopology", ".tmp.xml");
		tmp.deleteOnExit();

		IOUtils.copy(input, new FileOutputStream(tmp));
		return tmp.getAbsolutePath();
	}

}
