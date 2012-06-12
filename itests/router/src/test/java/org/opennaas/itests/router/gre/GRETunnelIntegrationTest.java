/**
 *
 */
package org.opennaas.itests.router.gre;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
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
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.itests.router.mock.MockBootstrapper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * @author Jordi
 */
@RunWith(JUnit4TestRunner.class)
public abstract class GRETunnelIntegrationTest
{
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	protected static final String		TUNNEL_NAME		= "gr-1/0/3.1";
	protected static final String		IPV4_ADDRESS	= "192.168.32.1";
	protected static final String		SUBNET_MASK		= "255.255.255.0";
	protected static final String		IP_SOURCE		= "147.56.89.62";
	protected static final String		IP_DESTINY		= "193.45.23.1";
	private final Log					log				= LogFactory
																.getLog(GRETunnelIntegrationTest.class);
	private static MockResource			mockResource;
	protected IQueueManagerCapability	queueCapability;
	protected IGRETunnelCapability		greTunnelCapability;

	@Inject
	private BundleContext				bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory			queueManagerFactory;

	@Inject
	private IProtocolManager			protocolManager;

	@Inject
	@Filter("(capability=gretunnel)")
	private ICapabilityFactory			gretunnelFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer			routerRepositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer			queueService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.gretunnel)")
	private BlueprintContainer			gretunnelService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initBundles()
			throws CapabilityException, IncorrectLifecycleStateException,
			ResourceException, CorruptStateException, ProtocolException
	{
		initResource();
		initCapability();
	}

	/**
	 * Initialize the router resource
	 */
	public void initResource() {
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		mockResource.setBootstrapper(new MockBootstrapper());
		((ComputerSystem) mockResource.getModel())
				.addHostedService(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("gretunnel");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		IResourceIdentifier resourceIdentifier = new ResourceIdentifier(resourceDescriptor.getInformation().getType(), resourceDescriptor.getId());
		mockResource.setResourceIdentifier(resourceIdentifier);
	}

	/**
	 * Initialize the Queue and GRETunnel capabilies
	 */
	public void initCapability()
			throws CapabilityException, IncorrectLifecycleStateException,
			ResourceException, CorruptStateException, ProtocolException
	{
		log.info("INFO: Before Test, getting queue...");
		queueCapability = (IQueueManagerCapability) queueManagerFactory.create(mockResource);
		((ICapabilityLifecycle) queueCapability).initialize();

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("Creating gretunnel capability");
		greTunnelCapability = (IGRETunnelCapability) gretunnelFactory.create(mockResource);
		((ICapabilityLifecycle) greTunnelCapability).initialize();

		mockResource.addCapability(queueCapability);
		mockResource.addCapability(greTunnelCapability);
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		log.debug("FFFF test setup uri: " + uri + "Length: " + uri.length());
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;
	}

	/**
	 * Get the GRETunnelService
	 * 
	 * @return GRETunnelService
	 * @throws IOException
	 */
	protected GRETunnelService getGRETunnelService(String tunnelName, String ipv4Address, String subnetMask, String ipSource, String ipDestiny) {
		GRETunnelService greService = new GRETunnelService();
		greService.setElementName("");
		greService.setName(tunnelName);

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress(ipSource);
		greConfig.setDestinationAddress(ipDestiny);

		GRETunnelEndpoint gE = new GRETunnelEndpoint();
		gE.setIPv4Address(ipv4Address);
		gE.setSubnetMask(subnetMask);

		greService.setGRETunnelConfiguration(greConfig);
		greService.addProtocolEndpoint(gE);

		return greService;
	}

	/**
	 * Add service to the model
	 */
	private void addGRETunnelService() {
		org.opennaas.extensions.router.model.ComputerSystem system = new org.opennaas.extensions.router.model.ComputerSystem();
		system.addHostedService(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
	}
}
