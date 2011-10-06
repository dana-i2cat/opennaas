package interfaces;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import helpers.IntegrationTestsHelper;
import helpers.KarafCommandHelper;
import helpers.ProtocolSessionHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.ManagedSystemElement.OperationalStatus;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundles;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;
import org.osgi.framework.BundleContext;
import org.apache.felix.service.command.CommandProcessor;

//import org.apache.felix.service.command.CommandProcessor;

@SuppressWarnings("unused")
@RunWith(JUnit4TestRunner.class)
public class InterfacesUPDownLoKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
	static Log					log				= LogFactory
														.getLog(InterfacesDownKarafTest.class);
	IResourceManager			resourceManager;
	String						resourceFriendlyID;
	IResource					resource;
	private CommandProcessor	commandprocessor;
	@Inject
	BundleContext				bundleContext	= null;
	private Boolean				isMock			= false;

	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	@Configuration
	public Option[] additionalConfiguration() throws Exception {
		return combine(configuration(), new Customizer() {
			@Override
			public InputStream customizeTestProbe(InputStream testProbe) throws Exception {
				return TinyBundles.modifyBundle(testProbe).set(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional").build();
			}
		});
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

	@Test
	public void DownUPInterfaceLoTest() {
		initBundles();
		try {
			DownInterfaceLo();
			UPInterfaceLo();
		} catch (Exception e) {
			resetRepository();
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		resetRepository();
	}

	public void DownInterfaceLo() throws Exception {

		// try {
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

		if (!isMock) {
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals("lo0.0")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.STOPPED);
				}
			}

		}
	}

	public void UPInterfaceLo() throws Exception {

		// try {
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
		if (!isMock) {
			// assert model updated
			ComputerSystem system = (ComputerSystem) resource.getModel();
			List<LogicalDevice> ld = system.getLogicalDevices();
			for (LogicalDevice logicalDevice : ld) {
				if (logicalDevice instanceof LogicalPort && logicalDevice.getName().equals("lo0.0")) {
					LogicalPort logicalPort = (LogicalPort) logicalDevice;
					Assert.assertTrue(logicalPort.getOperationalStatus() == OperationalStatus.OK);
				}
			}

		}
	}

}
