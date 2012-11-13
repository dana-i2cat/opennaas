package org.opennaas.itests.bod;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDCapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.itests.helpers.AbstractKarafCommandTest;
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
	private static final String		ACTION_NAME				= "dummy";
	private static final String		VERSION					= "1.0";
	private static final String		L2BOD_CAPABILIY_TYPE	= L2BoDCapability.CAPABILITY_TYPE;
	private static final String		QUEUE_CAPABILIY_TYPE	= "queue";
	private static final String		CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String		RESOURCE_TYPE			= "bod";
	private static final String		RESOURCE_URI			= "user:pass@host.net:2212";
	private static final String		RESOURCE_INFO_NAME		= "L2BoD_Test";

	private static Log				log						= LogFactory.getLog(L2BoDCommandsKarafTest.class);

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	@Filter("(type=bod)")
	private IResourceRepository		repository;

	@Inject
	private IProtocolManager		protocolManager;

	private ProtocolSessionManager	protocolSessionManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.capability.l2bod)")
	private BlueprintContainer		bodCapabilityService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.bod.repository)")
	private BlueprintContainer		bodRepositoryService;

	private IResource				resource;

	/*
	 * CONFIGURATION, BEFORE and AFTER
	 */

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-bod", "opennaas-netconf"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void fillRepository() throws ResourceException
	{
		Assert.assertTrue(repository.listResources().isEmpty());

		// L2BoD Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		lCapabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				VERSION,
				L2BOD_CAPABILIY_TYPE,
				CAPABILITY_URI));

		lCapabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				VERSION,
				QUEUE_CAPABILIY_TYPE,
				CAPABILITY_URI));

		// Create BOD resource
		resource = resourceManager.createResource(ResourceHelper.newResourceDescriptor(lCapabilityDescriptors,
				RESOURCE_TYPE,
				RESOURCE_URI,
				RESOURCE_INFO_NAME));

		Assert.assertTrue(repository.listResources().contains(resource));

		// Start resource
		resourceManager.startResource(resource.getResourceIdentifier());
		Assert.assertTrue(resource.getState().equals(State.ACTIVE));
		Assert.assertTrue(resource.getCapabilities().size() > 0);
		Assert.assertFalse(repository.listResources().isEmpty());
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	@After
	public void clearRepository() throws ResourceException {

		log.info("Clearing resource repo");

		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
			resourceManager.removeResource(resource.getResourceIdentifier());
		}
		Assert.assertTrue(resourceManager.listResources().isEmpty());
		Assert.assertTrue(repository.listResources().isEmpty());

		log.info("Resource repo cleared!");
	}

	/*
	 * TESTS
	 */

	/**
	 * Test to check the request connection command
	 */
	@Test
	public void RequestConnectionCommandTest() throws Exception {

		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		repository.stopResource(resource.getResourceDescriptor().getId());
		createProtocolForResource(resource.getResourceIdentifier().getId());
		repository.startResource(resource.getResourceDescriptor().getId());

		NetworkModel model = (NetworkModel) resource.getModel();
		model.getNetworkElements().add(createInterface("int1"));
		model.getNetworkElements().add(createInterface("int2"));

		List<String> response =
				executeCommand("l2bod:requestConnection --endtime 2012-04-20T18:36:00Z --capacity 10 " + resourceFriendlyID + " int1 int2");
		// assert command output does not contain ERROR tag
		log.info(response);
		Assert.assertTrue(response.get(1).isEmpty());

		// simulate action has been executed
		BoDLink link = new BoDLink();
		link.setName("link1");
		model.getNetworkElements().add(link);

		List<Link> links = NetworkModelHelper.getLinks(model.getNetworkElements());
		Assert.assertFalse(links.isEmpty());
		String linkName = links.get(0).getName();

		response = executeCommand("l2bod:shutdownConnection " + resourceFriendlyID + " " + linkName);
		// assert command output does not contain ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		repository.stopResource(resource.getResourceIdentifier().getId());
		repository.removeResource(resource.getResourceIdentifier().getId());
	}

	@Test
	public void BoDResourceTest() throws Exception {

		// Get Capabilities
		List<? extends ICapability> capabilityList = resource.getCapabilities();
		Assert.assertTrue(capabilityList.size() > 0);

		L2BoDCapability capability = (L2BoDCapability) resource.getCapabilityByInterface(IL2BoDCapability.class);

		IActionSet actionSet = ((L2BoDCapability) capability).getActionSet();
		List<String> actionList = actionSet.getActionNames();
		Assert.assertTrue(actionList.size() > 0);

		for (String actionName : actionList) {
			IAction action = actionSet.obtainAction(actionName);
			ActionResponse actionResponse = action.execute(protocolSessionManager);
			Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));
		}
	}

	/*
	 * HELPERS
	 */

	private Interface createInterface(String name)
	{
		Interface i = new Interface();
		i.setName(name);
		return i;
	}

	/**
	 * Create the protocol to connect
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 */
	private void createProtocolForResource(String resourceId) throws ProtocolException {

		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext psContext = new ProtocolSessionContext();

		psContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		psContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		psContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");

		protocolManager.getProtocolSessionManagerWithContext(resourceId, psContext);
	}

}