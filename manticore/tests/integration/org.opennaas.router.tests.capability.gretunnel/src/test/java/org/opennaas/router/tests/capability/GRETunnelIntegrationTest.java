/**
 * 
 */
package org.opennaas.router.tests.capability;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.router.capability.gretunnel.IGRETunnelService;
import org.opennaas.router.tests.capability.mock.MockBootstrapper;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

/**
 * @author Jordi
 */
@RunWith(JUnit4TestRunner.class)
public class GRETunnelIntegrationTest extends AbstractIntegrationTest {

	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	protected static final String	TUNNEL_NAME		= "gre.1";
	protected static final String	IPV4_ADDRESS	= "192.168.32.1";
	protected static final String	SUBNET_MASK		= "255.255.255.0";
	protected static final String	IP_SOURCE		= "147.56.89.62";
	protected static final String	IP_DESTINY		= "193.45.23.1";
	private final Log				log				= LogFactory
															.getLog(GRETunnelIntegrationTest.class);
	private static MockResource		mockResource;
	protected IGRETunnelService		iGRETunnelService;
	protected ICapability			queueCapability;
	protected ICapability			greTunnelCapability;

	@Inject
	BundleContext					bundleContext	= null;

	/**
	 * @return
	 */
	@Configuration
	public static Option[] configure() {
		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	/**
	 * 
	 */
	@Before
	public void initBundles() {
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

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
	public void initCapability() {
		try {
			log.info("INFO: Before Test, getting queue...");
			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = queueManagerFactory.create(mockResource);
			queueCapability.initialize();

			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

			ICapabilityFactory gretunnelFactory = getOsgiService(ICapabilityFactory.class, "capability=gretunnel", 10000);
			// Test elements not null
			log.info("Checking gretunnel factory");
			Assert.assertNotNull(gretunnelFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("gretunnel"));
			log.info("Creating gretunnel capability");
			greTunnelCapability = gretunnelFactory.create(mockResource);
			Assert.assertNotNull(greTunnelCapability);
			greTunnelCapability.initialize();

			mockResource.addCapability(queueCapability);
			mockResource.addCapability(greTunnelCapability);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
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
