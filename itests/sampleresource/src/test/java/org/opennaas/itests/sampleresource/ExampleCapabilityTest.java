package org.opennaas.itests.sampleresource;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.extensions.sampleresource.capability.example.IExampleCapability;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ExampleCapabilityTest {

	@Inject
	protected IResourceManager	resourceManager;

	private IResource			sampleResource;

	private final static String	RESOURCE_TYPE			= "sampleresource";
	private final static String	SAMPLE_CAPABILITY_TYPE	= "example";
	private final static String	CAPABILITY_IMPL_VERSION	= "1.0";
	private final static String	CAPABILITY_IMPL_NAME	= "dummy";

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-core", "opennaas-sampleresource", "itests-helpers"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	public void sampleTest() throws CapabilityException {
		Assert.assertNotNull(sampleResource.getCapabilities());
		Assert.assertEquals(1, sampleResource.getCapabilities().size());

		ICapability capab = sampleResource.getCapabilities().get(0);
		Assert.assertTrue(capab instanceof IExampleCapability);

		IExampleCapability sampleCapability = (IExampleCapability) capab;
		String greetings = sampleCapability.sayHello("OpenNaaS");
		Assert.assertEquals("Hello OpenNaaS", greetings);
	}

	@Before
	public void prepareTest() throws ResourceException {
		startResource();
	}

	private void startResource() throws ResourceException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		CapabilityDescriptor exampleCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(CAPABILITY_IMPL_NAME,
				CAPABILITY_IMPL_VERSION, SAMPLE_CAPABILITY_TYPE, "mock://user:pass@host.net:2212/mocksubsystem");
		lCapabilityDescriptors.add(exampleCapabilityDescriptor);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, RESOURCE_TYPE,
				"mock://user:pass@host.net:2212/mocksubsystem", "sample-resource");

		sampleResource = resourceManager.createResource(resourceDescriptor);
		resourceManager.startResource(sampleResource.getResourceIdentifier());

	}

	@After
	public void revertTest() throws ResourceException {
		resourceManager.stopResource(sampleResource.getResourceIdentifier());
		resourceManager.removeResource(sampleResource.getResourceIdentifier());

	}
}
