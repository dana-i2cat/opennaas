package org.opennaas.itests.router.ip;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.itests.router.mock.MockBootstrapper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class IPCapabilityIntegrationTest
{
	private final static Log		log			= LogFactory.getLog(IPCapabilityIntegrationTest.class);
	private final String			deviceID	= "junos";
	private final String			queueID		= "queue";

	private MockResource			mockResource;
	private IIPCapability			ipCapability;
	private IQueueManagerCapability	queueCapability;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=ipv4)")
	private ICapabilityFactory		ipFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer		routerService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router"),
				noConsole(),
				keepRuntimeFolder());
	}

	public void initResource() {
		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		mockResource.setBootstrapper(new MockBootstrapper());

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);
		mockResource.setResourceIdentifier(new ResourceIdentifier(resourceDescriptor.getInformation().getType(), resourceDescriptor.getId()));
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

	}

	public void initCapability() throws Exception {

		log.info("INFO: Before test, getting queue...");
		Assert.assertNotNull(queueManagerFactory);

		queueCapability = (IQueueManagerCapability) queueManagerFactory.create(mockResource);
		((ICapabilityLifecycle) queueCapability).initialize();
		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		// Test elements not null
		log.info("Checking ip factory");
		Assert.assertNotNull(ipFactory);
		log.info("Checking capability descriptor");
		Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("ipv4"));
		log.info("Creating ip capability");
		ipCapability = (IIPCapability) ipFactory.create(mockResource);
		Assert.assertNotNull(ipCapability);
		((ICapabilityLifecycle) ipCapability).initialize();

		mockResource.addCapability(ipCapability);
		mockResource.addCapability(queueCapability);
	}

	@Before
	public void setup() throws Exception {
		initResource();
		initCapability();
	}

	@Test
	public void testSetIPv4() throws CapabilityException, ProtocolException {
		ipCapability.setIPv4(getLogicalPort(), getIPProtocolEndPoint());
		queueCapability.execute();
	}

	@Test
	public void testSetInterfaceDescription() throws CapabilityException, ProtocolException {
		ipCapability.setInterfaceDescription(getLogicalPort());
		queueCapability.execute();
	}

	private LogicalPort getLogicalPort() {
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName("fe-0/3/2");
		logicalPort.setDescription("Description for the setSubInterfaceDescription test");
		return logicalPort;
	}

	/**
	 * @return
	 */
	private IPProtocolEndpoint getIPProtocolEndPoint() {
		IPProtocolEndpoint ipProtocolEndpoint = new IPProtocolEndpoint();
		return ipProtocolEndpoint;
	}

}
