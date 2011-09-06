package automaticrefresh;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.IntegrationTestsHelper;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.capability.AbstractCapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class AutomaticRefreshLuminisTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log					log				= LogFactory
														.getLog(AutomaticRefreshLuminisTest.class);
	@Inject
	BundleContext				bundleContext	= null;

	private String				startupActionName;
	private AbstractCapability	connectionsCapability;
	IResource					mockResource	= new MockResource();

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
	}

	/**
	 * tests ConnectionsCapability.getActionSet().getStartupRefreshAction() != null
	 */
	@Test
	public void getStartUpRefreshActionTest() {

		try {
			initBundles();
			mockResource.setResourceDescriptor(CapabilityHelper.newResourceDescriptor("roadm"));

			ICapabilityFactory connectionFactory = getOsgiService(ICapabilityFactory.class, "capability=connections", 10000);
			// Test elements not null
			log.info("Checking connections factory");
			Assert.assertNotNull(connectionFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("connections"));
			log.info("Creating connection capability");
			connectionsCapability = (AbstractCapability) connectionFactory.create(mockResource);
			Assert.assertNotNull(connectionsCapability);
			connectionsCapability.initialize();

			Assert.assertNotNull(connectionsCapability.getActionSet().getStartUpRefreshActionName());
			startupActionName = connectionsCapability.getActionSet().getStartUpRefreshActionName();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
	}

	// TODO test startupActionName action is executed during resourceRespository.startResource() (during bootstrap)

}