package org.opennaas.itests.openflowswitch;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OpenflowForwardingCapabilityIntegrationTest {

	private final static Log				log					= LogFactory.getLog(OpenflowForwardingCapabilityIntegrationTest.class);

	private static final String				SWITCH_ID			= "00:00:00:00:00:00:00:01";

	private static final String				ACTIONSET_NAME		= "floodlight";
	private static final String				ACTIONSET_VERSION	= "0.90";
	private static final String				CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String				RESOURCE_TYPE		= "openflowswitch";
	private static final String				RESOURCE_URI		= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String				RESOURCE_INFO_NAME	= "ofswitch";

	private static final String				PROTOCOL			= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;
	private static final String				SWITCH_ID_NAME		= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private IOpenflowForwardingCapability	ofForwardingCapability;
	private IResource						ofSwitchResource;

	@Inject
	private IResourceManager				resourceManager;

	@Inject
	private IProtocolManager				protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch.driver.floodlight)")
	private BlueprintContainer				floodlightDriverBundleContainer;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflow-switch", "opennaas-openflow-switch-driver-floodlight"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initBundle() throws ResourceException, ProtocolException {

		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Initialized!");
		startResource();
	}

	@After
	public void stopBundle() throws ResourceException {
		InitializerTestHelper.removeResources(resourceManager);
		log.info("INFO: Stopped!");
	}

	@Test
	public void isCapabilityAccesibleFromResource() throws ResourceException {

		Assert.assertEquals("Resource should have only one capability.", 1, ofSwitchResource.getCapabilities().size());

		Assert.assertNotNull("Resource should contain OpenflowForwardingCapability capability.",
				ofSwitchResource.getCapabilityByInterface(IOpenflowForwardingCapability.class));

	}

	@Test
	public void createOpenflowForwardingRuleTest() throws ResourceException {

		IModel model = ofSwitchResource.getModel();
		Assert.assertNotNull("Openflowswitch model should not be null after starting.", model);
		Assert.assertTrue(model instanceof OpenflowSwitchModel);

		OpenflowSwitchModel switchModel = (OpenflowSwitchModel) model;
		Assert.assertNotNull("Openflowswitch model should contain forwardingRule tables.", switchModel.getOfTables());
		Assert.assertEquals("Openflowswitch model should contain one forwardingRule table after starting.", 1, switchModel.getOfTables().size());

		OFFlowTable ofTable = switchModel.getOfTables().get(0);

		Assert.assertNotNull("Openflow rules should not be null in forwardingRules table.", ofTable.getOfForwardingRules());
		Assert.assertTrue("Openflow table should not contain any forwarding rule.", ofTable.getOfForwardingRules().isEmpty());

		IOpenflowForwardingCapability capability = (IOpenflowForwardingCapability) ofSwitchResource
				.getCapabilityByInterface(IOpenflowForwardingCapability.class);

		FloodlightOFFlow forwardingRule = new FloodlightOFFlow();
		forwardingRule.setName("flow1");
		forwardingRule.setPriority("1");

		FloodlightOFAction floodlightAction = new FloodlightOFAction();
		floodlightAction.setType("output");
		floodlightAction.setValue("dstPort=12");

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
		actions.add(floodlightAction);

		forwardingRule.setActions(actions);

		capability.createOpenflowForwardingRule(forwardingRule);

	}

	public void startResource() throws ResourceException, ProtocolException {
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

	}

}