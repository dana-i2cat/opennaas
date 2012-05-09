package org.opennaas.itests.bod.shell;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.itests.helpers.AbstractKarafCommandTest;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.topology.Interface;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class L2BoDCommandsKarafTest extends AbstractKarafCommandTest
{
	private static final String	ACTION_NAME				= "dummy";

	private static final String	VERSION					= "1.0";

	private static final String	L2BOD_CAPABILIY_TYPE	= "l2bod";

	private static final String	QUEUE_CAPABILIY_TYPE	= "queue";

	private static final String	CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	RESOURCE_TYPE			= "bod";

	private static final String	RESOURCE_URI			= "user:pass@host.net:2212";

	private static final String	RESOURCE_INFO_NAME		= "L2BoD_Test";

	private static Log			log						=
																LogFactory.getLog(L2BoDCommandsKarafTest.class);

	@Inject
	@Filter("(type=bod)")
	private IResourceRepository	repository;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.capability.l2bod)")
	private BlueprintContainer	bodCapabilityService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.repository)")
	private BlueprintContainer	bodRepositoryService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-bod", "opennaas-netconf"),
				includeTestHelper(),
				noConsole(),
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

		NetworkModel model = (NetworkModel) resource.getModel();
		model.getNetworkElements().add(createInterface("int1"));
		model.getNetworkElements().add(createInterface("int2"));

		List<String> response =
				executeCommand("l2bod:requestConnection --endtime 2012-04-20T18:36:00Z --capacity 10 " + resourceFriendlyID + " int1 int2");
		// assert command output does not contain ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		response = executeCommand("l2bod:shutdownConnection " + resourceFriendlyID + " int1 int2");
		// assert command output does not contain ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		repository.stopResource(resource.getResourceIdentifier().getId());
		repository.removeResource(resource.getResourceIdentifier().getId());
	}

	private Interface createInterface(String name)
	{
		Interface i = new Interface();
		i.setName(name);
		return i;
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
		return protocolSessionContext;
	}

	/**
	 * Create the protocol to connect
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());
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