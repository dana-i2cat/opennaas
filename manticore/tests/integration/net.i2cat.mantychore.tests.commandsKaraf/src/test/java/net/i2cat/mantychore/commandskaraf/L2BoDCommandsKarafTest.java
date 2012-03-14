package net.i2cat.mantychore.commandskaraf;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.configureConsole;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.After;
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
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class L2BoDCommandsKarafTest
{
	private static final String	ACTION_NAME				= "dummy";

	private static final String	VERSION					= "1.0";

	private static final String	L2BOD_CAPABILIY_TYPE	= "l2bod";

	private static final String	QUEUE_CAPABILIY_TYPE	= "queue";

	private static final String	CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	RESOURCE_TYPE			= "bod";

	private static final String	RESOURCE_URI			= "user:pass@host.net:2212";

	private static final String	RESOURCE_INFO_NAME		= "L2BoD_Test";

	private static Log			log						= LogFactory.getLog(L2BoDCommandsKarafTest.class);

	@Inject
	@Filter("(type=bod)")
	private IResourceRepository	repository;

	@Inject
	private CommandProcessor	commandprocessor;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.bod.capability.l2bod)")
	private BlueprintContainer	bodCapabilityService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.bod.repository)")
	private BlueprintContainer	bodRepositoryService;

	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
		return probe;
	}

	@Configuration
	public static Option[] configuration() {
		return options(karafDistributionConfiguration()
				.frameworkUrl(maven()
						.groupId("net.i2cat.mantychore")
						.artifactId("assembly")
						.type("zip")
						.classifier("bin")
						.versionAsInProject())
				.karafVersion("2.2.2")
				.name("mantychore")
				.unpackDirectory(new File("target/paxexam")),
				editConfigurationFilePut("etc/org.apache.karaf.features.cfg",
						"featuresBoot",
						"opennaas-bod,nexus-tests-helper"),
				configureConsole()
						.ignoreLocalConsole()
						.ignoreRemoteShell(),
				keepRuntimeFolder());
	}

	/**
	 * Test to check the request connection command
	 */
	@Test
	public void RequestConnectionCommandTest() throws Exception {
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

		IResource resource = repository.createResource(resourceDescriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.startResource(resource.getResourceDescriptor().getId());

		ArrayList<String> response =
				KarafCommandHelper.executeCommand("l2bod:requestConnection  " + resourceFriendlyID + " int1 int2 ", commandprocessor);
		// assert command output does not contain ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		response = KarafCommandHelper.executeCommand("l2bod:shutdownConnection  " + resourceFriendlyID + " int1 int2 ",
				commandprocessor);
		// assert command output does not contain ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		repository.stopResource(resource.getResourceIdentifier().getId());
		repository.removeResource(resource.getResourceIdentifier().getId());
	}

	/**
	 * Create the protocol to connect
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {
		// A protocol will be required in the future for bod actionset being able to connect to the existing BoD system.
		// Dummy actionset (being used in this test) does not require a protocol.
		// This method is called only to reproduce typical workflow, but no action is required (while using dummy actionset).
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	@After
	public void clearRepository() {
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