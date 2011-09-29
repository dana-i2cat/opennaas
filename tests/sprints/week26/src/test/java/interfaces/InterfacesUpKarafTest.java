package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.IntegrationTestsHelper;
import helpers.KarafCommandHelper;
import helpers.ProtocolSessionHelper;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

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
import org.osgi.service.command.CommandProcessor;

//import org.apache.felix.service.command.CommandProcessor;

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
public class InterfacesUpKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log			log				= LogFactory
												.getLog(InterfacesUpKarafTest.class);

	CommandProcessor	commandprocessor;
	IResourceManager	resourceManager;
	String				resourceFriendlyID;
	IResource			resource;
	@Inject
	BundleContext		bundleContext	= null;

	private Boolean		isMock;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
					// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
					);

		return options;
	}

	// @Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 50000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		initTest();

	}

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);

		ProtocolSessionContext context = ProtocolSessionHelper.newSessionContextNetconf();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		if (context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock")) {
			return true;
		}

		return false;
	}

	public void initTest() {
		// ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);

		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			clearRepo();
			resource = resourceManager.createResource(resourceDescriptor);

			isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// call the command to initialize the model

		} catch (ResourceException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	public void clearRepo() throws ResourceException {
		for (IResource resource : resourceManager.listResources()) {
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
	}

	// @After
	public void resetRepository() {

		try {
			resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		// Assert.assertTrue(resourceManager.listResources().isEmpty());

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
	public void UpInterfaceETHTest() {
		initBundles();

		try {
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

			List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
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
		} catch (Exception e) {
			resetRepository();
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		resetRepository();
	}

	/**
	 * This test change the interface status to down. It try to enable the administrative mode, and it will be able to be configured. Estimation: 5
	 * (the operation can be cloned fromt he upInterface) tasks: -> Create unitary test, and integration test to new feature -> Implement operation ->
	 * create template -> add modifications in the parser -> create karaf command -> test to a real router
	 * 
	 * 
	 */
	@Test
	public void UPInterfaceLTTest() {
		initBundles();

		String interfaceToConfigure = "lt-0/1/2";
		try {
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
			List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces -r " + resourceFriendlyID, commandprocessor);
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
		} catch (Exception e) {
			resetRepository();
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		resetRepository();
	}

}
