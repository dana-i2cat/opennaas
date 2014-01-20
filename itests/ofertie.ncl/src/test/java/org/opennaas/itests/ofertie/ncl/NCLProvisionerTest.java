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

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemTimeout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup.FloodlightMockClientFactory;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * 
 * @author Adrian Rosello (i2cat)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class NCLProvisionerTest {

	private IResource					sdnNetResource;

	private QosPolicyRequest			qosPolicyRequest;

	private Map<String, IResource>		switches;

	private static final String			SWITCH_1_NAME					= "s1";
	private static final String			SWITCH_2_NAME					= "s2";
	private static final String			SWITCH_3_NAME					= "s3";
	private static final String			SWITCH_4_NAME					= "s4";
	private static final String			SWITCH_5_NAME					= "s5";

	private static final String			SWITCH_1_ID						= "00:00:00:00:00:00:00:01";
	private static final String			SWITCH_2_ID						= "00:00:00:00:00:00:00:02";
	private static final String			SWITCH_3_ID						= "00:00:00:00:00:00:00:03";
	private static final String			SWITCH_4_ID						= "00:00:00:00:00:00:00:04";
	private static final String			SWITCH_5_ID						= "00:00:00:00:00:00:00:05";

	private static final String			FLOODLIGHT_ACTIONSET_NAME		= "floodlight";
	private static final String			FLOODLIGHT_ACTIONSET_VERSION	= "0.90";
	private static final String			FLOODLIGHT_PROTOCOL				= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;

	private static final String			OFSWITCH_RESOURCE_TYPE			= "openflowswitch";
	private static final String			SWITCH_ID_NAME					= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private static final String			CAPABILITY_URI					= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String			RESOURCE_URI					= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String			PROTOCOL_URI					= "http://dev.ofertie.i2cat.net:8080";

	private static final String			SDN_ACTIONSET_NAME				= "internal";
	private static final String			SDN_ACTIONSET_VERSION			= "1.0.0";

	private static final String			SDN_RESOURCE_NAME				= "sdnNetwork";
	private static final String			OFNETWORK_RESOURCE_TYPE			= "ofnetwork";

	/* FLOW REQUEST PARAMS */
	private static final String			SRC_IP_ADDRESS					= "192.168.10.10";
	private static final String			DST_IP_ADDRESS					= "192.168.10.11";
	private static final int			SRC_PORT						= 0;
	private static final int			DST_PORT						= 1;
	private static final int			TOS								= 0;
	private static final int			QOS_MIN_LATENCY					= 5;
	private static final int			QOS_MAX_LATENCY					= 10;
	private static final int			QOS_MIN_JITTER					= 2;
	private static final int			QOS_MAX_JITTER					= 4;
	private static final int			QOS_MIN_THROUGHPUT				= 100;
	private static final int			QOS_MAX_THROUGHPUT				= 1000;
	private static final int			QOS_MIN_PACKET_LOSS				= 0;
	private static final int			QOS_MAX_PACKET_LOSS				= 1;

	private static final String			WS_URI							= "http://localhost:8888/opennaas/ofertie/ncl";
	private static final String			WS_USERNAME						= "admin";
	private static final String			WS_PASSWORD						= "123456";

	@Inject
	protected BundleContext				context;

	// /// ENDPOINT LISTENERS //// //
	private WSEndpointListenerHandler	sdnListener;
	private WSEndpointListenerHandler	ofswitch1Listener;
	private WSEndpointListenerHandler	ofswitch2Listener;
	private WSEndpointListenerHandler	ofswitch3Listener;
	private WSEndpointListenerHandler	ofswitch4Listener;
	private WSEndpointListenerHandler	ofswitch5Listener;

	private static final String			OFNET_PROVISION_CONTEXT			= "/opennaas/" + OFNETWORK_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/ofprovisionnet";
	private static final String			SWITCH1_FORWARDING_CONTEXT		= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_1_NAME + "/offorwarding";
	private static final String			SWITCH2_FORWARDING_CONTEXT		= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_2_NAME + "/offorwarding";
	private static final String			SWITCH3_FORWARDING_CONTEXT		= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_3_NAME + "/offorwarding";
	private static final String			SWITCH4_FORWARDING_CONTEXT		= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_4_NAME + "/offorwarding";
	private static final String			SWITCH5_FORWARDING_CONTEXT		= "/opennaas/" + OFSWITCH_RESOURCE_TYPE + "/" + SWITCH_5_NAME + "/offorwarding";

	/**
	 * Make sure blueprint for specified bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch)", timeout = 50000)
	private BlueprintContainer			switchBlueprintContainer;
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)", timeout = 50000)
	private BlueprintContainer			floodlightDriverBundleContainer;
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.ofnetwork)", timeout = 50000)
	private BlueprintContainer			ofNetworkBlueprintContainer;
	@SuppressWarnings("unused")
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
				opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflowswitch", "opennaas-ofnetwork", "opennaas-openflowswitch-driver-floodlight",
						"opennaas-ofertie-ncl", "itests-helpers"),
				systemTimeout(1000 * 60 * 10),
				noConsole(),
				// OpennaasExamOptions.openDebugSocket(),
				keepRuntimeFolder());
	}

	@Before
	public void createResources() throws Exception {
		createSwitches();
		createSDNNetwork();
		qosPolicyRequest = generateSampleFlowRequest();
	}

	@After
	public void deleteResources() throws Exception {
		resourceManager.destroyAllResources();
		sdnListener.waitForEndpointToBeUnpublished();
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
		INCLProvisioner provisionerClient = InitializerTestHelper.createRestClient(WS_URI, INCLProvisioner.class, null, WS_USERNAME, WS_PASSWORD);
		testAllocateDeallocate(provisionerClient);
	}

	public void testAllocateDeallocate(INCLProvisioner provisioner) throws Exception {

		String id = provisioner.allocateFlow(qosPolicyRequest);

		Map<String, QosPolicyRequest> flows = provisioner.readAllocatedFlows().getQoSPolicyRequests();
		QosPolicyRequest allocatedFlow = null;
		for (Entry<String, QosPolicyRequest> entry : flows.entrySet()) {
			if (entry.getKey().equals(id)) {
				allocatedFlow = entry.getValue();
				break;
			}
		}
		Assert.assertNotNull("readAllocatedFlows() must contain allocated flow", allocatedFlow);

		List<NetOFFlow> netFlows = provisioner.getFlowImplementation(id).getItems();
		Assert.assertNotNull("implementation must not be null", netFlows);
		Assert.assertFalse("implementation must not be empty", netFlows.isEmpty());

		// Get flows in SDN network
		IOFProvisioningNetworkCapability sdnCapab = (IOFProvisioningNetworkCapability) sdnNetResource
				.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);
		Set<NetOFFlow> allocatedNetFlows = sdnCapab.getAllocatedFlows();

		for (NetOFFlow expected : netFlows) {
			Assert.assertTrue("expected flows are present in the network", allocatedNetFlows.contains(expected));

			// Get flow in switches
			IResource switchResource = getSwitchResourceFromName(expected.getResourceId());
			IOpenflowForwardingCapability s3capab = (IOpenflowForwardingCapability) switchResource
					.getCapabilityByInterface(IOpenflowForwardingCapability.class);
			List<FloodlightOFFlow> switchFlows = s3capab.getOpenflowForwardingRules();
			FloodlightOFFlow switchFlow = null;
			for (FloodlightOFFlow flow : switchFlows) {
				if (flow.getName().equals(expected.getName())) {
					switchFlow = flow;
					break;
				}
			}
			Assert.assertNotNull("switch has flow with flowId equals to expected one", switchFlow);
		}

		provisioner.deallocateFlow(id);
		flows = provisioner.readAllocatedFlows().getQoSPolicyRequests();
		Assert.assertTrue("There should not be allocated flows.", flows.isEmpty());
		// Get flows in SDN network
		allocatedNetFlows = sdnCapab.getAllocatedFlows();
		// Get allocated flow in SDN network
		Assert.assertEquals("There should be not allocated sdnFlow", 0, allocatedNetFlows.size());

		for (NetOFFlow past : netFlows) {
			Assert.assertFalse("past flow is no longer present in the network", allocatedNetFlows.contains(past));

			// Get flow in switches
			IResource switchResource = getSwitchResourceFromName(past.getResourceId());
			IOpenflowForwardingCapability s3capab = (IOpenflowForwardingCapability) switchResource
					.getCapabilityByInterface(IOpenflowForwardingCapability.class);
			List<FloodlightOFFlow> switchFlows = s3capab.getOpenflowForwardingRules();
			FloodlightOFFlow switchFlow = null;
			for (FloodlightOFFlow flow : switchFlows) {
				if (flow.getName().equals(past.getName())) {
					switchFlow = flow;
					break;
				}
			}
			Assert.assertNull("past flow is no longer present in the switch it was", switchFlow);
		}

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

	private void createSDNNetwork() throws ResourceException, InterruptedException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor provisionCapab = ResourceHelper.newCapabilityDescriptor(SDN_ACTIONSET_NAME,
				SDN_ACTIONSET_VERSION, OFProvisioningNetworkCapability.CAPABILITY_TYPE, CAPABILITY_URI);

		lCapabilityDescriptors.add(provisionCapab);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFNETWORK_RESOURCE_TYPE,
				RESOURCE_URI, SDN_RESOURCE_NAME);

		sdnNetResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		sdnListener = new WSEndpointListenerHandler();
		sdnListener.registerWSEndpointListener(OFNET_PROVISION_CONTEXT, context);
		resourceManager.startResource(sdnNetResource.getResourceIdentifier());
		sdnListener.waitForEndpointToBePublished();
	}

	private IResource createSwitch(String switchId, String switchName) throws ResourceException, ProtocolException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(FLOODLIGHT_ACTIONSET_NAME,
				FLOODLIGHT_ACTIONSET_VERSION, OpenflowForwardingCapability.CAPABILITY_TYPE, CAPABILITY_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFSWITCH_RESOURCE_TYPE,
				RESOURCE_URI, switchName);

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
}
