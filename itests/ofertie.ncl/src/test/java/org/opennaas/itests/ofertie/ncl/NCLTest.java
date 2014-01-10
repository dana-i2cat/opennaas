package org.opennaas.itests.ofertie.ncl;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
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
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.INCLProvisioner;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.FlowAllocationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.FlowRequest;
import org.opennaas.extensions.ofnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
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
public class NCLTest extends MockHTTPServerTest {

	// //// SWITCHES //// //
	private static final String			SWITCH_1_ID						= "00:00:00:00:00:00:00:01";
	private static final String			SWITCH_2_ID						= "00:00:00:00:00:00:00:02";
	private static final String			SWITCH_3_ID						= "00:00:00:00:00:00:00:03";
	private static final String			SWITCH_4_ID						= "00:00:00:00:00:00:00:04";
	private static final String			SWITCH_5_ID						= "00:00:00:00:00:00:00:05";
	private final static String			SWITCH_1_NAME					= "s1";
	private final static String			SWITCH_2_NAME					= "s2";
	private final static String			SWITCH_3_NAME					= "s3";
	private final static String			SWITCH_4_NAME					= "s4";
	private final static String			SWITCH_5_NAME					= "s5";
	private IResource					switchResource1;
	private IResource					switchResource2;
	private IResource					switchResource3;
	private IResource					switchResource4;
	private IResource					switchResource5;
	private static final String			SWITCH1_FORWARDING_CONTEXT		= "/opennaas/openflowswitch/s1/offorwarding";
	private static final String			SWITCH2_FORWARDING_CONTEXT		= "/opennaas/openflowswitch/s2/offorwarding";
	private static final String			SWITCH3_FORWARDING_CONTEXT		= "/opennaas/openflowswitch/s3/offorwarding";
	private static final String			SWITCH4_FORWARDING_CONTEXT		= "/opennaas/openflowswitch/s4/offorwarding";
	private static final String			SWITCH5_FORWARDING_CONTEXT		= "/opennaas/openflowswitch/s5/offorwarding";
	private static final String			ACTIONSET_NAME					= "floodlight";
	private static final String			ACTIONSET_VERSION				= "0.90";
	private static final String			OFSWITCH_RESOURCE_TYPE			= "openflowswitch";
	private static final String			SWITCH_ID_NAME					= FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME;
	private static final String			FLOODLIGHT_PROTOCOL				= FloodlightProtocolSession.FLOODLIGHT_PROTOCOL_TYPE;

	// //// SDN NETWORK //// //
	private IResource					sdnNetResource;
	private static final String			SDN_ACTIONSET_NAME				= "internal";
	private static final String			SDN_ACTIONSET_VERSION			= "1.0.0";
	private static final String			SDN_RESOURCE_NAME				= "sdnNetwork";
	private static final String			OFNETWORK_RESOURCE_TYPE			= "ofnetwork";
	private static final String			OFNET_PROVISION_CONTEXT			= "/opennaas/ofnetwork/sdnNetwork/ofprovisionnet";

	// //// SERVER INFO //// //
	private final static String			SERVER_URL						= "http://localhost:8080";
	private final static String			SERVLET_CONTEXT_URL				= "/wm/staticflowentrypusher";
	private final static String			FLOODLIGHT_ADD_FLOW_URL			= SERVLET_CONTEXT_URL + "/json";
	private final static String			GET_FLOWS_URL_SWITCH1			= SERVLET_CONTEXT_URL + "/list/" + SWITCH_1_ID + "/json";
	private final static String			GET_FLOWS_URL_SWITCH2			= SERVLET_CONTEXT_URL + "/list/" + SWITCH_2_ID + "/json";
	private final static String			GET_FLOWS_URL_SWITCH4			= SERVLET_CONTEXT_URL + "/list/" + SWITCH_4_ID + "/json";
	private final static String			GET_FLOWS_URL_SWITCH5			= SERVLET_CONTEXT_URL + "/list/" + SWITCH_5_ID + "/json";

	// /// ENDPOINT LISTENERS //// //
	private WSEndpointListenerHandler	sdnListener;
	private WSEndpointListenerHandler	ofswitch1Listener;
	private WSEndpointListenerHandler	ofswitch2Listener;
	private WSEndpointListenerHandler	ofswitch3Listener;
	private WSEndpointListenerHandler	ofswitch4Listener;
	private WSEndpointListenerHandler	ofswitch5Listener;

