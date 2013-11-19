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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.opener.client.model.IPData;
import org.opennaas.extensions.router.opener.client.model.Interface;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.GetInterfacesResponse;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.Utils;
import org.opennaas.itests.helpers.server.HTTPRequest;
import org.opennaas.itests.helpers.server.HTTPResponse;
import org.opennaas.itests.helpers.server.HTTPServer;
import org.opennaas.itests.helpers.server.HTTPServerBehaviour;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

/**
 * 
 * Test creates a mock server for Opener, including answers for the refreshAction and the setIp methods.
 * 
 * We can not test wrong behaviors, since Opener crashes with an Internal Server Error if we send wrong parameters.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OpenerDriverTest extends RouterResourceWithOpenerDriver {

	private final static Log			log					= LogFactory.getLog(OpenerDriverTest.class);

	private final static String			XML_TYPE			= MediaType.TEXT_XML + ";charset=UTF-8";

	private final static String			SERVER_URL			= "http://localhost:8080";
	private final static String			SERVLET_CONTEXT_URL	= "/axis2/services/quagga_openapi/linux";
	private final static String			GET_INTERFACES_URL	= SERVLET_CONTEXT_URL + "/getInterfaces";
	private static final String			GET_INTERFACE_URL	= SERVLET_CONTEXT_URL + "/getInterface";
	private static final String			SET_IP_URL			= SERVLET_CONTEXT_URL + "/setInterface";

	private static final String			SAMPLE_IP			= "192.168.1.25";
	private static final String			SAMPLE_IP_MASK		= "24";
	private static final String			SAMPLE_IP_WITH_MASK	= SAMPLE_IP + "/" + SAMPLE_IP_MASK;

	private final static String			IFACE_ETH0			= "eth0";
	private final static String			IFACE_ETH1			= "eth1";

	private HTTPServer					server;
	private List<HTTPServerBehaviour>	desiredBehaviours;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-router-driver-opener", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Test simulates a router with two interfaces: eth0 and eth1. When resource is started, refresh action is called. The behaviour of the server is
	 * to answer correctly to the getInterfaces call.
	 * 
	 * @throws Exception
	 */
	@Test
	public void refreshActionTest() throws Exception {

		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();
		Assert.assertNotNull(routerModel);

		List<LogicalDevice> logicalDevices = routerModel.getLogicalDevices();
		Assert.assertNotNull(logicalDevices);
		Assert.assertEquals(2, logicalDevices.size());
		Assert.assertEquals(IFACE_ETH0, logicalDevices.get(0).getName());
		Assert.assertEquals(IFACE_ETH1, logicalDevices.get(1).getName());

	}

	@Test
	public void setIPActionTest() throws Exception {

		IIPCapability ipCapab = (IIPCapability) getCapability(IIPCapability.class);
		IQueueManagerCapability queue = (IQueueManagerCapability) getCapability(IQueueManagerCapability.class);

		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();

		ipCapab.setIP(routerModel.getLogicalDevices().get(1), SAMPLE_IP_WITH_MASK);
		queue.execute();

		Assert.assertNotNull(routerModel);

		List<LogicalDevice> logicalDevices = routerModel.getLogicalDevices();
		Assert.assertNotNull(logicalDevices);
		Assert.assertEquals(2, logicalDevices.size());
		Assert.assertEquals(IFACE_ETH0, logicalDevices.get(0).getName());
		Assert.assertEquals(IFACE_ETH1, logicalDevices.get(1).getName());

		Assert.assertTrue(logicalDevices.get(1) instanceof NetworkPort);

		NetworkPort eth1Iface = (NetworkPort) logicalDevices.get(1);
		Assert.assertEquals("Networkport name should be eth1", "eth1", eth1Iface.getName());
		Assert.assertEquals("Networkport eth1 should contain port number 0.", 0, eth1Iface.getPortNumber());

		Assert.assertNotNull("Networkport eth1.0 should contain protocol endpoint.", eth1Iface.getProtocolEndpoint());
		Assert.assertEquals("Networkport eth1.0 should contain one protocol endpoint.", 1, eth1Iface.getProtocolEndpoint().size());

		Assert.assertTrue(eth1Iface.getProtocolEndpoint().get(0) instanceof IPProtocolEndpoint);
		IPProtocolEndpoint pE = (IPProtocolEndpoint) eth1Iface.getProtocolEndpoint().get(0);
		Assert.assertEquals("Protocol Endpoint should be a IPv4 IP Protocol Endpoint ", ProtocolIFType.IPV4, pE.getProtocolIFType());
		Assert.assertNotNull("Protocol enpoint should contain a ipv4 address.", pE.getIPv4Address());
		Assert.assertNull("Protocol enpoint should not contain a ipv6 address.", pE.getIPv6Address());
		Assert.assertEquals("Protocol endpoint should contain ip 192.168.1.25", SAMPLE_IP, pE.getIPv4Address());
		Assert.assertEquals("Protocol endpoint should contain ip with mask 24", IPUtilsHelper.parseShortToLongIpv4NetMask(SAMPLE_IP_MASK),
				pE.getSubnetMask());

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

	@Before
	public void initTestScenario() throws Exception {
		prepareBehaviours();

		startServer();
		startResource(SERVER_URL + SERVLET_CONTEXT_URL);
	}

	@After
	public void shutDownTestScenario() throws Exception {
		stopResource();
		stopServer();
	}

	private void prepareBehaviours() throws JAXBException {
		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();

		HTTPRequest reqGetIfaces = new HTTPRequest(GET_INTERFACES_URL, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetIfaces = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, sampleXML(), "");
		HTTPServerBehaviour behaviorGetIfaces = new HTTPServerBehaviour(reqGetIfaces, respGetIfaces, false);
		desiredBehaviours.add(behaviorGetIfaces);

		HTTPRequest reqGetEth0 = new HTTPRequest(GET_INTERFACE_URL + "/" + IFACE_ETH0, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetEth0 = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, sampleInterface("eth0"), "");
		HTTPServerBehaviour behaviorGetEth0 = new HTTPServerBehaviour(reqGetEth0, respGetEth0, false);
		desiredBehaviours.add(behaviorGetEth0);

		// consumible behaviours for refresh action should be added "(#capabilies + 1) * #queuesExecutions" times.
		HTTPRequest reqGetEth1 = new HTTPRequest(GET_INTERFACE_URL + "/" + IFACE_ETH1, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetEth1 = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, sampleInterface("eth1"), "");
		HTTPServerBehaviour behaviorGetEth1 = new HTTPServerBehaviour(reqGetEth1, respGetEth1, true);
		desiredBehaviours.add(behaviorGetEth1);
		desiredBehaviours.add(behaviorGetEth1);
		desiredBehaviours.add(behaviorGetEth1);

		HTTPRequest reqSetIp = new HTTPRequest(SET_IP_URL, HttpMethod.PUT, XML_TYPE, sampleSetIp(IFACE_ETH1, SAMPLE_IP_WITH_MASK));
		HTTPResponse respSetIp = new HTTPResponse(HttpStatus.CREATED_201, XML_TYPE, sampleInterfaceWithIP(String.valueOf(HttpStatus.CREATED_201)), "");
		HTTPServerBehaviour behaviorSetIp = new HTTPServerBehaviour(reqSetIp, respSetIp, false);
		desiredBehaviours.add(behaviorSetIp);

		HTTPRequest reqGetEth1Ip = new HTTPRequest(GET_INTERFACE_URL + "/" + IFACE_ETH1, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetEth1Ip = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, getInterfaceWithIpResponse(IFACE_ETH1,
				SAMPLE_IP_WITH_MASK),
				"");
		HTTPServerBehaviour behaviorGetEth1Ip = new HTTPServerBehaviour(reqGetEth1Ip, respGetEth1Ip, true);
		desiredBehaviours.add(behaviorGetEth1Ip);
		desiredBehaviours.add(behaviorGetEth1Ip);
		desiredBehaviours.add(behaviorGetEth1Ip);

	}

	private String getInterfaceWithIpResponse(String ifaceName, String ip) throws JAXBException {

		GetInterfaceResponse response = new GetInterfaceResponse();

		IPData ipData = new IPData();
		ipData.setAddress(ip.split("/")[0]);
		ipData.setFamilyType(ProtocolIFType.IPV4.name());
		ipData.setPrefixLength(ip.split("/")[1]);

		Interface iface = new Interface();
		iface.setName(ifaceName);
		iface.setIp(ipData);

		response.setInterface(iface);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(GetInterfaceResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}

	private String sampleSetIp(String iface, String ip) throws JAXBException {

		String ipAdderss = ip.split("/")[0];
		String prefixLength = ip.split("/")[1];
		SetInterfaceIPRequest req = Utils.createSetInterfaceIPRequest(iface, ipAdderss, prefixLength);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(SetInterfaceIPRequest.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(req, writer);

		return writer.toString();
	}

	private String sampleInterfaceWithIP(String responseMessage) throws JAXBException {

		SetInterfaceResponse response = new SetInterfaceResponse();
		response.setError(null);
		response.setResponse(responseMessage);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(SetInterfaceResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
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
		ifaces.add(IFACE_ETH0);
		ifaces.add(IFACE_ETH1);

		response.setInterfaces(ifaces);

		StringWriter writer = new StringWriter();
		JAXBContext jaxbCon = JAXBContext.newInstance(GetInterfacesResponse.class);
		Marshaller marshaller = jaxbCon.createMarshaller();

		marshaller.marshal(response, writer);

		return writer.toString();
	}

}
