package net.i2cat.mantychore.commandskaraf;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.KarafCommandHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
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
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class ProfileCommandsKarafTest
{
	static Log log = LogFactory.getLog(ProfileCommandsKarafTest.class);

	@Inject
	@Filter("(type=router)")
	private IResourceRepository	repository;

	@Inject
	private IProfileManager		profileManager;

	@Inject
	private CommandProcessor	commandprocessor;

	@Inject
	private IProtocolManager	protocolManager;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer	routerRepositoryService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.ip)")
    private BlueprintContainer	ipService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer	queueService;

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
					   keepRuntimeFolder());
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
		return protocolSessionContext;
	}

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	@Test
	public void testListInfoAndRemoveProfileCommand() throws Exception {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource2", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setInterface", "ipv4", "router");
		IProfile profile2 = createProfile("profile2", "setInterface", "ipv4", "router");

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
	}

	@Test
	public void testActionIsOverriden() throws Exception {

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource7", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		IProfile profile1 = createProfile("profile1", "setIPv4", "ipv4", "router");

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