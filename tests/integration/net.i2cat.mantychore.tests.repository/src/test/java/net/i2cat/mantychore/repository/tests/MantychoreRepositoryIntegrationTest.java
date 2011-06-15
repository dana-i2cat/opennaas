package net.i2cat.mantychore.repository.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.queuemanager.QueueManagerConstants;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
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

@RunWith(JUnit4TestRunner.class)
public class MantychoreRepositoryIntegrationTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory
													.getLog(MantychoreRepositoryIntegrationTest.class);

	IResourceRepository	repository;

	@Inject
	BundleContext		bundleContext	= null;

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

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	@Before
	public void initBundles() throws ResourceException {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		repository = getOsgiService(IResourceRepository.class, 50000);

		log.info("INFO: Initialized!");

	}

	@Test
	public void createAndRemoveResource() {

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			Assert.assertFalse(repository.listResources().isEmpty());

			createProtocolForResource(resource.getResourceIdentifier().getId());

			repository.removeResource(resource.getResourceIdentifier().getId());
			Assert.assertTrue(repository.listResources().isEmpty());

		} catch (Exception e) {
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void StartAndStopResource() {

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {

			/* create resource */
			IResource resource = repository.createResource(resourceDescriptor);

			Assert.assertNotNull(resource.getResourceIdentifier());
			Assert.assertNotNull(resource.getResourceDescriptor());
			Assert.assertTrue(resource.getCapabilities().isEmpty());
			Assert.assertNull(resource.getModel());
			Assert.assertNull(resource.getProfile());

			Assert.assertFalse(repository.listResources().isEmpty());

			/* start resource */
			repository.startResource(resource.getResourceIdentifier().getId());
			Assert.assertFalse(resource.getCapabilities().isEmpty());
			Assert.assertNotNull(resource.getModel());
			// Assert.assertNotNull(resource.getProfile());

			/* stop resource */
			repository.stopResource(resource.getResourceIdentifier().getId());

			Assert.assertNotNull(resource.getResourceIdentifier());
			Assert.assertNotNull(resource.getResourceDescriptor());
			Assert.assertTrue(resource.getCapabilities().isEmpty());
			Assert.assertNull(resource.getModel());
			// Assert.assertNull(resource.getProfile());
			Assert.assertFalse(repository.listResources().isEmpty());

			/* remove resource */

			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

			Assert.assertTrue(resource.getCapabilities().isEmpty());
			Assert.assertNull(resource.getModel());
			Assert.assertNull(resource.getProfile());
			Assert.assertTrue(repository.listResources().isEmpty());

		} catch (Exception e) {
			log.error("Exception!! ", e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void operationWithResource() {

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newChassisCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			resource.setModel(new ComputerSystem());
			createProtocolForResource(resource.getResourceIdentifier().getId());

			repository.startResource(resource.getResourceIdentifier().getId());

			ICapability chassisCapability = getCapability(resource.getCapabilities(), "chassis");
			if (chassisCapability == null)
				Assert.fail("Capability not found");
			ICapability queueCapability = getCapability(resource.getCapabilities(), "queue");
			if (queueCapability == null)
				Assert.fail("Capability not found");

			Response resp = (Response) chassisCapability.sendMessage(ActionConstants.GETCONFIG, null);
			List<ActionResponse> responses = (List<ActionResponse>) queueCapability.sendMessage(QueueManagerConstants.EXECUTE, null);

			Assert.assertTrue(responses.size() == 2);
			ActionResponse actionResponse = responses.get(0);
			Assert.assertEquals(ActionConstants.GETCONFIG, actionResponse.getActionID());
			for (Response response : actionResponse.getResponses()) {
				Assert.assertTrue(response.getStatus() == Response.Status.OK);
			}

			List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueManagerConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);

			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			log.error("Exception!!", e);
			Assert.fail(e.getMessage());
		}

	}

	public ICapability getCapability(List<ICapability> capabilities, String type) {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		return null;
	}

}
