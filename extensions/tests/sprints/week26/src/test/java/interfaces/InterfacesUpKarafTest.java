package interfaces;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Tests new chassis operations in interface. In this feature it is necessary to create two operations to configure the status interface. The
 * objective it is to configure the interface status (up, down status administrative)
 *
 * jira ticket: http://jira.i2cat.net:8080/browse/MANTYCHORE-161
 *
 * @author Carlos Báez Ruiz
 *
 */
@SuppressWarnings("unused")
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class InterfacesUpKarafTest
{
	private final static Log	log			= LogFactory.getLog(InterfacesUpKarafTest.class);

	private String				resourceFriendlyID;
	private IResource			resource;
	private Boolean				isMock;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private CommandProcessor	commandprocessor;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private BundleContext		bundleContext;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer routerService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer queueService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.chassis)")
    private BlueprintContainer chassisService;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-router"),
					   includeTestHelper(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
		ProtocolSessionContext context = ResourceHelper.newSessionContextNetconf();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		if (context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock")) {
			return true;
		}

		return false;
	}

	@Before
	public void initTest() throws ResourceException, ProtocolException {
		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);

		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		resource = resourceManager.createResource(resourceDescriptor);

		isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
	}

	@After
	public void resetRepository() throws InterruptedException, ResourceException {
		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
		for (IResource resource : resourceManager.listResources()) {
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	/**
	 * This test change the interface status to up. It try to enable the administrative mode, and it will be able to be configured. Estimation: 15
	 *
	 * tasks:
	 *
	 * -> Create unitary test, and integration test to new feature
	 *
	 * -> Implement operation
	 *
	 * -> create template
	 *
	 * -> add modifications in the parser
	 *
	 * -> create karaf command -
	 *
	 * -> test to a real router
	 *
	 *
	 */
	@Test
	public void UpInterfaceETHTest() throws Exception {
		String interfaceToConfigure = "fe-0/3/0";
		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:up " + resourceFriendlyID + " " + interfaceToConfigure,
																  commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		// assert command output no contains ERROR tag

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response1.get(1).isEmpty());

		// assert command output no contains ERROR tag

		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response2.get(1).isEmpty());

		// assert command output no contains ERROR tag
		if (!isMock) {
			// assert model updated
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
				}
			}
		}
	}

	/**
	 * This test change the interface status to down. It try to enable the administrative mode, and it will be able to be configured. Estimation: 5
	 * (the operation can be cloned fromt he upInterface) tasks: -> Create unitary test, and integration test to new feature -> Implement operation ->
	 * create template -> add modifications in the parser -> create karaf command -> test to a real router
	 *
	 *
	 */
	@Test
	public void UPInterfaceLTTest() throws Exception {
		String interfaceToConfigure = "lt-0/1/2";
		// chassis:setVLAN interface VLANid
		List<String> response = KarafCommandHelper.executeCommand("chassis:up " + resourceFriendlyID + " " + interfaceToConfigure,
																  commandprocessor);
		log.info(response.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
		log.info(response1.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID, commandprocessor);
		log.info(response2.get(0));

		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		if (!isMock) {
			// assert model updated
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals(interfaceToConfigure)) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
				}
			}
		}
	}
}
