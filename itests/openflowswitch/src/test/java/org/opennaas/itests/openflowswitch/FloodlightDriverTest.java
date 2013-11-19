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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.itests.helpers.OpennaasExamOptions;
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

	private static final String			SWITCH_ID						= "00:00:00:00:00:00:00:01";

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
				OpennaasExamOptions.openDebugSocket(),
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
