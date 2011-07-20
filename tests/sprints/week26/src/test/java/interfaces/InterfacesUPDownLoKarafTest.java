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
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.helpers.ResourceDescriptorFactory;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
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

@SuppressWarnings("unused")
@RunWith(JUnit4TestRunner.class)
public class InterfacesUPDownLoKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
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

	// @Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		repository = getOsgiService(IResourceRepository.class, 50000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		initTest();

	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		String uri = "mock://user:pass@host.net:2212/mocksubsystem";
		ProtocolSessionContext protocolSessionContext = ProtocolSessionHelper.newSessionContextNetconf();
		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);

		protocolManager.getProtocolSessionManagerWithContext(resourceId, protocolSessionContext);

	}

	public void initTest() {

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);

		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			resource = repository.createResource(resourceDescriptor);

			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			// call the command to initialize the model

		} catch (ResourceException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	// @After
	public void resetRepository() {

		try {
			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());
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

	}

	@Test
	public void DownUPInterfaceLoTest() {
		initBundles();
		DownInterfaceLo();
		UPInterfaceLo();
		resetRepository();
	}

	public void DownInterfaceLo() {

		try {
			// chassis:setVLAN interface VLANid
			List<String> response = KarafCommandHelper.executeCommand("chassis:down " + resourceFriendlyID + " lo0", commandprocessor);
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

			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			// for (LogicalDevice logicalDevice : ld) {
			// if (logicalDevice instanceof LogicalPort && logicalDevice.getElementName().equals("lo0.0")) {
			// LogicalPort logicalPort = (LogicalPort) logicalDevice;
			// Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
			// }
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	public void UPInterfaceLo() {

		try {
			// chassis:setVLAN interface VLANid
			List<String> response = KarafCommandHelper.executeCommand("chassis:up " + resourceFriendlyID + " lo0", commandprocessor);
			log.info(response.get(0));

			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			List<String> response1 = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID, commandprocessor);
			log.info(response1.get(0));

			// assert command output no contains ERROR tag
			Assert.assertTrue(response1.get(1).isEmpty());

			List<String> response2 = KarafCommandHelper.executeCommand("chassis:showInterfaces  -r " + resourceFriendlyID, commandprocessor);
			log.info(response2.get(0));

			// assert command output no contains ERROR tag
			Assert.assertTrue(response2.get(1).isEmpty());

			// // assert model updated
			// ComputerSystem system = (ComputerSystem) resource.getModel();
			// List<LogicalDevice> ld = system.getLogicalDevices();
			// for (LogicalDevice logicalDevice : ld) {
			// if (logicalDevice instanceof LogicalPort && logicalDevice.getElementName().equals("lo0.0")) {
			// LogicalPort logicalPort = (LogicalPort) logicalDevice;
			// Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
			// }
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
