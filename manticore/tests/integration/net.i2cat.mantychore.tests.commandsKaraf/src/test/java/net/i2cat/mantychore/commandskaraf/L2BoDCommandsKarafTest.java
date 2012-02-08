package net.i2cat.mantychore.commandskaraf;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundles;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;

@RunWith(JUnit4TestRunner.class)
public class L2BoDCommandsKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	private static final String	ACTION_NAME				= "dummy";

	private static final String	VERSION					= "1.0";

	private static final String	L2BOD_CAPABILIY_TYPE	= "l2bod";

	private static final String	QUEUE_CAPABILIY_TYPE	= "queue";

	private static final String	CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	RESOURCE_TYPE			= "bod";

	private static final String	RESOURCE_URI			= "user:pass@host.net:2212";

	private static final String	RESOURCE_INFO_NAME		= "L2BoD_Test";

	private static Log			log						= LogFactory
																.getLog(L2BoDCommandsKarafTest.class);

	private IResourceRepository	repository;

	private CommandProcessor	commandprocessor;

	@Configuration
	public Option[] additionalConfiguration() throws Exception {
		return combine(configuration(), new Customizer() {
			@Override
			public InputStream customizeTestProbe(InputStream testProbe) throws Exception {
				return TinyBundles.modifyBundle(testProbe).set(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional")
						.build();
			}
		});
	}

	/**
	 * Test to check the request connection command
	 */
	@Test
	public void RequestConnectionCommandTest() {

		// Initialize the bundles
		initBundles();

		// Clear repository
		clearRepository();

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		CapabilityDescriptor l2bodCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, VERSION, L2BOD_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(l2bodCapabilityDescriptor);

		CapabilityDescriptor queueCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, VERSION, QUEUE_CAPABILIY_TYPE,
				CAPABILITY_URI);
		lCapabilityDescriptors.add(queueCapabilityDescriptor);

		// BoD Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
				RESOURCE_INFO_NAME);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {

			IResource resource = repository.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			ArrayList<String> response = KarafCommandHelper.executeCommand(
					"l2bod:requestConnection  " + resourceFriendlyID + " int1 int2 ", commandprocessor);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("l2bod:shutdownConnection  " + resourceFriendlyID + " int1 int2 ",
					commandprocessor);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

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

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;
	}

	/**
	 * Create the protocol to connect
	 *
	 * @param resourceId
	 * @throws ProtocolException
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {

		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());
	}

	/**
	 * Initialize the bundles
	 */
	private void initBundles() {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		repository = getOsgiService(IResourceRepository.class, "type=bod", 50000);
		commandprocessor = getOsgiService(CommandProcessor.class);
		log.info("INFO: Initialized!");
	}

	/**
	 * Get the mantychore test options
	 *
	 * @return Option[]
	 * @throws Exception
	 */
	private static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(IntegrationTestsHelper.FELIX_CONTAINER),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	private void clearRepository() {

		log.info("Clearing resource repo");

		List<IResource> toRemove = repository.listResources();

		for (IResource resource : toRemove) {
			try {
				if (resource.getState().equals(State.ACTIVE)) {
					repository.startResource(resource.getResourceIdentifier().getId());
				}
				repository.removeResource(resource.getResourceIdentifier().getId());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				Assert.fail(e.getLocalizedMessage());
			}
		}

		log.info("Resource repo cleared!");
	}
}