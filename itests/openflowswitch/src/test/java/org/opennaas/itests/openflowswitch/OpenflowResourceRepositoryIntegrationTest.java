package org.opennaas.itests.openflowswitch;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OpenflowResourceRepositoryIntegrationTest {

	private final static Log	log	= LogFactory.getLog(OpenflowResourceRepositoryIntegrationTest.class);

	@Inject
	private IResourceManager	resourceManager;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflow-switch"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	public void createAndRemoveResourceTest() throws Exception {

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor("openflowswitch");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(ResourceHelper.newQueueCapabilityDescriptor());

		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		IResource resource = resourceManager.createResource(resourceDescriptor);
		Assert.assertFalse(resourceManager.listResources().isEmpty());

		resourceManager.removeResource(resource.getResourceIdentifier());
		Assert.assertTrue(resourceManager.listResources().isEmpty());
	}

}
