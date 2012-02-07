package automaticrefresh;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
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

	private List<String>		startupActionNames;
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
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
	}

	/**
	 * tests ConnectionsCapability.getActionSet().getStartupRefreshAction() != null
	 */
	@Test
	public void getStartUpRefreshActionTest() {

		try {
			// TODO this initbundles guarantee that all the bundles are initialized. It is possible to work the test without this initbundles
			initBundles();
			mockResource.setResourceDescriptor(ResourceHelper.newResourceDescriptorProteus("roadm"));

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

			Assert.assertFalse(connectionsCapability.getActionSet().getRefreshActionName().isEmpty());
			startupActionNames = connectionsCapability.getActionSet().getRefreshActionName();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(e.getLocalizedMessage());
			// if (org.apache.commons.lang3.exception.ExceptionUtils.getRootCause(e) != null)
			// log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
	}

	// TODO test startupActionName action is executed during resourceRespository.startResource() (during bootstrap)

}
