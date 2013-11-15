package org.opennaas.itests.router.mockserver;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.opener.client.model.Interface;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfacesResponse;
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
public class OpenerDriverTest extends RouterResourceWithOpenerDriver {

	private final static Log			log					= LogFactory.getLog(OpenerDriverTest.class);

	private final static String			SERVER_URL			= "http://localhost:8080";

	private final static String			SERVLET_CONTEXT_URL	= "/wm/staticflowentrypusher";
	private final static String			GET_INTERFACES_URL	= SERVLET_CONTEXT_URL + "/getInterfaces";
	private static final String			GET_INTERFACE_URL	= SERVLET_CONTEXT_URL + "/getInterface";

	private HTTPServer					server;
	private List<HTTPServerBehaviour>	desiredBehaviours;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-router-driver-opener", "itests-helpers"),
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

	@Test
	public void refreshActionTest() throws Exception {

		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();
		HTTPServerBehaviour behaviourIfaces = createBehaviour(HttpMethod.GET, GET_INTERFACES_URL, HttpStatus.OK_200, MediaType.TEXT_XML,
				sampleXML());
		desiredBehaviours.add(behaviourIfaces);
		HTTPServerBehaviour behaviourEth0 = createBehaviour(HttpMethod.GET, GET_INTERFACE_URL + "/eth0", HttpStatus.OK_200, MediaType.TEXT_XML,
				sampleInterface("eth0"));
		desiredBehaviours.add(behaviourEth0);

		HTTPServerBehaviour behaviourEth1 = createBehaviour(HttpMethod.GET, GET_INTERFACE_URL + "/eth1", HttpStatus.OK_200, MediaType.TEXT_XML,
				sampleInterface("eth1"));
		desiredBehaviours.add(behaviourEth1);

		startServer();
		startResource(SERVER_URL + SERVLET_CONTEXT_URL);

		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();
		Assert.assertNotNull(routerModel);

		List<LogicalDevice> logicalDevices = routerModel.getLogicalDevices();
		Assert.assertNotNull(logicalDevices);
		Assert.assertEquals(2, logicalDevices.size());
		Assert.assertEquals("eth0", logicalDevices.get(0).getName());
		Assert.assertEquals("eth1", logicalDevices.get(1).getName());

		stopServer();

	}

	private String sampleInterface(String ifaceName) throws JAXBException {

		GetInterfaceResponse response = new GetInterfaceResponse();

		Interface iface = new Interface();
		iface.setName(ifaceName);

		response.setInterface(iface);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(GetInterfaceResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}

	private String sampleXML() throws JAXBException {
		GetInterfacesResponse response = new GetInterfacesResponse();

		List<String> ifaces = new ArrayList<String>();
		ifaces.add("eth0");
		ifaces.add("eth1");

		response.setInterfaces(ifaces);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(GetInterfacesResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}

	private HTTPServerBehaviour createBehaviour(String reqMethod, String reqURL, int respStatus, String contentType, String bodyMessage) {

		HTTPRequest req = new HTTPRequest();
		req.setMethod(reqMethod);
		req.setRequestURL(reqURL);

		HTTPResponse response = new HTTPResponse();
		response.setContentType(contentType);
		response.setBodyMessage(bodyMessage);
		response.setStatus(respStatus);

		return new HTTPServerBehaviour(req, response);
	}
}
