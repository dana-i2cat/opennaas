package org.opennaas.itests.vcpe;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.vcpe.helper.VCPENetworkDescriptorHelper;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class VCPENetworkTest {

	@Inject
	private IResourceManager	rm;

	@Inject
	@Filter("type=vcpe")
	private IResourceRepository	resourceRepo;

	private String				resourceName	= "vcpenet1";
	private String				resourceType	= "vcpenet";

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-vcpe"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Test
	public void resourceWorkflow()
			throws InterruptedException, ResourceException, SerializationException
	{
		createResource();
		startResource();

		rm.destroyAllResources();
	}

	public void createResource() throws ResourceException, SerializationException {

		ResourceDescriptor descriptor = VCPENetworkDescriptorHelper.generateSampleDescriptor(
				resourceName,
				VCPENetworkModelHelper.generateSampleModel().toXml());
		IResource resource = rm.createResource(descriptor);

		Assert.assertEquals(resourceName, rm.getNameFromResourceID(resource.getResourceIdentifier().getId()));
		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

		List<IResource> resources = rm.listResourcesByType(resourceType);
		Assert.assertFalse(resources.isEmpty());

		Assert.assertNotNull(resourceRepo);
		List<IResource> resources1 = resourceRepo.listResources();
		Assert.assertFalse(resources1.isEmpty());

	}

	public void startResource() throws ResourceException {
		Assert.assertNotNull(resourceRepo);
		List<IResource> resources = resourceRepo.listResources();
		Assert.assertFalse(resources.isEmpty());
		List<IResource> resources1 = rm.listResourcesByType(resourceType);
		Assert.assertFalse(resources1.isEmpty());

		IResource resource = rm.getResource(rm.getIdentifierFromResourceName(resourceType, resourceName));
		Assert.assertNotNull(resource);
		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

		rm.startResource(resource.getResourceIdentifier());

		resource = rm.getResource(rm.getIdentifierFromResourceName(resourceType, resourceName));
		Assert.assertNotNull(resource);

		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.ACTIVE, resource.getState());
	}

}
