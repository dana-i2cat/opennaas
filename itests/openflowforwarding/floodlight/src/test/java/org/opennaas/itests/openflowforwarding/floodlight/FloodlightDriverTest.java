package org.opennaas.itests.openflowforwarding.floodlight;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.actions.CreateOFForwardingAction;
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

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class FloodlightDriverTest extends MockHTTPServerTest {

	@Inject
	protected IResourceManager			resourceManager;

	@Inject
	protected IProtocolManager			protocolManager;

	@Inject
	protected BundleContext				context;

	private final static Log			log								= LogFactory.getLog(FloodlightDriverTest.class);

	private final static String			SERVER_URL						= "http://localhost:8080";
	private final static String			SERVLET_CONTEXT_URL				= "/wm/staticflowentrypusher";

	private final static String			SWITCH_ID						= "00:00:00:00:00:00:00:01";

	private final static String			FLOW_INGRESS_PORT				= "1";
	private final static String			FLOW_OUTPUT_PORT				= "2";
	private final static String			FLOW_PRIORITY					= "32767";
	private final static String			OUTPUT_ACTION					= CreateOFForwardingAction.FORWARDING_ACTION;

	private final static String			GET_FLOWS_URL					= SERVLET_CONTEXT_URL + "/list/" + SWITCH_ID + "/json";
	private final static String			ADD_FLOW_URL					= SERVLET_CONTEXT_URL + "/json";
	private final static String			REMOVE_FLOW_URL					= SERVLET_CONTEXT_URL + "/json";

	private final static String			FLOODLIGHT_ADD_FLOW_RESPONSE	= "Entry pushed";

	private final static String			RESOURCE_INFO_NAME				= "OpenflowSwitch";

	private static final String			ACTIONSET_NAME					= "floodlight";
	private static final String			ACTIONSET_VERSION				= "0.90";
	private static final String			MOCK_URI						= "mock://user:pass@host.net:2212/mocksubsystem";
	private static final String			RESOURCE_TYPE					= "openflowswitch";

	private static final String			PROTOCOL						= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;
	private static final String			SWITCH_ID_NAME					= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;

	private IResource					ofSwitchResource;
	private WSEndpointListenerHandler	listenerHandler;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflowswitch", "opennaas-openflowswitch-driver-floodlight", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initTestScenario() throws Exception {

		log.info("Creating initial scenario.");

		prepareBehaviours();

		startServer(SERVLET_CONTEXT_URL);
		startResource(SERVER_URL, SWITCH_ID);

		log.info("Test initialized.");
	}

	@After
	public void shutDownTestScenario() throws Exception {

		log.info("Shutting down test scenario.");

		stopResource();
		stopServer();

		log.info("Test finished.");

	}

	/**
	 * Test checks that, when creating a flow using the {@link IOpenflowForwardingCapability} with a successful result, it's set to model.
	 * 
	 * @throws Exception
	 */
	@Test
	public void createRuleTest() throws Exception {

		IOpenflowForwardingCapability forwardingCapab = (IOpenflowForwardingCapability) getCapability(IOpenflowForwardingCapability.class);
		FloodlightOFFlow forwardingRule = FloodlightTestHelper.sampleFloodlightOFFlow("flow-mod-1", "1", "1", "2");
		forwardingCapab.createOpenflowForwardingRule(forwardingRule);

		OpenflowSwitchModel model = (OpenflowSwitchModel) ofSwitchResource.getModel();
		Assert.assertNotNull(model);
		Assert.assertEquals(1, model.getOfTables().size());

		OFFlowTable table = model.getOfTables().get(0);
		Assert.assertNotNull(table.getOfForwardingRules());
		Assert.assertEquals(1, table.getOfForwardingRules().size());

		FloodlightOFFlow flow = table.getOfForwardingRules().get(0);
		Assert.assertNotNull(flow);
		Assert.assertNotNull("Flow should contain as id \"flow-mod-1\".", flow.getName());
		Assert.assertEquals("Flow priority should be " + FLOW_PRIORITY, FLOW_PRIORITY, flow.getPriority());
		Assert.assertEquals("Switch id should be " + SWITCH_ID, SWITCH_ID, flow.getSwitchId());

		Assert.assertNotNull("Flow should contain actions.", flow.getActions());
		Assert.assertEquals("Flow should contain one action.", 1, flow.getActions().size());

		FloodlightOFAction action = flow.getActions().get(0);
		Assert.assertEquals("The name of the action should be " + OUTPUT_ACTION, OUTPUT_ACTION, action.getType());
		Assert.assertEquals("Output port should be " + FLOW_OUTPUT_PORT, FLOW_OUTPUT_PORT, action.getValue());

		Assert.assertNotNull("Flow should contain match.", flow.getMatch());

		FloodlightOFMatch match = flow.getMatch();
		Assert.assertEquals("Ingress port should be " + FLOW_INGRESS_PORT, FLOW_INGRESS_PORT, match.getIngressPort());

	}

	/**
	 * Floodlight driver needs to send a body message in the DELETE method because of the Floodlight API. Our Jetty server answers a HTTP 400 Bad
	 * Request error if we include body in this method, so we can not test this functionality.
	 * 
	 * @throws ResourceException
	 */
	@Ignore
	@Test
	public void createAndDeleteRuleTest() throws ResourceException {

		IOpenflowForwardingCapability forwardingCapab = (IOpenflowForwardingCapability) getCapability(IOpenflowForwardingCapability.class);
		FloodlightOFFlow forwardingRule = FloodlightTestHelper.sampleFloodlightOFFlow("flow-mod-1", "1", "1", "2");
		forwardingCapab.createOpenflowForwardingRule(forwardingRule);

		OpenflowSwitchModel model = (OpenflowSwitchModel) ofSwitchResource.getModel();
		OFFlowTable table = model.getOfTables().get(0);

		Assert.assertEquals(1, table.getOfForwardingRules().size());
		FloodlightOFFlow flow = table.getOfForwardingRules().get(0);
		Assert.assertNotNull(flow);

		forwardingCapab.removeOpenflowForwardingRule(flow.getName());
		Assert.assertEquals(0, table.getOfForwardingRules().size());

	}

	@Override
	protected void prepareBehaviours() throws JAXBException, IOException {
		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();

		HTTPRequest reqCreateFlow = new HTTPRequest(ADD_FLOW_URL, HttpMethod.POST, MediaType.APPLICATION_JSON, readSampleFile("/addFlow.json"));
		HTTPResponse resqCreateFlow = new HTTPResponse(HttpStatus.CREATED_201, MediaType.APPLICATION_JSON, FLOODLIGHT_ADD_FLOW_RESPONSE, "");
		HTTPServerBehaviour behaviourCreateFlow = new HTTPServerBehaviour(reqCreateFlow, resqCreateFlow, false);
		desiredBehaviours.add(behaviourCreateFlow);

		HTTPRequest reqGetFlows = new HTTPRequest(GET_FLOWS_URL, HttpMethod.GET, MediaType.APPLICATION_JSON, "");
		HTTPResponse respGetFlows = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON,
				readSampleFile("/getSwitchFlowsWithOneFlow.json"), "");
		HTTPServerBehaviour behaviourgetFlows = new HTTPServerBehaviour(reqGetFlows, respGetFlows, true);
		desiredBehaviours.add(behaviourgetFlows);
		desiredBehaviours.add(behaviourgetFlows);

		HTTPRequest reqDelFlow = new HTTPRequest(REMOVE_FLOW_URL, HttpMethod.DELETE, MediaType.APPLICATION_JSON, "");
		HTTPResponse respDelFlow = new HTTPResponse(HttpStatus.OK_200, MediaType.APPLICATION_JSON,
				"", "");
		HTTPServerBehaviour behaviourDelFlows = new HTTPServerBehaviour(reqDelFlow, respDelFlow, true);
		desiredBehaviours.add(behaviourDelFlows);

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

	private void startResource(String serverURL, String switchId) throws ResourceException, ProtocolException, InterruptedException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor ofForwardingDescriptor = ResourceHelper.newCapabilityDescriptor(ACTIONSET_NAME,
				ACTIONSET_VERSION,
				OpenflowForwardingCapability.CAPABILITY_TYPE,
				MOCK_URI);
		lCapabilityDescriptors.add(ofForwardingDescriptor);

		// OFSwitch Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				MOCK_URI, RESOURCE_INFO_NAME);

		ofSwitchResource = resourceManager.createResource(resourceDescriptor);

		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put(SWITCH_ID_NAME, switchId);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContextWithSessionParams(protocolManager, ofSwitchResource.getResourceIdentifier().getId(), serverURL,
				PROTOCOL, sessionParameters);

		// Start resource
		listenerHandler = new WSEndpointListenerHandler();
		listenerHandler.registerWSEndpointListener(context, IOpenflowForwardingCapability.class);
		resourceManager.startResource(ofSwitchResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBePublished();

	}

	private void stopResource() throws ResourceException, InterruptedException {
		resourceManager.stopResource(ofSwitchResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBeUnpublished();
		resourceManager.removeResource(ofSwitchResource.getResourceIdentifier());
	}

	private ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {
		ICapability capab = ofSwitchResource.getCapabilityByInterface(clazz);
		Assert.assertNotNull(capab);
		return capab;
	}

}
