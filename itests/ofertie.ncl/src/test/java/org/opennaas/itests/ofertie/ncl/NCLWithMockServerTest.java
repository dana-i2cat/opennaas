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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.genericnetwork.actionsets.internal.circuitprovisioning.CircuitProvisioningActionsetImplementation;
import org.opennaas.extensions.genericnetwork.capability.circuitaggregation.CircuitAggregationCapability;
import org.opennaas.extensions.genericnetwork.capability.circuitprovisioning.CircuitProvisioningCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.NCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.capability.nettopology.NetTopologyCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingParamsMapping;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.server.HTTPRequest;
import org.opennaas.itests.helpers.server.HTTPResponse;
import org.opennaas.itests.helpers.server.HTTPServerBehaviour;
import org.opennaas.itests.helpers.server.MockHTTPServerTest;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class NCLWithMockServerTest extends MockHTTPServerTest {

	private static final String			ROUTE_FILES_URI						= "/route5switches.xml";
	private static final String			ROUTES_MAPPING_URI					= "/mapping5switches.xml";
	private static final String			TOPOLOGY_FILE_URI					= "/topologies/topology5switches.xml";

	// //// SWITCHES //// //
	private static final String			SWITCH_1_ID							= "00:00:00:00:00:00:00:01";
	private static final String			SWITCH_2_ID							= "00:00:00:00:00:00:00:02";
	private static final String			SWITCH_3_ID							= "00:00:00:00:00:00:00:03";
	private static final String			SWITCH_4_ID							= "00:00:00:00:00:00:00:04";
	private static final String			SWITCH_5_ID							= "00:00:00:00:00:00:00:05";
	private final static String			SWITCH_1_NAME						= "s1";
	private final static String			SWITCH_2_NAME						= "s2";
	private final static String			SWITCH_3_NAME						= "s3";
	private final static String			SWITCH_4_NAME						= "s4";
	private final static String			SWITCH_5_NAME						= "s5";
	private IResource					switchResource1;
	private IResource					switchResource2;
	private IResource					switchResource3;
	private IResource					switchResource4;
	private IResource					switchResource5;
	private static final String			SWITCH1_FORWARDING_CONTEXT			= "/opennaas/openflowswitch/s1/offorwarding";
	private static final String			SWITCH2_FORWARDING_CONTEXT			= "/opennaas/openflowswitch/s2/offorwarding";
	private static final String			SWITCH3_FORWARDING_CONTEXT			= "/opennaas/openflowswitch/s3/offorwarding";
	private static final String			SWITCH4_FORWARDING_CONTEXT			= "/opennaas/openflowswitch/s4/offorwarding";
	private static final String			SWITCH5_FORWARDING_CONTEXT			= "/opennaas/openflowswitch/s5/offorwarding";
	private static final String			ACTIONSET_NAME						= "floodlight";
	private static final String			ACTIONSET_VERSION					= "0.90";
	private static final String			OFSWITCH_RESOURCE_TYPE				= "openflowswitch";
	private static final String			SWITCH_ID_NAME						= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;
	private static final String			FLOODLIGHT_PROTOCOL					= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;

	// //// SDN NETWORK //// //
	private IResource					sdnNetResource;
	private static final String			SDN_RESOURCE_NAME					= "sdnNetwork";

	private static final String			GENERICNET_RESOURCE_TYPE			= "genericnetwork";

	private static final String			INTERNAL_ACTIONSET_NAME				= "internal";
	private static final String			CAPABILITY_VERSION					= "1.0.0";

	private static final String			CIRCUIT_AGGREGATION_CAPABILITY_TYPE	= CircuitAggregationCapability.CAPABILITY_TYPE;
	private static final String			PATHFINDING_CAPABILITY_TYPE			= PathFindingCapability.CAPABILITY_TYPE;
	private static final String			NCLPROVISIONER_CAPABILITY_TYPE		= NCLProvisionerCapability.CAPABILITY_TYPE;
	private static final String			NETTOPOLOGY_CAPABILITY_TYPE			= NetTopologyCapability.CAPABILITY_TYPE;

	// //// SERVER INFO //// //
	private final static String			SERVER_URL							= "http://localhost:8080";
	private final static String			SERVLET_CONTEXT_URL					= "/wm/staticflowentrypusher";
	private final static String			FLOODLIGHT_ADD_FLOW_URL				= SERVLET_CONTEXT_URL + "/json";
	private final static String			GET_FLOWS_URL_SWITCH1				= SERVLET_CONTEXT_URL + "/list/" + SWITCH_1_ID + "/json";
	private final static String			GET_FLOWS_URL_SWITCH2				= SERVLET_CONTEXT_URL + "/list/" + SWITCH_2_ID + "/json";
	private final static String			GET_FLOWS_URL_SWITCH4				= SERVLET_CONTEXT_URL + "/list/" + SWITCH_4_ID + "/json";
	private final static String			GET_FLOWS_URL_SWITCH5				= SERVLET_CONTEXT_URL + "/list/" + SWITCH_5_ID + "/json";

	// /// LISTENERS CONTEXTS /// //
	private static final String			PATHFINDING_CONTEXT					= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + PATHFINDING_CAPABILITY_TYPE;
	private static final String			CIRCUIT_PROV_CONTEXT				= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + CircuitProvisioningCapability.CAPABILITY_TYPE;
	private static final String			NCLPROV_CONTEXT						= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + NCLPROVISIONER_CAPABILITY_TYPE;
	private static final String			TOPOLOGY_CONTEXT					= "/opennaas/" + GENERICNET_RESOURCE_TYPE + "/" + SDN_RESOURCE_NAME + "/" + NETTOPOLOGY_CAPABILITY_TYPE;

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

	// //// LOG //// //
	private final static Log			log									= LogFactory.getLog(NCLWithMockServerTest.class);

	// /// OTHERS //// //
	private static final String			MOCK_URI							= "mock://user:pass@host.net:2212/mocksubsystem";
	private final static String			FLOODLIGHT_ADD_FLOW_RESPONSE		= "Entry pushed";

	private static final String			DEFAULT_FLOW_PRIORITY				= "32000";
	private static final String			IP_ETHER_TYPE						= "0x0800";
	private static final String			SRC_IP								= "192.168.10.10";
	private static final String			DST_IP								= "192.168.10.11";
	@Inject
	private IResourceManager			resourceManager;

	@Inject
	protected BundleContext				context;

	@Inject
	protected IProtocolManager			protocolManager;

	@Inject
	private INCLProvisioner				provisioner;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflowswitch", "opennaas-genericnetwork", "opennaas-openflowswitch-driver-floodlight",
						"opennaas-ofertie-ncl", "itests-helpers"),
				noConsole(),
				// OpennaasExamOptions.openDebugSocket(),
				keepRuntimeFolder());
	}

	@Before
	public void initTestScenario() throws Exception {

		log.info("Creating initial scenario.");

		prepareBehaviours();
		startServer(SERVLET_CONTEXT_URL);

		createSDNNetwork();
		createSwitches();

		log.info("Test initialized.");
	}

	@After
	public void shutDownTestScenario() throws Exception {

		log.info("Shutting down test scenario.");

		stopServer();
		stopResources();

		log.info("Test finished.");

	}

	@Override
	protected void prepareBehaviours() throws Exception {
		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();

		List<String> fieldsToOmit = new ArrayList<String>();
		fieldsToOmit.add("name");

		HTTPRequest flow2Req = new HTTPRequest(FLOODLIGHT_ADD_FLOW_URL, HttpMethod.POST, MediaType.APPLICATION_JSON,
				readSampleFile("/allocateFlowSwitch2.json"), fieldsToOmit);
		HTTPResponse flow2Resp = new HTTPResponse(HttpStatus.CREATED_201, MediaType.APPLICATION_JSON, FLOODLIGHT_ADD_FLOW_RESPONSE, "");
		HTTPServerBehaviour flow2Behaviour = new HTTPServerBehaviour(flow2Req, flow2Resp, true);
		desiredBehaviours.add(flow2Behaviour);

		HTTPRequest getFlowsS2Req = new HTTPRequest(GET_FLOWS_URL_SWITCH2, HttpMethod.GET, MediaType.APPLICATION_JSON, "", new ArrayList<String>());
		HTTPResponse getFlows2Resp = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON,
				readSampleFile("/getFlowsSwitch2.json"), "");
		HTTPServerBehaviour behaviourgetFlows2 = new HTTPServerBehaviour(getFlowsS2Req, getFlows2Resp, true);
		desiredBehaviours.add(behaviourgetFlows2);

		HTTPRequest flow1Req = new HTTPRequest(FLOODLIGHT_ADD_FLOW_URL, HttpMethod.POST, MediaType.APPLICATION_JSON,
				readSampleFile("/allocateFlowSwitch1.json"), fieldsToOmit);
		HTTPResponse flow1Resp = new HTTPResponse(HttpStatus.CREATED_201, MediaType.APPLICATION_JSON, FLOODLIGHT_ADD_FLOW_RESPONSE, "");
		HTTPServerBehaviour flow1Behaviour = new HTTPServerBehaviour(flow1Req, flow1Resp, true);
		desiredBehaviours.add(flow1Behaviour);

		HTTPRequest getFlowsS1Req = new HTTPRequest(GET_FLOWS_URL_SWITCH1, HttpMethod.GET, MediaType.APPLICATION_JSON, "", new ArrayList<String>());
		HTTPResponse getFlows1Resp = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON,
				readSampleFile("/getFlowsSwitch1.json"), "");
		HTTPServerBehaviour behaviourgetFlows1 = new HTTPServerBehaviour(getFlowsS1Req, getFlows1Resp, true);
		desiredBehaviours.add(behaviourgetFlows1);

		HTTPRequest flow4Req = new HTTPRequest(FLOODLIGHT_ADD_FLOW_URL, HttpMethod.POST, MediaType.APPLICATION_JSON,
				readSampleFile("/allocateFlowSwitch4.json"), fieldsToOmit);
		HTTPResponse flow4Resp = new HTTPResponse(HttpStatus.CREATED_201, MediaType.APPLICATION_JSON, FLOODLIGHT_ADD_FLOW_RESPONSE, "");
		HTTPServerBehaviour flow4Behaviour = new HTTPServerBehaviour(flow4Req, flow4Resp, true);
		desiredBehaviours.add(flow4Behaviour);

		HTTPRequest getFlowsS4Req = new HTTPRequest(GET_FLOWS_URL_SWITCH4, HttpMethod.GET, MediaType.APPLICATION_JSON, "", new ArrayList<String>());
		HTTPResponse getFlows4Resp = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON,
				readSampleFile("/getFlowsSwitch4.json"), "");
		HTTPServerBehaviour behaviourgetFlows4 = new HTTPServerBehaviour(getFlowsS4Req, getFlows4Resp, true);
		desiredBehaviours.add(behaviourgetFlows4);

		HTTPRequest flow5Req = new HTTPRequest(FLOODLIGHT_ADD_FLOW_URL, HttpMethod.POST, MediaType.APPLICATION_JSON,
				readSampleFile("/allocateFlowSwitch5.json"), fieldsToOmit);
		HTTPResponse flow5Resp = new HTTPResponse(HttpStatus.CREATED_201, MediaType.APPLICATION_JSON, FLOODLIGHT_ADD_FLOW_RESPONSE, "");
		HTTPServerBehaviour flow5Behaviour = new HTTPServerBehaviour(flow5Req, flow5Resp, true);
		desiredBehaviours.add(flow5Behaviour);

		HTTPRequest getFlowsS5Req = new HTTPRequest(GET_FLOWS_URL_SWITCH5, HttpMethod.GET, MediaType.APPLICATION_JSON, "", new ArrayList<String>());
		HTTPResponse getFlows5Resp = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON,
				readSampleFile("/getFlowsSwitch5.json"), "");
		HTTPServerBehaviour behaviourgetFlows5 = new HTTPServerBehaviour(getFlowsS5Req, getFlows5Resp, true);
		desiredBehaviours.add(behaviourgetFlows5);

	}

	@Test
	public void allocateMostSpecificFlowTest() throws FlowAllocationException, ProvisionerException {

		QosPolicyRequest req = generateQosPolicyRequest();
		provisioner.allocateFlow(req);

		checkSwitchesRules();

	}

	private void checkSwitchesRules() {

		OpenflowSwitchModel switch1Model = (OpenflowSwitchModel) switchResource1.getModel();
		OpenflowSwitchModel switch2Model = (OpenflowSwitchModel) switchResource2.getModel();
		OpenflowSwitchModel switch3Model = (OpenflowSwitchModel) switchResource3.getModel();
		OpenflowSwitchModel switch4Model = (OpenflowSwitchModel) switchResource4.getModel();
		OpenflowSwitchModel switch5Model = (OpenflowSwitchModel) switchResource5.getModel();

		Assert.assertNotNull(switch1Model);
		Assert.assertNotNull(switch2Model);
		Assert.assertNotNull(switch3Model);
		Assert.assertNotNull(switch4Model);
		Assert.assertNotNull(switch5Model);

		Assert.assertEquals(SWITCH_1_ID, switch1Model.getSwitchId());
		Assert.assertEquals(SWITCH_2_ID, switch2Model.getSwitchId());
		Assert.assertEquals(SWITCH_4_ID, switch4Model.getSwitchId());
		Assert.assertEquals(SWITCH_5_ID, switch5Model.getSwitchId());

		// TODO Switch3Id should be empty in test. It's set by the allocateFlowAction, that it's not called
		// because it does not belong to the selected route. When moving this functionality to another better side,
		// fix this Assert.
		Assert.assertFalse(switch3Model.getSwitchId().equals("SWITCH_3_ID"));
		Assert.assertTrue(switch3Model.getSwitchId().isEmpty());

		Assert.assertEquals(1, switch1Model.getOfTables().size());
		Assert.assertEquals(1, switch2Model.getOfTables().size());
		Assert.assertEquals(1, switch4Model.getOfTables().size());
		Assert.assertEquals(1, switch5Model.getOfTables().size());

		OFFlowTable tableSwitch1 = switch1Model.getOfTables().get(0);
		OFFlowTable tableSwitch2 = switch2Model.getOfTables().get(0);
		OFFlowTable tableSwitch4 = switch4Model.getOfTables().get(0);
		OFFlowTable tableSwitch5 = switch5Model.getOfTables().get(0);

		Assert.assertNotNull(tableSwitch1);
		Assert.assertNotNull(tableSwitch2);
		Assert.assertNotNull(tableSwitch4);
		Assert.assertNotNull(tableSwitch5);

		List<FloodlightOFFlow> rulesSwitch1 = tableSwitch1.getOfForwardingRules();
		List<FloodlightOFFlow> rulesSwitch2 = tableSwitch2.getOfForwardingRules();
		List<FloodlightOFFlow> rulesSwitch4 = tableSwitch4.getOfForwardingRules();
		List<FloodlightOFFlow> rulesSwitch5 = tableSwitch5.getOfForwardingRules();

		Assert.assertEquals(1, rulesSwitch1.size());
		Assert.assertEquals(1, rulesSwitch2.size());
		Assert.assertEquals(1, rulesSwitch4.size());
		Assert.assertEquals(1, rulesSwitch5.size());

		// Forwarding rule switch 1
		FloodlightOFFlow flowSwitch1 = rulesSwitch1.get(0);
		Assert.assertNotNull(flowSwitch1);
		Assert.assertEquals(DEFAULT_FLOW_PRIORITY, flowSwitch1.getPriority());
		Assert.assertEquals(SWITCH_1_ID, flowSwitch1.getSwitchId());

		FloodlightOFMatch match1 = flowSwitch1.getMatch();
		Assert.assertNotNull(match1);
		Assert.assertEquals(SRC_IP, match1.getSrcIp());
		Assert.assertEquals(DST_IP, match1.getDstIp());

		Assert.assertEquals(IP_ETHER_TYPE, match1.getEtherType());
		Assert.assertEquals("3", match1.getIngressPort());
		Assert.assertEquals("1", match1.getTosBits());

		List<FloodlightOFAction> actionsSwitch1 = flowSwitch1.getActions();
		Assert.assertNotNull(actionsSwitch1);
		Assert.assertEquals(1, actionsSwitch1.size());

		Assert.assertEquals("output", actionsSwitch1.get(0).getType());
		Assert.assertEquals("1", actionsSwitch1.get(0).getValue());

		// Forwarding rule switch 2
		FloodlightOFFlow flowSwitch2 = rulesSwitch2.get(0);
		Assert.assertNotNull(flowSwitch2);
		Assert.assertEquals(DEFAULT_FLOW_PRIORITY, flowSwitch2.getPriority());
		Assert.assertEquals(SWITCH_2_ID, flowSwitch2.getSwitchId());

		FloodlightOFMatch match2 = flowSwitch2.getMatch();
		Assert.assertNotNull(match2);
		Assert.assertEquals(SRC_IP, match2.getSrcIp());
		Assert.assertEquals(DST_IP, match2.getDstIp());

		Assert.assertEquals(IP_ETHER_TYPE, match2.getEtherType());
		Assert.assertEquals("1", match2.getIngressPort());
		Assert.assertEquals("1", match2.getTosBits());

		List<FloodlightOFAction> actionsSwitch2 = flowSwitch2.getActions();
		Assert.assertNotNull(actionsSwitch2);
		Assert.assertEquals(1, actionsSwitch2.size());

		Assert.assertEquals("output", actionsSwitch2.get(0).getType());
		Assert.assertEquals("3", actionsSwitch2.get(0).getValue());

		// Forwarding rule switch 4
		FloodlightOFFlow flowSwitch4 = rulesSwitch4.get(0);
		Assert.assertNotNull(flowSwitch4);
		Assert.assertEquals(DEFAULT_FLOW_PRIORITY, flowSwitch4.getPriority());
		Assert.assertEquals(SWITCH_4_ID, flowSwitch4.getSwitchId());

		FloodlightOFMatch match4 = flowSwitch4.getMatch();
		Assert.assertNotNull(match4);
		Assert.assertEquals(SRC_IP, match4.getSrcIp());
		Assert.assertEquals(DST_IP, match4.getDstIp());

		Assert.assertEquals(IP_ETHER_TYPE, match4.getEtherType());
		Assert.assertEquals("3", match4.getIngressPort());
		Assert.assertEquals("1", match4.getTosBits());

		List<FloodlightOFAction> actionsSwitch4 = flowSwitch4.getActions();
		Assert.assertNotNull(actionsSwitch4);
		Assert.assertEquals(1, actionsSwitch4.size());

		Assert.assertEquals("output", actionsSwitch4.get(0).getType());
		Assert.assertEquals("1", actionsSwitch4.get(0).getValue());

		// Forwarding rule switch 5
		FloodlightOFFlow flowSwitch5 = rulesSwitch5.get(0);
		Assert.assertNotNull(flowSwitch5);
		Assert.assertEquals(DEFAULT_FLOW_PRIORITY, flowSwitch5.getPriority());
		Assert.assertEquals(SWITCH_4_ID, flowSwitch5.getSwitchId());

		FloodlightOFMatch match5 = flowSwitch5.getMatch();
		Assert.assertNotNull(match5);
		Assert.assertEquals(SRC_IP, match5.getSrcIp());
		Assert.assertEquals(DST_IP, match5.getDstIp());

		Assert.assertEquals(IP_ETHER_TYPE, match5.getEtherType());
		Assert.assertEquals("3", match5.getIngressPort());
		Assert.assertEquals("1", match5.getTosBits());

		List<FloodlightOFAction> actionsSwitch5 = flowSwitch5.getActions();
		Assert.assertNotNull(actionsSwitch5);
		Assert.assertEquals(1, actionsSwitch5.size());

		Assert.assertEquals("output", actionsSwitch5.get(0).getType());
		Assert.assertEquals("2", actionsSwitch5.get(0).getValue());

	}

	private QosPolicyRequest generateQosPolicyRequest() {

		QosPolicyRequest req = new QosPolicyRequest();
		Source source = new Source();
		source.setAddress(SRC_IP);
		source.setPort("0");
		req.setSource(source);

		Destination destination = new Destination();
		destination.setAddress(DST_IP);
		destination.setPort("0");
		req.setDestination(destination);

		req.setLabel("0");

		return req;
	}

	private void stopResources() throws ResourceException, InterruptedException {
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

	private void createSwitches() throws ResourceException, ProtocolException, InterruptedException {

		ofswitch1Listener = new WSEndpointListenerHandler();
		ofswitch1Listener.registerWSEndpointListener(SWITCH1_FORWARDING_CONTEXT, context);
		switchResource1 = createSwitch(SERVER_URL, SWITCH_1_ID, SWITCH_1_NAME);
		ofswitch1Listener.waitForEndpointToBePublished();

		ofswitch2Listener = new WSEndpointListenerHandler();
		ofswitch2Listener.registerWSEndpointListener(SWITCH2_FORWARDING_CONTEXT, context);
		switchResource2 = createSwitch(SERVER_URL, SWITCH_2_ID, SWITCH_2_NAME);
		ofswitch2Listener.waitForEndpointToBePublished();

		ofswitch3Listener = new WSEndpointListenerHandler();
		ofswitch3Listener.registerWSEndpointListener(SWITCH3_FORWARDING_CONTEXT, context);
		switchResource3 = createSwitch(SERVER_URL, SWITCH_3_ID, SWITCH_3_NAME);
		ofswitch3Listener.waitForEndpointToBePublished();

		ofswitch4Listener = new WSEndpointListenerHandler();
		ofswitch4Listener.registerWSEndpointListener(SWITCH4_FORWARDING_CONTEXT, context);
		switchResource4 = createSwitch(SERVER_URL, SWITCH_4_ID, SWITCH_4_NAME);
		ofswitch4Listener.waitForEndpointToBePublished();

		ofswitch5Listener = new WSEndpointListenerHandler();
		ofswitch5Listener.registerWSEndpointListener(SWITCH5_FORWARDING_CONTEXT, context);
		switchResource5 = createSwitch(SERVER_URL, SWITCH_5_ID, SWITCH_5_NAME);
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

	private IResource createSwitch(String serverURL, String switchId, String resourceName) throws ResourceException,
			ProtocolException, InterruptedException {

		IResource resource;

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION,
				OpenflowForwardingCapability.CAPABILITY_TYPE,
				MOCK_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFSWITCH_RESOURCE_TYPE,
				MOCK_URI, resourceName);

		resource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, switchId);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, resource.getResourceIdentifier().getId(), serverURL,
				FLOODLIGHT_PROTOCOL, sessionParameters);

		// Start resource

		resourceManager.startResource(resource.getResourceIdentifier());

		return resource;

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

	private String readSampleFile(String url) throws IOException {
		String fileString = "";
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(url)));
		String line;
		while ((line = br.readLine()) != null) {
			fileString += line += "\n";
		}
		br.close();
		return fileString;
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
