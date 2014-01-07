package org.opennaas.itests.ip.opener;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

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
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.TestsConstants;
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
 * Test creates a mock server for Opener, including answers for the refreshAction and the setIp methods.
 * 
 * We can not test wrong behaviors, since Opener crashes with an Internal Server Error if we send wrong parameters.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OpenerDriverTest extends MockHTTPServerTest {

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
	private final static String			RESOURCE_INFO_NAME	= "RouterWithOpenerDriver";

	@Inject
	protected IResourceManager			resourceManager;

	@Inject
	protected IProtocolManager			protocolManager;

	@Inject
	protected BundleContext				context;

	protected IResource					routerResource;
	protected WSEndpointListenerHandler	listenerHandler;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-router-driver-opener", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initTestScenario() throws Exception {

		log.info("Creating initial scenario.");

		prepareBehaviours();

		startServer(SERVLET_CONTEXT_URL);
		startResource(SERVER_URL + SERVLET_CONTEXT_URL);

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
	 * Test simulates a router with two interfaces: eth0 and eth1. When resource is started, refresh action is called. The behaviour of the server is
	 * to answer correctly to the getInterfaces call.
	 * 
	 * @throws Exception
	 */
	@Test
	public void refreshActionTest() throws Exception {

		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();
		Assert.assertNotNull("Router should contain a model.", routerModel);

		List<LogicalDevice> logicalDevices = routerModel.getLogicalDevices();
		Assert.assertNotNull("Router model should contain logical devices.", logicalDevices);
		Assert.assertEquals("Router model should contain two network ports.", 2, logicalDevices.size());
		Assert.assertEquals("First network port should be eth0", IFACE_ETH0, logicalDevices.get(0).getName());
		Assert.assertEquals("Second network port should be eth1", IFACE_ETH1, logicalDevices.get(1).getName());

	}

	/**
	 * Test checks that, if we call the setIp method of the IP capability, and there's no error, the new ip is set in model as a IPProtocolEndpoint of
	 * the networkport it belongs to. Be aware that the queue is executed twice (on startup and on manual execution)
	 * 
	 * @throws Exception
	 */
	@Test
	public void setIPActionTest() throws Exception {

		IIPCapability ipCapab = (IIPCapability) getCapability(IIPCapability.class);
		IQueueManagerCapability queue = (IQueueManagerCapability) getCapability(IQueueManagerCapability.class);

		ComputerSystem routerModel = (ComputerSystem) routerResource.getModel();

		ipCapab.setIP(routerModel.getLogicalDevices().get(1), SAMPLE_IP_WITH_MASK);
		queue.execute();

		Assert.assertNotNull("Router should contain a model.", routerModel);

		List<LogicalDevice> logicalDevices = routerModel.getLogicalDevices();
		Assert.assertNotNull("Router should contain network ports.", logicalDevices);
		Assert.assertEquals("Router model should contain two network ports.", 2, logicalDevices.size());
		Assert.assertEquals("First network port should be eth0", IFACE_ETH0, logicalDevices.get(0).getName());
		Assert.assertEquals("Second network port should be eth1", IFACE_ETH1, logicalDevices.get(1).getName());

		Assert.assertTrue("Eth1 should be parsed as a network port.", logicalDevices.get(1) instanceof NetworkPort);

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

	@Override
	protected void prepareBehaviours() throws JAXBException {

		desiredBehaviours = new ArrayList<HTTPServerBehaviour>();

		List<String> ifaces = new ArrayList<String>();
		ifaces.add(IFACE_ETH0);
		ifaces.add(IFACE_ETH1);
		String respGetIfacesBody = OpenerTestHelper.sampleGetInterfacesResponse(ifaces);

		HTTPRequest reqGetIfaces = new HTTPRequest(GET_INTERFACES_URL, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetIfaces = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, respGetIfacesBody, "");
		HTTPServerBehaviour behaviorGetIfaces = new HTTPServerBehaviour(reqGetIfaces, respGetIfaces, false);
		desiredBehaviours.add(behaviorGetIfaces);

		HTTPRequest reqGetEth0 = new HTTPRequest(GET_INTERFACE_URL + "/" + IFACE_ETH0, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetEth0 = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, OpenerTestHelper.sampleGetInterfaceResponse(IFACE_ETH0,
				null), "");
		HTTPServerBehaviour behaviorGetEth0 = new HTTPServerBehaviour(reqGetEth0, respGetEth0, false);
		desiredBehaviours.add(behaviorGetEth0);

		// consumible behaviours for refresh action should be added "(#capabilies + 1) * #queuesExecutions" times.
		HTTPRequest reqGetEth1 = new HTTPRequest(GET_INTERFACE_URL + "/" + IFACE_ETH1, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetEth1 = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, OpenerTestHelper.sampleGetInterfaceResponse(IFACE_ETH1,
				null), "");
		HTTPServerBehaviour behaviorGetEth1 = new HTTPServerBehaviour(reqGetEth1, respGetEth1, true);
		desiredBehaviours.add(behaviorGetEth1);
		desiredBehaviours.add(behaviorGetEth1);
		desiredBehaviours.add(behaviorGetEth1);

		HTTPRequest reqSetIp = new HTTPRequest(SET_IP_URL, HttpMethod.PUT, XML_TYPE, OpenerTestHelper.sampleSetInterfaceRequest(IFACE_ETH1,
				SAMPLE_IP_WITH_MASK));
		HTTPResponse respSetIp = new HTTPResponse(HttpStatus.CREATED_201, XML_TYPE, OpenerTestHelper.sampleSetInterfaceResponse(String
				.valueOf(HttpStatus.CREATED_201)), "");
		HTTPServerBehaviour behaviorSetIp = new HTTPServerBehaviour(reqSetIp, respSetIp, false);
		desiredBehaviours.add(behaviorSetIp);

		String getIfaceEth1Body = OpenerTestHelper.sampleGetInterfaceResponse(IFACE_ETH1, SAMPLE_IP_WITH_MASK);
		HTTPRequest reqGetEth1Ip = new HTTPRequest(GET_INTERFACE_URL + "/" + IFACE_ETH1, HttpMethod.GET, XML_TYPE, "");
		HTTPResponse respGetEth1Ip = new HTTPResponse(HttpStatus.OK_200, MediaType.TEXT_XML, getIfaceEth1Body, "");
		HTTPServerBehaviour behaviorGetEth1Ip = new HTTPServerBehaviour(reqGetEth1Ip, respGetEth1Ip, true);
		desiredBehaviours.add(behaviorGetEth1Ip);
		desiredBehaviours.add(behaviorGetEth1Ip);
		desiredBehaviours.add(behaviorGetEth1Ip);

	}

	private void startResource(String serverURL) throws ResourceException, ProtocolException, InterruptedException {

		log.info("Creating router resource with Opener-0.01 driver.");

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor chassisCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.OPENER_ACTIONSET_NAME,
				TestsConstants.OPENER_ACTIONSET_VERSION,
				TestsConstants.CHASSIS_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(chassisCapabilityDescriptor);

		CapabilityDescriptor ipCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(TestsConstants.OPENER_ACTIONSET_NAME,
				TestsConstants.OPENER_ACTIONSET_VERSION,
				TestsConstants.IP_CAPABILITY_TYPE,
				TestsConstants.CAPABILITY_URI);
		lCapabilityDescriptors.add(ipCapabilityDescriptor);

		// Add Queue Capability Descriptor
		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newQueueCapabilityDescriptor(TestsConstants.OPENER_ACTIONSET_NAME,
				TestsConstants.OPENER_ACTIONSET_VERSION);
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, TestsConstants.RESOURCE_TYPE,
				TestsConstants.RESOURCE_URI,
				RESOURCE_INFO_NAME);

		routerResource = resourceManager.createResource(resourceDescriptor);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, routerResource.getResourceIdentifier().getId(), serverURL,
				TestsConstants.OPENER_PROTOCOL, "noauth");

		// Start resource

		listenerHandler = new WSEndpointListenerHandler();
		listenerHandler.registerWSEndpointListener(context, IChassisCapability.class);
		resourceManager.startResource(routerResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBePublished();

		log.info("Router resource with Opener-0.01 driver successfully created.");

	}

	private void stopResource() throws ResourceException, InterruptedException {
		resourceManager.stopResource(routerResource.getResourceIdentifier());
		listenerHandler.waitForEndpointToBeUnpublished();
		resourceManager.removeResource(routerResource.getResourceIdentifier());

	}

	private ICapability getCapability(Class<? extends ICapability> clazz) throws ResourceException {

		log.debug("Getting capability " + clazz.getName());

		ICapability capab = routerResource.getCapabilityByInterface(clazz);
		Assert.assertNotNull(capab);
		return capab;
	}

}
