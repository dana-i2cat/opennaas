package net.i2cat.nexus.resources.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
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

	static Log			log				= LogFactory.getLog(UseProfileBundleTest.class);

	IResourceManager	resourceManager;
	IProfileManager		profileManager;

	@Inject
	BundleContext		bundleContext	= null;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper"),
				/*mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.mockprofile")*/
				mavenBundle().groupId("org.opennaas").artifactId("opennaas-core-tests-mockprofile")
							// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
							);
		return options;
	}

	//@Before
	public void initBundles() {
		
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		resourceManager = getOsgiService(IResourceManager.class, 50000);
		profileManager = getOsgiService(IProfileManager.class, 30000);

		clearRepo();

		log.info("INFO: Initialized!");
	}

	//@After
	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {

			if (resource.getState().equals(State.ACTIVE)) {
				try {
					resourceManager.stopResource(resource.getResourceIdentifier());
				} catch (ResourceException e) {
					log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				}
			}
			try {
				resourceManager.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
			}
		}

		log.info("Resource repo cleared!");
	}

	/**
	 * Creates a resource indicating a profileId in its descriptor. Checks profile is loaded correctly and profile actions are called instead of
	 * original ones.
	 */
	@Test
	public void createResourceWithProfile() {

		initBundles();

		try {

			List<String> capabilities = new ArrayList<String>();
			capabilities.add("chassis");
			capabilities.add("queue");

			// put profile in profileManager
			List<ProfileDescriptor> profileDescriptors = profileManager.listProfiles();
			Assert.assertFalse(profileDescriptors.isEmpty());
			Assert.assertTrue("TestProfile".equals(profileDescriptors.get(0).getProfileName()));

			log.info("Found profile with name: " + profileDescriptors.get(0).getProfileName());
			IProfile profile = profileManager.getProfile(profileDescriptors.get(0).getProfileName());

			// create resourceDescriptor with profile id

			ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("TestResource", "router", capabilities);
			resourceDescriptor.setProfileId(profile.getProfileName());

			// call createResource(resourceDescriptor)
			IResource resource = resourceManager.createResource(resourceDescriptor);
			createProtocolForResource(resourceDescriptor.getId());
			//log.info("UseProfileBundleTest: resource. getResourceIdentifier.getId gives us: " + resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// assert profile loading has been correct
			Assert.assertNotNull(resource.getProfile());
			Assert.assertNotNull(profile);
			// FIXME there are problems with CGLIB used by hibernate and equals()
			// Assert.assertTrue(resource.getProfile().equals(profile));
			Assert.assertTrue(resource.getProfile().getProfileName().equals(profile.getProfileName()));
			Assert.assertTrue(resource.getProfile().getProfileDescriptor().equals(profile.getProfileDescriptor()));

			// TODO launch setInterface Action and assert DummyAction is executed instead of original one

		} catch (ResourceException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}
		// catch (ProtocolException e) {
		// log.error("Error ocurred!!!", e);
		// Assert.fail(e.getMessage());
		// }
	}

	private void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 15000);
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
