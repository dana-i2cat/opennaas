package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

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
import org.osgi.service.command.CommandProcessor;

/**
 * Tests new chassis operations in interface. In this feature it is necessary to create two operations to configure the status interface. The
 * objective it is to configure the interface status (up, down status administrative)
 * 
 * jira ticket: http://jira.i2cat.net:8080/browse/MANTYCHORE-161
 * 
 * @author Carlos Báez Ruiz
 * 
 */
@RunWith(JUnit4TestRunner.class)
public class InterfacesDownKarafTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(InterfacesDownKarafTest.class);
	IResourceRepository			repository;
	String						resourceFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	@Inject
	BundleContext				bundleContext	= null;

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

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		repository = getOsgiService(IResourceRepository.class, 50000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		initTest();

	}

	public void initTest() {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = repository.createResource(resourceDescriptor);
			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(resource.getResourceIdentifier().getId(), ProtocolSessionHelper
					.newSessionContextNetconf());
			repository.startResource(resource.getResourceDescriptor().getId());

		} catch (ResourceException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
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
	public void DownInterfaceETHTest() {

		try {
			// chassis:setVLAN interface VLANid
			String response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " fe-0/0/1", commandprocessor);
			// assert command output no contains ERROR tag

			String response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag

			String response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID + " -r", commandprocessor);
			// assert command output no contains ERROR tag

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getElementName().equals("fe-0/0/1")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
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
	public void DownInterfaceLTTest() {

		try {
			// chassis:setVLAN interface VLANid
			String response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " lt-0/1/2", commandprocessor);
			// assert command output no contains ERROR tag

			String response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag
			String response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID + " -r", commandprocessor);
			// assert command output no contains ERROR tag
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getElementName().equals("lt-0/1/2")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void DownInterfaceLoTest() {

		try {
			// chassis:setVLAN interface VLANid
			String response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " lo0.0", commandprocessor);
			// assert command output no contains ERROR tag

			String response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			// assert command output no contains ERROR tag
			String response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces " + resourceFriendlyID + " -r", commandprocessor);
			// assert command output no contains ERROR tag
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getElementName().equals("lo0.0")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

}
