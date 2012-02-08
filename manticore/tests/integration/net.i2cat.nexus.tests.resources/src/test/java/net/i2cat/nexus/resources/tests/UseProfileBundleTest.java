package net.i2cat.nexus.resources.tests;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.profile.IProfile;
import org.opennaas.core.resources.profile.IProfileManager;
import org.opennaas.core.resources.profile.ProfileDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.ops4j.pax.exam.util.ServiceLookup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class UseProfileBundleTest { // extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory.getLog(UseProfileBundleTest.class);

	IResourceManager	resourceManager;
	IProfileManager		profileManager;

	@Inject
	BundleContext		bundleContext	= null;

	@Configuration
	public Option[] config() {
		return new Option[] { karafDistributionConfiguration().frameworkUrl("mvn:net.i2cat.mantychore/assembly/1.0.0-SNAPSHOT/zip/bin")
				.karafVersion("2.2.2").name("mantychore") };
	}

	/*
	 * @Configuration public static Option[] configure() {
	 *
	 * Option[] options = combine( IntegrationTestsHelper.getMantychoreTestOptions(),
	 * //mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper"),
	 * mavenBundle().groupId("org.opennaas").artifactId("opennaas-core-tests-mockprofile") // ,
	 * vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005") ); return options; }
	 */

	// @Before
	public void initBundles() {

		waitForAllBundlesActive(bundleContext);

		// resourceManager = getOsgiService(IResourceManager.class, 50000);
		// profileManager = getOsgiService(IProfileManager.class, 30000);

		resourceManager = ServiceLookup.getService(bundleContext, "org.opennaas.core.resources.IResourceManager");
		profileManager = ServiceLookup.getService(bundleContext, "org.opennaas.core.resources.profile.IProfileManager");

		Assert.assertNotNull(profileManager);
		Assert.assertNotNull(resourceManager);

		clearRepo();
		log.info("INFO: Initialized!");
	}

	// @After
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
			// log.info("UseProfileBundleTest: resource. getResourceIdentifier.getId gives us: " + resource.getResourceIdentifier().getId());
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
		// IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 15000);
		IProtocolManager protocolManager = ServiceLookup.getService(bundleContext, "org.opennaas.core.resources.protocol.IProtocolManager");
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());
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

	/**
	 * Wait for all bundles to be active, tries to start non active bundles.
	 */
	private static void waitForAllBundlesActive(BundleContext bundleContext) {

		log.info("Waiting for activation of all bundles");

		int MAX_RETRIES = 20;
		Bundle b = null;
		boolean active = true;

		for (int i = 0; i < MAX_RETRIES; i++) {
			active = true;
			for (int j = 0; j < bundleContext.getBundles().length; j++) {
				Bundle bundle = bundleContext.getBundles()[j];
				if (bundle.getHeaders().get("Fragment-Host") == null &&
					bundle.getState() == Bundle.RESOLVED) {
					active = false;
					try {
						bundleContext.getBundles()[j].start();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			if (active) {
				break;
			}

			listBundles(bundleContext);
			log.info("Waiting for activation of all bundles, this is the " + i + " try. Sleeping for 1 second");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				break;
			}
		}

		listBundles(bundleContext);

		if (active)
			log.info("All the bundles activated. Waiting for 15 seconds more to allow Blueprint to publish all the services into the OSGi registry");
		else
			log.warn("MAX RETRIES REACHED!!! Waiting for 15 seconds more to allow Blueprint to publish all the services into the OSGi registry");

		try {
			Thread.sleep(15000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	private static String listBundles(BundleContext bundleContext) {
		Bundle b = null;
		String listBundles = "";
		for (int i = 0; i < bundleContext.getBundles().length; i++) {
			b = bundleContext.getBundles()[i];
			listBundles += b.toString() + " : " + getStateString(b.getState()) + '\n';
			if (getStateString(b.getState()).equals("INSTALLED")) {
				try {
					b.start();
				} catch (Exception e) {
					listBundles += "ERROR: " + e.getMessage() + '\n';
					e.printStackTrace();
				}
			}
		}
		log.info(listBundles);
		return listBundles;

	}

	private static String getStateString(int value) {
		if (value == Bundle.ACTIVE) {
			return "ACTIVE";
		} else if (value == Bundle.INSTALLED) {
			return "INSTALLED";
		} else if (value == Bundle.RESOLVED) {
			return "RESOLVED";
		} else if (value == Bundle.UNINSTALLED) {
			return "UNINSTALLED";
		}

		return "UNKNOWN";
	}
}
