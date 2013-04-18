package org.opennaas.itests.core.shell;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockProfile;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.itests.core.resources.DummyAction;
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
public class CommandsKarafTest extends AbstractKarafCommandTest
{
	static Log					log	= LogFactory.getLog(CommandsKarafTest.class);

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	private IProtocolManager	protocolManager;

	@Inject
	private IProfileManager		profileManager;

	@Inject
	@Filter("(type=router)")
	private IResourceRepository	repository;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer	routerRepositoryService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.ip)")
	private BlueprintContainer	ipService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)")
	private BlueprintContainer	queueService;

	/*
	 * CONFIGURATION, BEFORE and AFTER
	 */

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router", "opennaas-junos"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@After
	public void clearRepo() throws ResourceException {

		log.info("Clearing resource repo");

		resourceManager.destroyAllResources();

		log.info("Clearing profiles");

		for (ProfileDescriptor profile : profileManager.listProfiles())
			profileManager.removeProfile(profile.getProfileName());
	}

	/*
	 * KARAF COMMAND TESTS
	 */

	@Test
	public void InfoCommandTest() throws Exception {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ip");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
				.newResourceDescriptor("junosm20", "router", capabilities);
		String resourceFriendlyID =
				resourceDescriptor.getInformation()
						.getType()
						+ ":"
						+ resourceDescriptor.getInformation().getName();

		IResource resource =
				resourceManager.createResource(resourceDescriptor);
		createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());

		List<String> response =
				executeCommand("resource:info " + resourceFriendlyID);

		if (!response.get(1).isEmpty()) {
			Assert.fail(response.get(1));
		}
		Assert.assertTrue(response.get(1).isEmpty());

		Assert.assertTrue(response.get(0).contains("Resource Id: " + resource.getResourceIdentifier().getId()));
		Assert.assertTrue(response.get(0).contains("Type: ip"));
		Assert.assertTrue(response.get(0).contains("Type: queue"));

		resourceManager.stopResource(resource.getResourceIdentifier());
		resourceManager.removeResource(resource.getResourceIdentifier());
	}

	/**
	 * Configure the protocol to connect
	 */
	@Test
	public void SetAndGetInterfacesCommandTest() {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ip");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource1", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			List<String> response = executeCommand(
					"ip:setIP  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1/24");
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = executeCommand("queue:listActions  " + resourceFriendlyID);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = executeCommand("queue:execute  " + resourceFriendlyID);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testListInfoAndRemoveProfileCommand() throws Exception {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ip");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource2", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setInterface", "ip", "router");
		IProfile profile2 = createProfile("profile2", "setInterface", "ip", "router");

		profileManager.addProfile(profile1);

		executeCommand("profile:list");
		executeCommand("profile:info " + profile1.getProfileName());

		profileManager.addProfile(profile2);
		executeCommand("profile:list");

		// add resource with profile1
		resourceDescriptor.setProfileId(profile1.getProfileName());
		IResource resource = repository.createResource(resourceDescriptor);

		createProtocolForResource(resource.getResourceDescriptor().getId());

		repository.startResource(resource.getResourceDescriptor().getId());

		executeCommand("profile:list");
		executeCommand("profile:info " + profile1.getProfileName());

		executeCommand("profile:remove " + profile2.getProfileName());
		executeCommand("profile:list");

		// there is no such profile, but it does not fail for that ;)
		// it silently does nothing
		executeCommand("profile:remove " + profile2.getProfileName());

		boolean failed = false;
		try {
			executeCommand("profile:remove " + profile1.getProfileName());
		} catch (ResourceException e) {
			// profile in use
			failed = true;
		}
		Assert.assertTrue(failed);
		repository.stopResource(resource.getResourceIdentifier().getId());
		repository.removeResource(resource.getResourceIdentifier().getId());
		// String resourceFriendlyID = resource.getResourceDescriptor().getInformation().getType() + ":" + resource.getResourceDescriptor()
		// .getInformation().getName();
		// log.debug("executing command  #>resource:remove " + resourceFriendlyID);
		// executeCommand("resource:remove " + resourceFriendlyID);

		executeCommand("profile:remove " + profile1.getProfileName());
		executeCommand("profile:list");

		// there is no such profile, but it does not fail for that ;)
		// it silently does nothing
		executeCommand("profile:info " + profile1.getProfileName());
	}

	@Test
	public void testActionIsOverriden() throws Exception {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ip");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource7", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setIP", "ip", "router");

		profileManager.addProfile(profile1);

		// add resource with profile1
		resourceDescriptor.setProfileId(profile1.getProfileName());
		IResource resource = repository.createResource(resourceDescriptor);

		createProtocolForResource(resource.getResourceDescriptor().getId());

		// launch setInterface Action and assert DummyAction is executed instead of original one
		repository.startResource(resource.getResourceIdentifier().getId());

		List<String> response = executeCommand("ip:list " + resourceFriendlyID);

		// assert command output contains no ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		response = executeCommand("ip:setIP  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1/24");
		// assert command output contains no ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		response = executeCommand("ip:list " + resourceFriendlyID);
		// assert command output contains no ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
	}

	/*
	 * HELPERS
	 */

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
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");

		// ADDED
		return protocolSessionContext;
	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	private IProfile createProfile(String profileName, String actionId, String capabilityId, String resourceType) {

		ActionSet actionSet = new ActionSet();

		actionSet.setActionSetId("aProfileActionSet");
		actionSet.putAction(actionId, DummyAction.class);

		ProfileDescriptor profileDesc = new ProfileDescriptor();
		profileDesc.setProfileName(profileName);
		profileDesc.setResourceType(resourceType);

		IProfile profile = new MockProfile(profileDesc);
		profile.addActionSetForCapability(actionSet, capabilityId);

		return profile;
	}

}