package mantychore;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.model.ComputerSystem;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ProtocolSessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
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

@RunWith(JUnit4TestRunner.class)
public class CreateLogicalRouterTest extends AbstractIntegrationTest {
	static Log					log				= LogFactory
														.getLog(CreateLogicalRouterTest.class);

	String						resourceFriendlyID;
	String						LRFriendlyID	= "pepito";
	IResource					resource;
	private CommandProcessor	commandprocessor;
	private IResourceManager	resourceManager;

	private Boolean				isMock;

	/*
	 * all types interfaces
	 */
	@Inject
	BundleContext				bundleContext	= null;

	
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				// ////////import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

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

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all tLogicalROuterhe bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		resourceManager = getOsgiService(IResourceManager.class, 5000);
		commandprocessor = getOsgiService(CommandProcessor.class);

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

	public void initResource() {

		clearRepo();
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();
		try {

			resource = resourceManager.createResource(resourceDescriptor);
			isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// call the command to initialize the model
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			Assert.fail(e.getMessage());
		}

	}

	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				try {
					resourceManager.stopResource(resource.getResourceIdentifier());
				} catch (ResourceException e) {
					log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				}
			}
			try {
				resourceManager.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
			}

		}

		log.info("Resource repo cleared!");
	}

	@Test
	public void createLogicalRouterOnRealRouterTest() {

		initBundles();
		initResource();

		List<String> response;
		List<String> response1;
		try {

			if (isMock) {
				LRFriendlyID = "routerV2";
			} else {
				LRFriendlyID = "pepito";
			}

			// creating LogicalRouter
			response = KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " " + LRFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());
			response = KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID,
					commandprocessor);
			Assert.assertTrue(response.get(1).isEmpty());

			// check logical router creation
			response = KarafCommandHelper.executeCommand("resource:refresh " + resourceFriendlyID,
					commandprocessor);
			Assert.assertTrue(response.get(1).isEmpty());

			response1 = KarafCommandHelper.executeCommand("resource:list ",
					commandprocessor);
			Assert.assertTrue(response1.get(1).isEmpty());
			if (!isMock) {

				Assert.assertTrue(CheckHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));

				Assert.assertTrue(response1.get(0).contains(LRFriendlyID));

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void listLogicalRoutersOnResourceTest() {
		initBundles();
		initResource();
		List<String> response;
		try {
			if (isMock) {
				LRFriendlyID = "routerV2";
			} else {
				LRFriendlyID = "pepito";
			}
			// chassis:listLogicalRouters
			response = KarafCommandHelper.executeCommand("chassis:listLogicalRouter " + resourceFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			Assert.assertTrue(response.get(0).contains(LRFriendlyID));

			if (!isMock) {
				Assert.assertTrue(CheckHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void discoveryOnBootstrapLogicalRoutersTest() {
		initBundles();
		initResource();
		try {
			if (isMock) {
				LRFriendlyID = "routerV2";
			} else {
				LRFriendlyID = "pepito";
			}

			// resource:list
			List<String> response = KarafCommandHelper.executeCommand("resource:list ",
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check that the logical router is on the list
			Assert.assertTrue(response.get(0).contains(LRFriendlyID));

			response = KarafCommandHelper.executeCommand("resource:info " + "router:" + LRFriendlyID,
					commandprocessor);
			// assert command output no contains ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			// check resource initialized
			if (!isMock)
				Assert.assertTrue(response.get(0).contains("INITIALIZED"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}