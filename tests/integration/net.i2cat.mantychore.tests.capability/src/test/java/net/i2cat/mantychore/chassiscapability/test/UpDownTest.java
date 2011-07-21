package net.i2cat.mantychore.chassiscapability.test;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.CapabilityException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.MockResource;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class UpDownTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log			log				= LogFactory
															.getLog(UpDownTest.class);
	static MockResource	mockResource;
	String				deviceID		= "junos";
	String				queueID			= "queue";
	static ICapability	chassisCapability;

	@Inject
	BundleContext		bundleContext	= null;
	private ICapability	queueCapability;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
//					, vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		// TODO IS IT EXIT A BETTER METHOD TO PASS THE URI
		String uri = System.getProperty("protocol.uri");
		if (uri != null && !uri.equals("${protocol.uri}")) {
			Option[] optionsWithURI = options(systemProperty("protocol.uri").value(uri));
			options = combine(options, optionsWithURI);
		}
		return options;
	}

	public void initResource() {
		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		/* initialize model */

		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor(deviceID, "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);
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

	public void initCapability() {

		try {
			log.info("INFO: Before test, getting queue...");
			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = queueManagerFactory.create(mockResource);

			// IQueueManagerService queueManagerService = (IQueueManagerService) getOsgiService(IQueueManagerService.class,
			// "(capability=queue)(capability.name=" + deviceID + ")", 5000);

			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

			ICapabilityFactory chassisFactory = getOsgiService(ICapabilityFactory.class, "capability=chassis", 10000);
			// Test elements not null
			log.info("Checking chassis factory");
			Assert.assertNotNull(chassisFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("chassis"));
			log.info("Creating chassis capability");
			chassisCapability = chassisFactory.create(mockResource);
			Assert.assertNotNull(chassisCapability);
			chassisCapability.initialize();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail();
		}
	}

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		initResource();
		initCapability();
	}

	private LogicalDevice getLogicalDevice(String nameInterface, ComputerSystem router) throws Exception {
		Iterator iterator = router.getLogicalDevices().iterator();

		while (iterator.hasNext()) {
			LogicalDevice logicalDevice = (LogicalDevice) iterator.next();
			if (logicalDevice.getElementName().equals(nameInterface))
				return logicalDevice;
		}

		throw new Exception("Not found logical device");

	}

	@Test
	public void UpDownActionTest() {
		try {
			Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);

			/* check model */
			LogicalDevice logicalDevice = null;
			try {
				logicalDevice = getLogicalDevice("fe-0/1/3", (ComputerSystem) mockResource.getModel());
			} catch (Exception ex) {
				Assert.fail("I don't found logicalDevice");
			}

			if (logicalDevice.getOperationalStatus() != OperationalStatus.OK) {
				log.info("The test can be executed because the needed interface is down");
				return;
			}

			/* send to change status */
			resp = (Response) chassisCapability.sendMessage(ActionConstants.CONFIGURESTATUS,
					newParamsConfigureStatus("fe-0/1/3", OperationalStatus.STOPPED));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			/* we refresh model, FIXME the getconfig operation have to implement a method to get status interface */
			resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 2);
			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 0);
			// Assert.assertTrue(logicalDevice.getOperationalStatus() == OperationalStatus.STOPPED);

			/* send to change status */
			resp = (Response) chassisCapability.sendMessage(ActionConstants.CONFIGURESTATUS,
					newParamsConfigureStatus("fe-0/0/3", OperationalStatus.OK));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			/* we refresh model, FIXME the getconfig operation have to implement a method to get status interface */
			resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 2);
			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 0);
			// Assert.assertTrue(logicalDevice.getOperationalStatus() == OperationalStatus.OK);

		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	private Object newParamsConfigureStatus(String interfaceName, OperationalStatus status) {

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setElementName(interfaceName);
		logicalPort.setOperationalStatus(status);
		return logicalPort;
	}
}
