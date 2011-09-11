package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.IntegrationTestsHelper;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.osgi.framework.BundleContext;
import org.osgi.service.command.CommandProcessor;

public class ConfigureLRTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(ConfigureLRTest.class);

	String						resourceFriendlyID;
	String						LRFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private IResourceManager	resourceManager;

	private Boolean				isMock;

	@Inject
	BundleContext				bundleContext	= null;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
								// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
								// ////////import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

								);

		return options;
	}

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		// IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		// initTest();

	}

	public void logicalRouterConfigureInterfaceEthernetTest() {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0

		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// restore configuration
	}

	public void logicalRouterConfigureInterfaceLogicalTunnelTest() {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// restore configuration

	}

	public void logicalRouterConfigureInterfaceLoopbackTest() {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// restore configuration

	}

	public void addAndRemoveInterfaceLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// check interface is not included in the L1
		// restore configuration
	}

	public void failAddCreateLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// resource:start L1
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// test fail, cannot add new interfaces when the L1 resource is started
		// restore configuration

	}

}
