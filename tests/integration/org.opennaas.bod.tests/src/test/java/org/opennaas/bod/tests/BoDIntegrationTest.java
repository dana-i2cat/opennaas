package org.opennaas.bod.tests;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.bod.capability.l2bod.L2BoDCapability;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class BoDIntegrationTest extends AbstractIntegrationTest {

	static Log						log					= LogFactory
																.getLog(BoDIntegrationTest.class);
	static ProtocolSessionManager	protocolSessionManager;

	@Inject
	private BundleContext			bundleContext;

	private IResourceManager		resourceManager;

	private static final String		ACTION_NAME			= "dummy";
	private static final String		VERSION				= "1.0";
	private static final String		CAPABILITY_TYPE		= "l2bod";
	private static final String		CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String		RESOURCE_TYPE		= "bod";
	private static final String		RESOURCE_URI		= "user:pass@host.net:2212";
	private static final String		RESOURCE_INFO_NAME	= "BoD Resource";

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
	public void initBundles() throws ResourceException {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		// Get Resource Manager
		resourceManager = getOsgiService(IResourceManager.class, 50000);
		clearRepository();
		log.info("INFO: Initialized!");
	}

	@Test
	public void BoDResourceTest() {

		// Create and start Resource from ResourceManager
		try {

			// L2BoD Capability Descriptor

			List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
			CapabilityDescriptor capabilityDescriptor =
					ResourceHelper.newCapabilityDescriptor(ACTION_NAME, VERSION, CAPABILITY_TYPE, CAPABILITY_URI);
			lCapabilityDescriptors.add(capabilityDescriptor);

			// BoD Resource Descriptor
			ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
					RESOURCE_INFO_NAME);

			// Create resource
			IResource resource = resourceManager.createResource(resourceDescriptor);

			// Start Resource
			resourceManager.startResource(resource.getResourceIdentifier());
			Assert.assertTrue(resource.getState().equals(State.ACTIVE));

			// Get Capabilities
			List<ICapability> CapabilityList = resource.getCapabilities();
			Assert.assertTrue(CapabilityList.size() > 0);

			// Get and Execute Actions
			for (ICapability capability : CapabilityList) {
				Assert.assertTrue(capability.getCapabilityInformation().getType().equals(CAPABILITY_TYPE));

				IActionSet actionSet = ((L2BoDCapability) capability).getActionSet();
				List<String> actionList = actionSet.getActionNames();
				Assert.assertTrue(actionList.size() > 0);

				for (String actionName : actionList) {
					IAction action = actionSet.obtainAction(actionName);
					ActionResponse actionResponse = action.execute(protocolSessionManager);
					Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

				}
			}
			// Stop Resource
			resourceManager.stopResource(resource.getResourceIdentifier());

			// Remove Resource from ResourceManager
			resourceManager.removeResource(resource.getResourceIdentifier());
			Assert.assertTrue(resourceManager.listResources().isEmpty());

		} catch (ResourceException e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			log.error("Exception!! ", e.getCause());
			Assert.fail(e.getMessage());
		} finally {
			clearRepository();
		}

	}

	@After
	public void clearRepository() {

		log.info("Clearing resource repository");

		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			try {
				if (resource.getState().equals(State.ACTIVE)) {
					resourceManager.stopResource(resource.getResourceIdentifier());
				}
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				Assert.fail(e.getLocalizedMessage());
			}
		}
	}

}
