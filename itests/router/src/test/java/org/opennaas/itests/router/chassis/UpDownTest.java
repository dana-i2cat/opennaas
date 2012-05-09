package org.opennaas.itests.router.chassis;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
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
public class UpDownTest
{
	private final static Log		log			= LogFactory.getLog(UpDownTest.class);
	private final String			deviceID	= "junos";
	private final String			queueID		= "queue";

	private MockResource			mockResource;
	private IChassisCapability		chassisCapability;
	private IQueueManagerCapability	queueCapability;
	private boolean					isMock		= false;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=chassis)")
	private ICapabilityFactory		chassisFactory;

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
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = (IQueueManagerCapability) queueManagerFactory.create(mockResource);
			((ICapabilityLifecycle) queueCapability).initialize();

			protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

			// Test elements not null
			log.info("Checking chassis factory");
			Assert.assertNotNull(chassisFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("chassis"));
			log.info("Creating chassis capability");
			chassisCapability = (IChassisCapability) chassisFactory.create(mockResource);
			Assert.assertNotNull(chassisCapability);
			((ICapabilityLifecycle) chassisCapability).initialize();

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

	@Before
	public void setup() {
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
	public void UpDownActionTest() throws CapabilityException, ProtocolException {
		// Force to refresh the model
		QueueResponse queueResponse = (QueueResponse) queueCapability.execute();
		String str = "";
		ComputerSystem model = (ComputerSystem) mockResource.getModel();
		Assert.assertNotNull(model);
		for (LogicalDevice device : model.getLogicalDevices()) {
			if (device instanceof EthernetPort) {
				EthernetPort port = (EthernetPort) device;
				Assert.assertNotNull("OperationalStatus must be set", port.getOperationalStatus());

				str += "- EthernetPort: " + '\n';
				str += port.getName() + '.' + port.getPortNumber() + '\n';
				str += port.getOperationalStatus();
				str += '\n';
				for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint)
								protocolEndpoint;
						str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
						str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
					}
				}
			}
			else {
				str += "not searched device";
			}

		}

		log.info(str);

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
		chassisCapability.downPhysicalInterface(newParamsConfigureStatus(interfaceName, OperationalStatus.STOPPED));

		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 1);
		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 0);

		if (!isMock) {
			checkOperationalStatus((ComputerSystem) mockResource.getModel(), interfaceName, OperationalStatus.STOPPED);
		}

		/* send to change status */
		chassisCapability.upPhysicalInterface(newParamsConfigureStatus(interfaceName, OperationalStatus.OK));

		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 1);
		queueResponse = (QueueResponse) queueCapability.execute();
		Assert.assertTrue(queueResponse.isOk());
		Assert.assertTrue(((List<IAction>) queueCapability.getActions()).size() == 0);

		if (!isMock) {
			checkOperationalStatus((ComputerSystem) mockResource.getModel(), interfaceName, OperationalStatus.OK);
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

	private LogicalPort newParamsConfigureStatus(String interfaceName, OperationalStatus status) {
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName(interfaceName);
		logicalPort.setOperationalStatus(status);
		return logicalPort;
	}
}
