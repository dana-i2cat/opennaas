package org.opennaas.itests.openflowswitch;

/*
 * #%L
 * OpenNaaS :: iTests :: Openflow Switch
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup.FloodlightMockClientFactory;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

//import static org.opennaas.itests.helpers.OpennaasExamOptions.openDebugSocket;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OpenflowForwardingCapabilityIntegrationTest {

	private final static Log	log					= LogFactory.getLog(OpenflowForwardingCapabilityIntegrationTest.class);

	private static final String	SWITCH_ID			= "00:00:00:00:00:00:00:01";

	private static final String	ACTIONSET_NAME		= "floodlight";
	private static final String	ACTIONSET_VERSION	= "0.90";
	private static final String	CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String	RESOURCE_TYPE		= "openflowswitch";
	private static final String	RESOURCE_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String	RESOURCE_INFO_NAME	= "ofswitch";

	private static final String	PROTOCOL			= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;
	private static final String	SWITCH_ID_NAME		= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private static final String	WS_URI				= "http://localhost:8888/opennaas/" + RESOURCE_TYPE + "/" + RESOURCE_INFO_NAME + "/" + OpenflowForwardingCapability.CAPABILITY_TYPE;
	private static final String	WS_USERNAME			= "admin";
	private static final String	WS_PASSWORD			= "123456";

	private IResource			ofSwitchResource;

	/**
	 * Make sure blueprint for specified bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch)", timeout = 20000)
	private BlueprintContainer	switchBlueprintContainer;
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)", timeout = 20000)
	private BlueprintContainer	floodlightDriverBundleContainer;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-openflowswitch", "opennaas-openflowswitch-driver-floodlight", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void initBundle() throws ResourceException, ProtocolException {
		InitializerTestHelper.removeResources(resourceManager);
		// sleep to get time for dosgi to unregister the WS endpoints
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			// ignored
		}
		startResource();
		// sleep to get time for dosgi to register the WS endpoints
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			// ignored
		}
		log.info("INFO: Initialized!");
	}

	@After
	public void stopBundle() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		ofSwitchResource = null;
		// sleep to get time for dosgi to unregister the WS endpoints
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			// ignored
		}
		log.info("INFO: Stopped!");
	}

	@Test
	public void isCapabilityAccesibleFromResource() throws ResourceException {

		Assert.assertEquals("Resource should have only one capability.", 1, ofSwitchResource.getCapabilities().size());

		Assert.assertNotNull("Resource should contain OpenflowForwardingCapability capability.",
				ofSwitchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class));

	}

	/**
	 * This test checks that, when a flow is created, the model is populated if there's no error in capability method execution.
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	@Test
	public void createOpenflowForwardingRuleTest() throws ResourceException, ProtocolException {

		IModel model = ofSwitchResource.getModel();
		Assert.assertNotNull("Openflowswitch model should not be null after starting.", model);
		Assert.assertTrue("Model is OpenflowSwitchModel", model instanceof OpenflowSwitchModel);

		OpenflowSwitchModel switchModel = (OpenflowSwitchModel) model;
		Assert.assertNotNull("Openflowswitch model should contain forwardingRule tables.", switchModel.getOfTables());
		Assert.assertEquals("Openflowswitch model should contain one forwardingRule table after starting.", 1, switchModel.getOfTables().size());

		OFFlowTable ofTable = switchModel.getOfTables().get(0);

		Assert.assertNotNull("Openflow rules should not be null in forwardingRules table.", ofTable.getOfForwardingRules());
		Assert.assertTrue("Openflow table should not contain any forwarding rule.", ofTable.getOfForwardingRules().isEmpty());

		IOpenflowForwardingCapability capability = (IOpenflowForwardingCapability) ofSwitchResource
				.getCapabilityByInterface(IOpenflowForwardingCapability.class);

		FloodlightOFFlow forwardingRule = generateSampleFloodlightOFFlow("flow1", "1", "dstPort=12");

		capability.createOpenflowForwardingRule(forwardingRule);

		Assert.assertNotNull("Openflowswitch model should contain forwardingRule tables.", switchModel.getOfTables());
		Assert.assertEquals("Openflowswitch model should contain one forwardingRule table after action execution.", 1, switchModel.getOfTables()
				.size());

		Assert.assertEquals("Incorrect switchId in model", SWITCH_ID, switchModel.getSwitchId());

		OFFlowTable ofNewTable = switchModel.getOfTables().get(0);

		Assert.assertNotNull("Openflow rules should not be null in forwardingRules table.", ofNewTable.getOfForwardingRules());
		Assert.assertEquals("Openflow table should contain one forwarding rule.", 1, ofNewTable.getOfForwardingRules().size());
		Assert.assertEquals("Model forwarding rule should be the one created for the action.", forwardingRule,
				ofNewTable.getOfForwardingRules().get(0));

		FloodlightOFFlow newForwardingRule = generateSampleFloodlightOFFlow("flow1", "1", "dstPort=12");

		capability.createOpenflowForwardingRule(newForwardingRule);

		Assert.assertNotNull("Openflowswitch model should contain forwardingRule tables.", switchModel.getOfTables());
		Assert.assertEquals("Openflowswitch model should contain one forwardingRule table after action execution.", 1, switchModel.getOfTables()
				.size());

		Assert.assertEquals("Incorrect switchId in model", SWITCH_ID, switchModel.getSwitchId());

		OFFlowTable ofModelTable = switchModel.getOfTables().get(0);

		Assert.assertNotNull("Openflow rules should not be null in forwardingRules table.", ofModelTable.getOfForwardingRules());
		Assert.assertEquals("Openflow table should contain two forwarding rules.", 2, ofModelTable.getOfForwardingRules().size());
		Assert.assertEquals("Model forwarding rule should be the one created for the action.", forwardingRule,
				ofModelTable.getOfForwardingRules().get(0));
		Assert.assertEquals("Model forwarding rule should be the one created for the action.", newForwardingRule,
				ofModelTable.getOfForwardingRules().get(1));
	}

	/**
	 * Test create a forwarding rule with a wrong actionId, since forwardingCapability supports only "output" actions.
	 * 
	 * @throws ResourceException
	 */
	@Test(expected = CapabilityException.class)
	public void wrongActionTest() throws ResourceException {

		IOpenflowForwardingCapability capability = (IOpenflowForwardingCapability) ofSwitchResource
				.getCapabilityByInterface(IOpenflowForwardingCapability.class);

		FloodlightOFFlow forwardingRule = new FloodlightOFFlow();
		forwardingRule.setName("flow1");
		forwardingRule.setPriority("1");

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("wrongActionName");
		floodlightAction.setValue("dstPort=12");

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		forwardingRule.setActions(actions);

		capability.createOpenflowForwardingRule(forwardingRule);

	}

	@Test
	public void createDeleteGetTest() throws Exception {

		IOpenflowForwardingCapability capability = (IOpenflowForwardingCapability) ofSwitchResource
				.getCapabilityByInterface(IOpenflowForwardingCapability.class);

		FloodlightOFFlow forwardingRule1 = generateSampleFloodlightOFFlow("flow1", "1", "dstPort=12");
		forwardingRule1.setSwitchId(SWITCH_ID);

		FloodlightOFFlow forwardingRule2 = generateSampleFloodlightOFFlow("flow2", "2", "dstPort=12");
		forwardingRule2.setSwitchId(SWITCH_ID);

		Assert.assertTrue("No rules in a freshly created switch", capability.getOpenflowForwardingRules().isEmpty());

		capability.createOpenflowForwardingRule(forwardingRule1);
		Assert.assertEquals("There is one single rule after creating one", 1, capability.getOpenflowForwardingRules().size());
		Assert.assertEquals("forwardingRule1 has been correctly created", forwardingRule1, capability
				.getOpenflowForwardingRules().get(0));

		capability.createOpenflowForwardingRule(forwardingRule2);
		Assert.assertEquals("There are two rules after creating two", 2, capability.getOpenflowForwardingRules().size());
		Assert.assertEquals("forwardingRule2 has been correctly created", forwardingRule2, capability
				.getOpenflowForwardingRules().get(1));

		capability.removeOpenflowForwardingRule(forwardingRule1.getName());
		Assert.assertEquals("There is one single rule after deleting forwardingRule1", 1, capability.getOpenflowForwardingRules().size());
		Assert.assertEquals("forwardingRule2 is still in the switch after deleting forwardingRule1", forwardingRule2, capability
				.getOpenflowForwardingRules().get(0));

		capability.removeOpenflowForwardingRule(forwardingRule2.getName());
		Assert.assertTrue("There is no rule after 2 deletions", capability.getOpenflowForwardingRules().isEmpty());
	}

	@Test
	public void createDeleteGetWSTest() throws Exception {

		IOpenflowForwardingCapability capabilityWSClient = InitializerTestHelper.createRestClient(WS_URI, IOpenflowForwardingCapability.class, null,
				WS_USERNAME, WS_PASSWORD);

		FloodlightOFFlow forwardingRule1 = generateSampleFloodlightOFFlow("flow1", "1", "dstPort=12");
		forwardingRule1.setSwitchId(SWITCH_ID);

		FloodlightOFFlow forwardingRule2 = generateSampleFloodlightOFFlow("flow2", "2", "dstPort=12");
		forwardingRule2.setSwitchId(SWITCH_ID);

		Assert.assertTrue("No rules in a freshly created switch", capabilityWSClient.getOpenflowForwardingRulesAPI().getForwardingRules().isEmpty());

		capabilityWSClient.createOpenflowForwardingRule(forwardingRule1);
		Assert.assertEquals("There is one single rule after creating one", 1, capabilityWSClient.getOpenflowForwardingRulesAPI().getForwardingRules()
				.size());
		Assert.assertEquals("forwardingRule1 has been correctly created", forwardingRule1, capabilityWSClient
				.getOpenflowForwardingRulesAPI().getForwardingRules().get(0));

		capabilityWSClient.createOpenflowForwardingRule(forwardingRule2);
		Assert.assertEquals("There are two rules after creating two", 2, capabilityWSClient.getOpenflowForwardingRulesAPI().getForwardingRules()
				.size());
		Assert.assertEquals("forwardingRule2 has been correctly created", forwardingRule2, capabilityWSClient
				.getOpenflowForwardingRulesAPI().getForwardingRules().get(1));

		capabilityWSClient.removeOpenflowForwardingRule(forwardingRule1.getName());
		Assert.assertEquals("There is one single rule after deleting forwardingRule1", 1, capabilityWSClient.getOpenflowForwardingRulesAPI()
				.getForwardingRules().size());
		Assert.assertEquals("forwardingRule2 is still in the switch after deleting forwardingRule1", forwardingRule2, capabilityWSClient
				.getOpenflowForwardingRulesAPI().getForwardingRules().get(0));

		capabilityWSClient.removeOpenflowForwardingRule(forwardingRule2.getName());
		Assert.assertTrue("There is no rule after 2 deletions", capabilityWSClient.getOpenflowForwardingRulesAPI().getForwardingRules().isEmpty());
	}

	private void startResource() throws ResourceException, ProtocolException {
		/* initialize model */
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION,
				OpenflowForwardingCapability.CAPABILITY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				RESOURCE_URI,
				RESOURCE_INFO_NAME);

		ofSwitchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, SWITCH_ID);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, ofSwitchResource.getResourceIdentifier().getId(), RESOURCE_URI,
				PROTOCOL, sessionParameters);

		// Start resource
		resourceManager.startResource(ofSwitchResource.getResourceIdentifier());

		prepareClient();

	}

	private void prepareClient() throws ProtocolException {

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(ofSwitchResource.getResourceIdentifier().getId());

		FloodlightProtocolSession session = (FloodlightProtocolSession) sessionManager.obtainSessionByProtocol(
				FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE, false);

		session.setClientFactory(new FloodlightMockClientFactory());
		IFloodlightStaticFlowPusherClient client = session.getClientFactory().createClient(session.getSessionContext());
		session.setFloodlightClient(client);

	}

	private static FloodlightOFFlow generateSampleFloodlightOFFlow(String name, String inputPort, String outputPort) {

		FloodlightOFFlow forwardingRule = new FloodlightOFFlow();
		forwardingRule.setName(name);
		forwardingRule.setPriority("1");

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(inputPort);
		forwardingRule.setMatch(match);

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("output");
		floodlightAction.setValue(outputPort);

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		forwardingRule.setActions(actions);

		return forwardingRule;
	}

}