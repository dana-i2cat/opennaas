/**
 *
 */
package org.opennaas.router.tests.capability;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.router.capability.gretunnel.IGRETunnelService;
import org.opennaas.router.tests.capability.mock.MockBootstrapper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * @author Jordi
 */
@RunWith(JUnit4TestRunner.class)
public abstract class GRETunnelIntegrationTest
{
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	protected static final String	TUNNEL_NAME		= "gre.1";
	protected static final String	IPV4_ADDRESS	= "192.168.32.1";
	protected static final String	SUBNET_MASK		= "255.255.255.0";
	protected static final String	IP_SOURCE		= "147.56.89.62";
	protected static final String	IP_DESTINY		= "193.45.23.1";
	private final Log				log				= LogFactory
															.getLog(GRETunnelIntegrationTest.class);
	private static MockResource		mockResource;
	protected ICapability			queueCapability;
	protected ICapability			greTunnelCapability;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=gretunnel)")
	private ICapabilityFactory		gretunnelFactory;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer		routerRepositoryService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer		queueService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=opennaas.extension.router.capability.gretunnel)")
    private BlueprintContainer		gretunnelService;

	@Configuration
	public static Option[] configuration() {
		return options(karafDistributionConfiguration()
					   .frameworkUrl(maven()
									 .groupId("net.i2cat.mantychore")
									 .artifactId("assembly")
									 .type("zip")
									 .classifier("bin")
									 .versionAsInProject())
					   .karafVersion("2.2.2")
					   .name("mantychore")
					   .unpackDirectory(new File("target/paxexam")),
					   editConfigurationFilePut("etc/org.apache.karaf.features.cfg",
												"featuresBoot",
												"opennaas-router"),
					   configureConsole()
					   .ignoreLocalConsole()
					   .ignoreRemoteShell(),
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
		queueCapability = queueManagerFactory.create(mockResource);
		queueCapability.initialize();

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("Creating gretunnel capability");
		greTunnelCapability = gretunnelFactory.create(mockResource);
		greTunnelCapability.initialize();

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
		net.i2cat.mantychore.model.ComputerSystem system = new net.i2cat.mantychore.model.ComputerSystem();
		system.addHostedService(getGRETunnelService(TUNNEL_NAME, IPV4_ADDRESS, SUBNET_MASK, IP_SOURCE, IP_DESTINY));
	}
}