	// //// LOG //// //
	private final static Log			log								= LogFactory.getLog(NCLTest.class);

	// /// OTHERS //// //
	private static final String			MOCK_URI						= "mock://user:pass@host.net:2212/mocksubsystem";
	private final static String			FLOODLIGHT_ADD_FLOW_RESPONSE	= "Entry pushed";

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
				includeFeatures("opennaas-openflowswitch", "opennaas-ofnetwork", "opennaas-openflowswitch-driver-floodlight",
						"opennaas-ofertie-ncl", "itests-helpers"),
				noConsole(),
				OpennaasExamOptions.openDebugSocket(),
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

		FlowRequest req = generateFlowRequest();
		provisioner.allocateFlow(req);
	}

	private FlowRequest generateFlowRequest() {

		FlowRequest req = new FlowRequest();
		req.setSourceIPAddress("192.168.10.10");
		req.setDestinationIPAddress("192.168.10.11");
		req.setTos(0);

		return req;
	}

	private void stopResources() throws ResourceException, InterruptedException {
		resourceManager.destroyAllResources();
		sdnListener.waitForEndpointToBeUnpublished();
		ofswitch1Listener.waitForEndpointToBeUnpublished();
		ofswitch2Listener.waitForEndpointToBeUnpublished();
		ofswitch3Listener.waitForEndpointToBeUnpublished();
		ofswitch4Listener.waitForEndpointToBeUnpublished();
		ofswitch5Listener.waitForEndpointToBeUnpublished();

	}

	private void createSwitches() throws ResourceException, ProtocolException, InterruptedException {

		ofswitch1Listener = new WSEndpointListenerHandler();
		ofswitch1Listener.registerWSEndpointListener(SWITCH1_FORWARDING_CONTEXT, context);
		createSwitch(SERVER_URL, SWITCH_1_ID, SWITCH_1_NAME, switchResource1);
		ofswitch1Listener.waitForEndpointToBePublished();

		ofswitch2Listener = new WSEndpointListenerHandler();
		ofswitch2Listener.registerWSEndpointListener(SWITCH2_FORWARDING_CONTEXT, context);
		createSwitch(SERVER_URL, SWITCH_2_ID, SWITCH_2_NAME, switchResource2);
		ofswitch2Listener.waitForEndpointToBePublished();

		ofswitch3Listener = new WSEndpointListenerHandler();
		ofswitch3Listener.registerWSEndpointListener(SWITCH3_FORWARDING_CONTEXT, context);
		createSwitch(SERVER_URL, SWITCH_3_ID, SWITCH_3_NAME, switchResource3);
		ofswitch3Listener.waitForEndpointToBePublished();

		ofswitch4Listener = new WSEndpointListenerHandler();
		ofswitch4Listener.registerWSEndpointListener(SWITCH4_FORWARDING_CONTEXT, context);
		createSwitch(SERVER_URL, SWITCH_4_ID, SWITCH_4_NAME, switchResource4);
		ofswitch4Listener.waitForEndpointToBePublished();

		ofswitch5Listener = new WSEndpointListenerHandler();
		ofswitch5Listener.registerWSEndpointListener(SWITCH5_FORWARDING_CONTEXT, context);
		createSwitch(SERVER_URL, SWITCH_5_ID, SWITCH_5_NAME, switchResource5);
		ofswitch5Listener.waitForEndpointToBePublished();
	}

	private void createSDNNetwork() throws ResourceException, InterruptedException {
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor provisionCapab = ResourceHelper.newCapabilityDescriptor(SDN_ACTIONSET_NAME,
				SDN_ACTIONSET_VERSION, OFProvisioningNetworkCapability.CAPABILITY_TYPE, MOCK_URI);

		lCapabilityDescriptors.add(provisionCapab);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, OFNETWORK_RESOURCE_TYPE,
				MOCK_URI, SDN_RESOURCE_NAME);

		sdnNetResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		sdnListener = new WSEndpointListenerHandler();
		sdnListener.registerWSEndpointListener(OFNET_PROVISION_CONTEXT, context);
		resourceManager.startResource(sdnNetResource.getResourceIdentifier());
		sdnListener.waitForEndpointToBePublished();

	}

	private void createSwitch(String serverURL, String switchId, String resourceName, IResource resource) throws ResourceException,
			ProtocolException, InterruptedException {

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

}
