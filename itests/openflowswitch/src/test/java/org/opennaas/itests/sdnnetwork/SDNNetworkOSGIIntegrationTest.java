package org.opennaas.itests.sdnnetwork;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.OFProvisioningNetworkCapability;
import org.opennaas.extensions.sdnnetwork.driver.internal.actionsets.SDNNetworkInternalActionsetImplementation;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class SDNNetworkOSGIIntegrationTest {

	private final static Log	log	= LogFactory.getLog(SDNNetworkOSGIIntegrationTest.class);

	/**
	 * Make sure blueprint for org.opennaas.extensions.sdnnetwork bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.sdnnetwork)", timeout = 20000)
	private BlueprintContainer	sdnNetworkBlueprintContainer;

	@Inject
	private IResourceManager	resourceManager;

	@Inject
	@Filter("(type=sdnnetwork)")
	private IResourceRepository	sdnNetworkRepository;

	@Inject
	@Filter("(capability=ofprovisionnet)")
	private ICapabilityFactory	capabilityFactory;

	@Inject
	@Filter("(&(actionset.name=internal)(actionset.capability=ofprovisionnet))")
	private IActionSet			actionset;

	private ResourceDescriptor	resourceDescriptor;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-sdn-network", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initializeDescriptor() {
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("sdnnetwork");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newCapabilityDescriptor(SDNNetworkInternalActionsetImplementation.ACTIONSET_ID, "1.0.0",
				OFProvisioningNetworkCapability.CAPABILITY_TYPE, null));
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		this.resourceDescriptor = resourceDescriptor;
	}

	@Before
	@After
	public void clearRM() throws ResourceException {
		resourceManager.destroyAllResources();
	}

	@Test
	public void resourceWorkflowTest() throws Exception {

		IResource resource = resourceManager.createResource(resourceDescriptor);
		Assert.assertEquals(State.INITIALIZED, resource.getState());
		Assert.assertFalse(resourceManager.listResources().isEmpty());

		resourceManager.startResource(resource.getResourceIdentifier());
		Assert.assertEquals(State.ACTIVE, resource.getState());

		// retrieve capability, will throw exception if unable
		resource.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

		resourceManager.stopResource(resource.getResourceIdentifier());
		Assert.assertEquals(State.INITIALIZED, resource.getState());

		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

}
