package net.i2cat.mantychore.queuemanager.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.MockAction;
import net.i2cat.nexus.resources.helpers.MockResource;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class QueuemanagerTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log				log				= LogFactory
													.getLog(QueuemanagerTest.class);

	MockResource			mockResource;
	String					resourceID		= "junosResource";
	// QueueManager queueManagerService;
	ICapability				queueCapability;
	IQueueManagerService	queueManagerService;

	@Inject
	BundleContext			bundleContext	= null;

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
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
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

	public void initBundles() throws ProtocolException {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 20000);
		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("INFO: Initialized!");

	}

	// FIXME Before and After does not work with linux

	@Before
	public void before() throws ProtocolException, CapabilityException {
		initBundles();
		log.info("INFO: Before test, getting queue...");
		ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 20000);
		queueCapability = queueManagerFactory.create(mockResource);
		queueManagerService = getOsgiService(IQueueManagerService.class,
				"(capability=queue)(capability.name=" + mockResource.getResourceId() + ")", 20000);

	}

	@After
	public void after() {
		log.info("INFO: After test, cleaning queue...");
		queueManagerService.empty();

	}

	@Test
	public void removeAction() {
		// try {
		// before();
		// } catch (Exception e) {
		// e.printStackTrace();
		// Assert.fail(e.getLocalizedMessage());
		// }
		log.info("INFO: Remove actions");
		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerService.queueAction(action);
		Assert.assertTrue(queueManagerService.getActions().size() == 1);
		queueManagerService.empty();
		Assert.assertTrue(queueManagerService.getActions().size() == 0);
		log.info("INFO: OK!");
		// after();
	}

	@Test
	public void executeAction() {
		// try {
		// before();
		// } catch (Exception e) {
		// e.printStackTrace();
		// Assert.fail(e.getLocalizedMessage());
		// }
		log.info("INFO: Execute actions");

		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerService.queueAction(action);
		Assert.assertTrue(queueManagerService.getActions().size() == 1);
		try {
			queueManagerService.execute();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
		Assert.assertTrue(queueManagerService.getActions().size() == 0);
		log.info("INFO: OK!");
		// after();
	}

	@Test
	public void listActions() {
//		try {
//			before();
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail(e.getLocalizedMessage());
//		}
		log.info("INFO: List actions");

		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerService.queueAction(action);
		Assert.assertTrue(queueManagerService.getActions().size() == 1);

		for (IAction act : queueManagerService.getActions()) {
			log.info("INFO: action id=" + act.getActionID());
		}

		log.info("INFO: OK!");
		// after();
	}

}
