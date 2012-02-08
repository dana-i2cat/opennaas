package net.i2cat.mantychore.commandskaraf;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockProfile;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
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
public class ProfileCommandsKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log					log	= LogFactory
											.getLog(ProfileCommandsKarafTest.class);

	IResourceRepository			repository;
	IProfileManager				profileManager;

	private CommandProcessor	commandprocessor;

	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(IntegrationTestsHelper.FELIX_CONTAINER),
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
				return TinyBundles.modifyBundle(testProbe).set(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional")
						.build();
			}
		});
	}

	public void initBundles() {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		repository = getOsgiService(IResourceRepository.class, "type=router", 50000);
		profileManager = getOsgiService(IProfileManager.class, 25000);
		commandprocessor = getOsgiService(CommandProcessor.class);
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

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	@Test
	public void testListInfoAndRemoveProfileCommand() {

		initBundles();

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource2", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setInterface", "ipv4", "router");
		IProfile profile2 = createProfile("profile2", "setInterface", "ipv4", "router");

		try {

			profileManager.addProfile(profile1);

			KarafCommandHelper.executeCommand("profile:list", commandprocessor);
			KarafCommandHelper.executeCommand("profile:info " + profile1.getProfileName(), commandprocessor);

			profileManager.addProfile(profile2);
			KarafCommandHelper.executeCommand("profile:list", commandprocessor);

			// add resource with profile1
			resourceDescriptor.setProfileId(profile1.getProfileName());
			IResource resource = repository.createResource(resourceDescriptor);

			createProtocolForResource(resource.getResourceDescriptor().getId());

			repository.startResource(resource.getResourceDescriptor().getId());

			KarafCommandHelper.executeCommand("profile:list", commandprocessor);
			KarafCommandHelper.executeCommand("profile:info " + profile1.getProfileName(), commandprocessor);

			KarafCommandHelper.executeCommand("profile:remove " + profile2.getProfileName(), commandprocessor);
			KarafCommandHelper.executeCommand("profile:list", commandprocessor);

			// there is no such profile, but it does not fail for that ;)
			// it silently does nothing
			KarafCommandHelper.executeCommand("profile:remove " + profile2.getProfileName(), commandprocessor);

			boolean failed = false;
			try {
				KarafCommandHelper.executeCommand("profile:remove " + profile1.getProfileName(), commandprocessor);
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

			KarafCommandHelper.executeCommand("profile:remove " + profile1.getProfileName(), commandprocessor);
			KarafCommandHelper.executeCommand("profile:list", commandprocessor);

			// there is no such profile, but it does not fail for that ;)
			// it silently does nothing
			KarafCommandHelper.executeCommand("profile:info " + profile1.getProfileName(), commandprocessor);

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
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource7", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setIPv4", "ipv4", "router");

		try {
			profileManager.addProfile(profile1);

			// add resource with profile1
			resourceDescriptor.setProfileId(profile1.getProfileName());
			IResource resource = repository.createResource(resourceDescriptor);

			createProtocolForResource(resource.getResourceDescriptor().getId());

			// launch setInterface Action and assert DummyAction is executed instead of original one
			repository.startResource(resource.getResourceIdentifier().getId());

			ArrayList<String> response = KarafCommandHelper.executeCommand("ipv4:list " + resourceFriendlyID, commandprocessor);

			// assert command output contains no ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("ipv4:setIP  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0",
					commandprocessor);
			// assert command output contains no ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("ipv4:list " + resourceFriendlyID, commandprocessor);
			// assert command output contains no ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

		} catch (ResourceException e) {

			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {

			Assert.fail(e.getMessage());
		} catch (Exception e) {
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