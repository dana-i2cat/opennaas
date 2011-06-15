package net.i2cat.mantychore.commandskaraf;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.ActionSet;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.profile.IProfileManager;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

@RunWith(JUnit4TestRunner.class)
public class ProfileCommandsKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log	= LogFactory
									.getLog(ProfileCommandsKarafTest.class);

	IResourceRepository	repository;
	IProfileManager		profileManager;

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

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		repository = getOsgiService(IResourceRepository.class, 50000);
		profileManager = getOsgiService(IProfileManager.class, 25000);

		log.info("INFO: Initialized!");

	}

	public String capture() throws IOException {
		StringWriter sw = new StringWriter();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		String s = rdr.readLine();
		while (s != null) {
			sw.write(s);
			s = rdr.readLine();
		}
		return sw.toString();
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

	public Object executeCommand(String command) throws Exception {
		// Run some commands to make sure they are installed properly
		CommandProcessor cp = getOsgiService(CommandProcessor.class);
		CommandSession cs = cp.createSession(System.in, System.out, System.err);
		Object commandOutput = null;
		try {
			commandOutput = cs.execute(command);
			return commandOutput;
		} catch (IllegalArgumentException e) {
			Assert.fail("Action should have thrown an exception because: " + e.toString());
		} catch (NoSuchMethodException a) {
			log.error("Method for command not found: " + a.getLocalizedMessage());
			Assert.fail("Method for command not found.");
		}

		cs.close();
		return commandOutput;
	}

	@Test
	public void testListInfoAndRemoveProfileCommand() {

		initBundles();

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router", "resource2");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor chassisDescriptor = RepositoryHelper.newChassisCapabilityDescriptor();
		CapabilityDescriptor queueDescriptor = RepositoryHelper.newQueueCapabilityDescriptor();
		capabilityDescriptors.add(chassisDescriptor);
		capabilityDescriptors.add(queueDescriptor);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		IProfile profile1 = createProfile("profile1", "setInterface", String.valueOf(chassisDescriptor.getId()), "router");
		IProfile profile2 = createProfile("profile2", "setInterface", String.valueOf(chassisDescriptor.getId()), "router");

		try {

			profileManager.addProfile(profile1);

			executeCommand("profile:list");
			executeCommand("profile:info " + profile1.getProfileName());

			profileManager.addProfile(profile2);
			executeCommand("profile:list");

			// add resource with profile1
			resourceDescriptor.setProfileId(profile1.getProfileName());
			IResource resource = repository.createResource(resourceDescriptor);
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

		} catch (ResourceException e1) {
			log.error("ResourceException ocurred!!!", e1);
			Assert.fail(e1.getMessage());
		} catch (Exception e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testActionIsOverriden() {

		initBundles();

		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router", "resource7");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor chassisDescriptor = RepositoryHelper.newChassisCapabilityDescriptor();
		CapabilityDescriptor queueDescriptor = RepositoryHelper.newQueueCapabilityDescriptor();
		capabilityDescriptors.add(chassisDescriptor);
		capabilityDescriptors.add(queueDescriptor);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setInterface", chassisDescriptor.getCapabilityInformation().getType(), "router");

		try {
			profileManager.addProfile(profile1);

			// add resource with profile1
			resourceDescriptor.setProfileId(profile1.getProfileName());
			IResource resource = repository.createResource(resourceDescriptor);

			// launch setInterface Action and assert DummyAction is executed instead of original one
			repository.startResource(resource.getResourceIdentifier().getId());

			createProtocolForResource(resource.getResourceIdentifier().getId());

			log.info("---------------------------------------------");
			log.info("executeCommand(chasis:listInterfaces " + resourceFriendlyID);
			Integer response = (Integer) executeCommand("chasis:listInterfaces " + resourceFriendlyID);
			if (response != null)
				Assert.fail("Error in the listInterfaces command");

			log.info("executeCommand(chasis:setInterface " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0)");
			response = (Integer) executeCommand("chasis:setInterface " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0");
			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the setInterfaces command");

			log.info("executeCommand(chasis:listInterfaces " + resourceFriendlyID);
			response = (Integer) executeCommand("chasis:listInterfaces " + resourceFriendlyID);
			if (response != null)
				Assert.fail("Error in the listInterfaces command");

			log.info("---------------------------------------------");
			log.info("---------------------------------------------");
			log.info("---------------------------------------------");

		} catch (ResourceException e) {
			log.error("ResourceException ocurred!!!", e);
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			log.error("ProtocolException ocurred!!!", e);
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			log.error("Exception ocurred while executing setInterface command!!!", e);
			Assert.fail(e.getMessage());
		}

	}

	private IProfile createProfile(String profileName, String actionId, String capabilityId, String resourceType) {

		ActionSet actionSet = new ActionSet();

		actionSet.setActionSetId("aProfileActionSet");
		actionSet.putAction(actionId, DummyAction.class);

		// HashMap<String, IActionSet> overridenActions = new HashMap<String, IActionSet>();
		// overridenActions.put(capabilityId, actionSet);

		ProfileDescriptor profileDesc = new ProfileDescriptor();
		profileDesc.setProfileName(profileName);
		profileDesc.setResourceType(resourceType);

		IProfile profile = new MockProfile(profileDesc);
		profile.addActionSetForCapability(actionSet, capabilityId);

		return profile;

	}

}