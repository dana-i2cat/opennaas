package net.i2cat.nexus.resources.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.ActionSet;
import net.i2cat.nexus.resources.action.IActionSet;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.profile.IProfile;
import net.i2cat.nexus.resources.profile.IProfileManager;
import net.i2cat.nexus.resources.profile.ProfileDescriptor;
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
public class ResourcesWithProfileTest extends AbstractIntegrationTest {
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
						"net.i2cat.nexus.tests.helper")
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

		clearRepo();

		log.info("INFO: Initialized!");
	}

	@After
	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceRepository.listResources().size()];
		toRemove = resourceRepository.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			try {
				resourceRepository.stopResource(resource.getResourceIdentifier().getId());
				resourceRepository.removeResource(resource.getResourceIdentifier().getId());
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

		try {

			CapabilityDescriptor capDesc = RepositoryHelper.newChassisCapabilityDescriptor();

			// put profile in profileManager
			IProfile profile = createProfile("aProfile", "setInterface", String.valueOf(capDesc.getId()), "router");
			profileManager.addProfile(profile);

			// create resourceDescriptor with profile id
			ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router");
			resourceDescriptor.getInformation().setName("TestResource");
			resourceDescriptor.setProfileId(profile.getProfileName());

			List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
			capabilityDescriptors.add(capDesc);
			capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());

			resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

			// call createResource(resourceDescriptor)
			IResource resource = resourceRepository.createResource(resourceDescriptor);
			resourceRepository.startResource(resource.getResourceIdentifier().getId());

			// assert profile loading has been correct
			Assert.assertNotNull(resource.getProfile());
			Assert.assertTrue(resource.getProfile().equals(profile));

			// TODO launch setInterface Action and assert DummyAction is executed instead of original one
			ICapability capability = resource.getCapability(capDesc.getCapabilityInformation());

		} catch (ResourceException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}

	private IProfile createProfile(String profileName, String actionId, String capabilityId, String resourceType) {

		ActionSet actionSet = new ActionSet();

		actionSet.setActionSetId("aProfileActionSet");
		actionSet.putAction(actionId, DummyAction.class);

		HashMap<String, IActionSet> overridenActions = new HashMap<String, IActionSet>();
		overridenActions.put(capabilityId, actionSet);

		ProfileDescriptor profileDesc = new ProfileDescriptor();
		profileDesc.setProfileName(profileName);
		profileDesc.setResourceType(resourceType);

		IProfile profile = new MockProfile(profileDesc);
		profile.addActionSetForCapability(actionSet, capabilityId);

		return profile;

	}

}
