package org.opennaas.bod.tests.capability.l2bod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class L2BoDCapabilityIntegrationTest
{
	private static final Log	log					= LogFactory.getLog(L2BoDCapabilityIntegrationTest.class);

	private static final String	ACTION_NAME			= "dummy";

	private static final String	VERSION				= "1.0";

	private static final String	CAPABILIY_TYPE		= "l2bod";

	private static final String	CAPABILITY_URI		= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	RESOURCE_TYPE		= "bod";

	private static final String	RESOURCE_URI		= "user:pass@host.net:2212";

	private static final String	RESOURCE_INFO_NAME	= "L2BoD Test";

	@Inject
	private BundleContext		bundleContext;

	@Inject
	private IResourceManager	resourceManager;

	private ICapability			l2bodCapability;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-bod"),
					   includeTestHelper(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	/**
	 * Test to check if repostitory is accessible from OSGi.
	 */
	@Test
	public void isCapabilityAccessibleFromResource() throws Exception {

		// L2BoD Capability Descriptor
		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		CapabilityDescriptor capabilityDescriptor = ResourceHelper.newCapabilityDescriptor(ACTION_NAME, VERSION, CAPABILIY_TYPE,
																						   CAPABILITY_URI);
		lCapabilityDescriptors.add(capabilityDescriptor);

		// BoD Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE, RESOURCE_URI,
																					 RESOURCE_INFO_NAME);

		// Create resource
		IResource resource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(resource.getResourceIdentifier());
		Assert.assertTrue(resource.getCapabilities().size() > 0);

		// Stop resource
		resourceManager.stopResource(resource.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

	/**
	 * At the end of the tests, we empty the repository
	 */
	@After
	public void clearRepository() throws ResourceException {

		log.info("Clearing resource repo");

		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

		log.info("Resource repo cleared!");
	}
}
