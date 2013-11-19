package org.opennaas.itests.openflowswitch;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.driver.floodlight.actionssets.actions.CreateOFForwardingAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;
import org.opennaas.itests.helpers.server.HTTPRequest;
import org.opennaas.itests.helpers.server.HTTPResponse;
import org.opennaas.itests.helpers.server.HTTPServer;
import org.opennaas.itests.helpers.server.HTTPServerBehaviour;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class FloodlightDriverTest extends OFSwitchResourceWithFloodlight {

	private final static Log			log								= LogFactory.getLog(FloodlightDriverTest.class);

	private final static String			SERVER_URL						= "http://localhost:8080";
	private final static String			SERVLET_CONTEXT_URL				= "/wm/staticflowentrypusher";

	private final static String			SWITCH_ID						= "00:00:00:00:00:00:00:01";
	private final static String			FLOW_ID							= "flow-mod-1";
	private final static String			FLOW_INGRESS_PORT				= "1";
	private final static String			FLOW_OUTPUT_PORT				= "2";
	private final static String			FLOW_PRIORITY					= "32767";
	private final static String			OUTPUT_ACTION					= CreateOFForwardingAction.FORWARDING_ACTION;

	private final static String			GET_FLOWS_URL					= SERVLET_CONTEXT_URL + "/list/" + SWITCH_ID + "/json";
	private final static String			ADD_FLOW_URL					= SERVLET_CONTEXT_URL + "/json";

	private final static String			FLOODLIGHT_ADD_FLOW_RESPONSE	= "Entry pushed";

	private HTTPServer					server;
	private List<HTTPServerBehaviour>	desiredBehaviours;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflowswitch", "opennaas-openflowswitch-driver-floodlight", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	public void startServer() throws Exception {

		server = new HTTPServer(8080);
		server.setBaseURL(SERVLET_CONTEXT_URL);
		server.setDesiredBehaviours(desiredBehaviours);
		server.start();

	}

	public void stopServer() throws Exception {
		server.stop();
		desiredBehaviours = null;
	}

	/**
	 * Test checks that, when creating a flow using the {@link IOpenflowForwardingCapability} with a successful result, it's set to model.
	 * 
	 * @throws Exception
	 */
	@Test
	public void createRuleTest() throws Exception {
		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();
		HTTPServerBehaviour behaviourCreateFlow = createBehaviour(HttpMethod.POST, ADD_FLOW_URL, MediaType.APPLICATION_JSON,
				readSampleFile("/addFlow.json"),
				HttpStatus.CREATED_201,
				MediaType.APPLICATION_JSON, FLOODLIGHT_ADD_FLOW_RESPONSE);
		HTTPServerBehaviour behaviourgetFlows = createBehaviour(HttpMethod.GET, GET_FLOWS_URL, MediaType.APPLICATION_JSON, "", HttpStatus.OK_200,
				MediaType.APPLICATION_JSON, readSampleFile("/getSwitchFlows.json"));

		desiredBehaviours.add(behaviourCreateFlow);
		desiredBehaviours.add(behaviourgetFlows);

		startServer();
		startResource(SERVER_URL, SWITCH_ID);

		IOpenflowForwardingCapability forwardingCapab = (IOpenflowForwardingCapability) getCapability(IOpenflowForwardingCapability.class);
		FloodlightOFFlow forwardingRule = generateSampleFloodlightOFFlow("flow-mod-1", "1", "2");
		forwardingCapab.createOpenflowForwardingRule(forwardingRule);

		OpenflowSwitchModel model = (OpenflowSwitchModel) ofSwitchResource.getModel();
		Assert.assertNotNull(model);
		Assert.assertEquals(1, model.getOfTables().size());

		OFFlowTable table = model.getOfTables().get(0);
		Assert.assertNotNull(table.getOfForwardingRules());
		Assert.assertEquals(1, table.getOfForwardingRules().size());

		FloodlightOFFlow flow = table.getOfForwardingRules().get(0);
		Assert.assertNotNull(flow);
		Assert.assertNotNull("Flow should contain a generated name.", flow.getName());
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

		stopResource();
		stopServer();

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

	private HTTPServerBehaviour createBehaviour(String reqMethod, String reqURL, String reqContentType, String reqBody, int respStatus,
			String contentType,
			String bodyMessage) {

		HTTPRequest req = new HTTPRequest();
		req.setMethod(reqMethod);
		req.setRequestURL(reqURL);
		req.setBodyMessage(reqBody);
		req.setContentType(reqContentType);

		HTTPResponse response = new HTTPResponse();
		response.setContentType(contentType);
		response.setBodyMessage(bodyMessage);
		response.setStatus(respStatus);

		return new HTTPServerBehaviour(req, response);
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
