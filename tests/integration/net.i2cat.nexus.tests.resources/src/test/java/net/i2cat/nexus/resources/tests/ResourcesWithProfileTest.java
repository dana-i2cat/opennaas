package net.i2cat.nexus.resources.tests;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.opennaas.core.resources.action.ActionSet;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockAction;
import org.opennaas.core.resources.helpers.MockProfileFactory;
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
// @ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ResourcesWithProfileTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory.getLog(ResourcesWithProfileTest.class);

	IResourceManager	resourceManager;
	IProfileManager		profileManager;

	@Inject
	BundleContext		bundleContext	= null;

	@Configuration
	public Option[] config() {
		// return new
		// Option[]{karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").versionAsInProject()).karafVersion("2.2.4").name("Apache Karaf")};
		// return new
		// Option[]{karafDistributionConfiguration().frameworkUrl("file:/home/rvalles/.m2/repository/net/i2cat/mantychore/assembly/1.0.0-SNAPSHOT/assembly-1.0.0-SNAPSHOT-bin.zip").karafVersion("2.2.4").name("mantychore")};
		return new Option[] { karafDistributionConfiguration().frameworkUrl("mvn:net.i2cat.mantychore/assembly/1.0.0-SNAPSHOT/zip/bin")
				.karafVersion("2.2.2").name("mantychore") };
	}

	/*
	 * @Configuration public Option[] config() { return options(junitBundles(),equinox(),IntegrationTestsHelper.getMantychoreTestOptions()); }
	 */

	/*
	 * public static Option[] configure() { log.warn("HERE HERE HERE LOOK HERE"); Option[] options = combine(
	 * IntegrationTestsHelper.getMantychoreTestOptions(),mavenBundle().groupId("net.i2cat.nexus").artifactId("net.i2cat.nexus.tests.helper") //,
	 * vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005") ); return options; //return
	 * IntegrationTestsHelper.getMantychoreTestOptions(); }
	 */

	/*
	 * public static void main(String[] args) throws IOException { //createContainer(createTestSystem(combine(new
	 * ResourcesWithProfileTest().config(),profile("gogo")))).start(); //createContainer(createTestSystem(new ResourcesWithProfileTest().config())); }
	 */

	// @Before
	public void initBundles() {

		waitForAllBundlesActive(bundleContext);

		resourceManager = ServiceLookup.getService(bundleContext, "org.opennaas.core.resources.IResourceManager");
		profileManager = ServiceLookup.getService(bundleContext, "org.opennaas.core.resources.profile.IProfileManager");

		Assert.assertNotNull(resourceManager);
		Assert.assertNotNull(profileManager);

		clearRepo();

		log.info("INFO: Initialized!");
	}

	// @After
	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);
		int count = 0;
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
				count++;
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
			}
		}

		log.info("Resource repo cleared! Removed " + count + " resources.");
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

			ProfileDescriptor profileDescriptor = ResourceDescriptorFactory.newProfileDescriptor("profile", "router");
			ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("TestResource", "router", capabilities);
			/* specify profiles */
			Map<String, IActionSet> actionSets = new HashMap<String, IActionSet>();
			ActionSet actionSet = new ActionSet();
			actionSet.putAction("setIPv4", MockAction.class);

			actionSets.put("chassis", actionSet);

			IProfile profile = MockProfileFactory.newMockProfilefactory(profileDescriptor, actionSets);

			profileManager.addProfile(profile);

			resourceDescriptor.setProfileId(profile.getProfileName());

			// call createResource(resourceDescriptor)
			IResource resource = resourceManager.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			// assert profile loading has been correct
			Assert.assertNotNull(resource.getProfile());
			Assert.assertTrue(resource.getProfile().equals(profile));

			// // TODO launch setInterface Action and assert DummyAction is executed instead of original one

		} catch (ResourceException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		} catch (ProtocolException e) {
			log.error("Error ocurred!!!", e);
			Assert.fail(e.getMessage());
		} finally {
			clearRepo();
		}

	}

	private void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = ServiceLookup.getService(bundleContext, "org.opennaas.core.resources.protocol.IProtocolManager");
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

	/**
	 * Wait for all bundles to be active, tries to start non active bundles.
	 */
	private static void waitForAllBundlesActive(BundleContext bundleContext) {

		log.info("Waiting for activation of all bundles");

		int MAX_RETRIES = 20;
		Bundle b = null;
		boolean active = true;
		List<Integer> fragments = new ArrayList<Integer>();
		String strBundles;

		for (int i = 0; i < MAX_RETRIES; i++) {
			active = true;
			for (int j = 0; j < bundleContext.getBundles().length; j++) {
				if (!fragments.contains(new Integer(j))) {
					if (bundleContext.getBundles()[j].getState() == Bundle.RESOLVED) {
						active = false;
						try {
							bundleContext.getBundles()[j].start();
						} catch (Exception ex) {
							ex.printStackTrace();
							if (ex.getMessage().indexOf("fragment") != -1) {
								fragments.add(new Integer(j));

								Dictionary headers_dic = bundleContext.getBundles()[j].getHeaders();
								Enumeration headers = headers_dic.keys();
								log.info("Fragment headers:");
								while (headers.hasMoreElements()) {
									log.info(headers.nextElement());
								}
							}
						}
					}
				}
			}

			if (active) {
				break;
			}

			strBundles = listBundles(bundleContext);
			log.info("Waiting for activation of all bundles, this is the " + i + " try. Sleeping for 1 second");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				break;
			}
		}

		strBundles = listBundles(bundleContext);
		String fragmentsNums = "";
		for (Integer num : fragments) {
			fragmentsNums += num.toString() + ", ";
		}
		log.info("Detected " + fragments.size() + " fragments: " + fragmentsNums);

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
