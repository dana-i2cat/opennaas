package net.i2cat.mantychore.chassiscapability.test;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.chassiscapability.test.mock.MockBootstrapper;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
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
	boolean				isMock			= false;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
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

		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel((IModel) new ComputerSystem());
		mockResource.setBootstrapper(new MockBootstrapper());

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor(deviceID, "router", capabilities);
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

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED

		String protoUri = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		if (protoUri.startsWith("mock"))
			isMock = true;

		return protocolSessionContext;
	}

	public void initCapability() {

		try {
			log.info("INFO: Before test, getting queue...");
			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = queueManagerFactory.create(mockResource);
			queueCapability.initialize();

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

			mockResource.addCapability(chassisCapability);
			mockResource.addCapability(queueCapability);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			if (ExceptionUtils.getRootCause(e) != null)
				log.error(ExceptionUtils.getRootCause(e).getMessage());
			Assert.fail(e.getMessage());
		}
	}

	// @Before
	public void initBundles() {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		initResource();
		initCapability();
	}

	private LogicalDevice getLogicalDevice(String nameInterface, ComputerSystem router) throws Exception {
		Iterator<LogicalDevice> iterator = router.getLogicalDevices().iterator();

		while (iterator.hasNext()) {
			LogicalDevice logicalDevice = iterator.next();
			if (logicalDevice.getName().equals(nameInterface))
				return logicalDevice;
		}

		throw new Exception("Not found logical device");

	}

	@Test
	public void UpDownActionTest() {

		initBundles();

		try {

			Response resp;
			QueueResponse queueResponse;

			resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());

			String interfaceName = "fe-0/1/3";

			/* check model */
			LogicalDevice logicalDevice = null;
			try {
				logicalDevice = getLogicalDevice(interfaceName, (ComputerSystem) mockResource.getModel());
			} catch (Exception ex) {
				Assert.fail("LogicalDevice not found");
			}

			if (logicalDevice.getOperationalStatus() != OperationalStatus.OK) {
				Assert.fail("The test can't be executed because the needed interface is down");
			}

			/* send to change status */
			resp = (Response) chassisCapability.sendMessage(ActionConstants.CONFIGURESTATUS,
					newParamsConfigureStatus(interfaceName, OperationalStatus.STOPPED));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 1);
			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());
			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 0);

			if (!isMock) {
				checkOperationalStatus((ComputerSystem) mockResource.getModel(), interfaceName, OperationalStatus.STOPPED);
			}

			/* send to change status */
			resp = (Response) chassisCapability.sendMessage(ActionConstants.CONFIGURESTATUS,
					newParamsConfigureStatus(interfaceName, OperationalStatus.OK));
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 1);
			queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.isOk());
			Assert.assertTrue(((List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null)).size() == 0);

			if (!isMock) {
				checkOperationalStatus((ComputerSystem) mockResource.getModel(), interfaceName, OperationalStatus.OK);
			}

		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	private void checkOperationalStatus(ComputerSystem model, String interfaceName, OperationalStatus status) {
		LogicalPort port = null;
		try {
			LogicalDevice port1 = getLogicalDevice(interfaceName, model);
			if (port1 instanceof LogicalPort)
				port = (LogicalPort) port1;
		} catch (Exception e) {
		}

		if (port == null)
			Assert.fail("Interface not found in model");
		Assert.assertTrue(port.getOperatingStatus().equals(status));
	}

	private Object newParamsConfigureStatus(String interfaceName, OperationalStatus status) {

		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName(interfaceName);
		logicalPort.setOperationalStatus(status);
		return logicalPort;
	}
}
