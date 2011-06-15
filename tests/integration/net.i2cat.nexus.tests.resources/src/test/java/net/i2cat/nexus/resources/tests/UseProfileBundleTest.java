package net.i2cat.nexus.resources.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class UseProfileBundleTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory.getLog(ResourcesWithProfileTest.class);

	IResourceRepository	resourceRepository;
	IProfileManager		profileManager;

	@Inject
	BundleContext		bundleContext	= null;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper"),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.mockProfile")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	@Before
	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		resourceRepository = getOsgiService(IResourceRepository.class, 20000);
		profileManager = getOsgiService(IProfileManager.class, 20000);

		log.info("INFO: Initialized!");
	}

	/**
	 * Creates a resource indicating a profileId in its descriptor. Checks profile is loaded correctly and profile actions are called instead of
	 * original ones.
	 */
	@Test
	public void createResourceWithProfile() {

		try {

			CapabilityDescriptor capDesc = RepositoryHelper.newChassisCapabilityDescriptor();

			// put profile in profileManager
			List<ProfileDescriptor> profileDescriptors = profileManager.listProfiles();
			Assert.assertFalse(profileDescriptors.isEmpty());
			Assert.assertTrue("TestProfile".equals(profileDescriptors.get(0).getProfileName()));

			log.info("Found profile with name: " + profileDescriptors.get(0).getProfileName());
			IProfile profile = profileManager.getProfile(profileDescriptors.get(0).getProfileName());

			// create resourceDescriptor with profile id
			ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");
			resourceDescriptor.setProfileId(profile.getProfileName());

			List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
			capabilityDescriptors.add(capDesc);
			capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

			resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

			// call createResource(resourceDescriptor)
			IResource resource = resourceRepository.createResource(resourceDescriptor);

			// assert profile loading has been correct
			Assert.assertTrue(resource.getProfile().equals(profile));

			// TODO launch setInterface Action and assert DummyAction is executed instead of original one

		} catch (ResourceException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		}
	}

	private void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

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

}
