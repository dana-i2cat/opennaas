package org.opennaas.itests.router.mockserver;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class OpenerDriverTest {

	private final static Log			log					= LogFactory.getLog(OpenerDriverTest.class);
	private final static String			SERVLET_CONTEXT_URL	= "/wm/staticflowentrypusher";

	private HTTPServer					server;
	private List<HTTPServerBehaviour>	desiredBehaviours;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("itests-helpers"),
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
	}

	@Test
	public void testPrototype() throws Exception {

		initBehaviours();

		startServer();

		stopServer();

	}

	private void initBehaviours() throws IOException {

		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();

		HTTPRequest req = new HTTPRequest();
		req.setMethod(HttpMethod.GET);
		req.setRequestURL(SERVLET_CONTEXT_URL + "/getInterfaces");

		HTTPResponse response = new HTTPResponse();
		response.setContentType("text/html");
		response.setBodyMessage("<h1>" + "Hello OpenNaaS!</h1>");
		response.setStatus(HttpStatus.OK_200);
		HTTPServerBehaviour behaviour = new HTTPServerBehaviour(req, response);
		desiredBehaviours.add(behaviour);

	}
}
