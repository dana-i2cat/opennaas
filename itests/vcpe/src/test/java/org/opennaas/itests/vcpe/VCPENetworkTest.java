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
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class VCPENetworkTest {

	@Inject
	private IResourceManager	rm;

	@Inject
	@Filter("(type=vcpenet)")
	private IResourceRepository	resourceRepo;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.vcpe)")
	private BlueprintContainer	vcpeBundleBlueprintContainer;

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
	public void resourceWorkflow() throws ResourceException {
		try {
			IResource resource = createResource();
			startResource();
			// createVCPENetScenario(resource);
			// destroyVCPENetScenario(resource);
		} finally {
			rm.destroyAllResources();
		}
	}

	@Test
	public void modelPersistence() throws SerializationException, ResourceException {

		VCPENetworkModel model = VCPENetworkModelHelper.generateSampleModel();

		ResourceDescriptor descriptor = VCPENetworkDescriptorHelper.generateSampleDescriptor(
				resourceName,
				model.toXml());
		try {
			IResource resource = rm.createResource(descriptor);
			rm.startResource(resource.getResourceIdentifier());

			Assert.assertNotNull(resource.getModel());
			Assert.assertEquals(model, resource.getModel());

			String oldValue = ((VCPENetworkModel) resource.getModel()).getTemplateName();
			String changed = "AAABBBCCC";
			((VCPENetworkModel) resource.getModel()).setTemplateName(changed);

			rm.stopResource(resource.getResourceIdentifier());
			rm.startResource(resource.getResourceIdentifier());

			Assert.assertEquals(changed, ((VCPENetworkModel) resource.getModel()).getTemplateName());

			((VCPENetworkModel) resource.getModel()).setTemplateName(oldValue);
			Assert.assertEquals(model, resource.getModel());

		} finally {
			rm.destroyAllResources();
		}

	}

	public IResource createResource() throws ResourceException {

		ResourceDescriptor descriptor = VCPENetworkDescriptorHelper.generateSampleDescriptor(
				resourceName,
				null);
		IResource resource = rm.createResource(descriptor);

		Assert.assertEquals(resourceName, rm.getNameFromResourceID(resource.getResourceIdentifier().getId()));
		Assert.assertEquals(org.opennaas.core.resources.ILifecycle.State.INITIALIZED, resource.getState());

		List<IResource> resources = rm.listResourcesByType(resourceType);
		Assert.assertFalse(resources.isEmpty());

		Assert.assertNotNull(resourceRepo);
		List<IResource> resources1 = resourceRepo.listResources();
		Assert.assertFalse(resources1.isEmpty());

		return resource;

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

	public void createVCPENetScenario(IResource resource) throws ResourceException {

		IVCPENetworkBuilder cap = (IVCPENetworkBuilder) resource.getCapabilityByInterface(IVCPENetworkBuilder.class);
		cap.buildVCPENetwork(VCPENetworkModelHelper.generateSampleModel());

	}

	public void destroyVCPENetScenario(IResource resource) throws ResourceException {

		IVCPENetworkBuilder cap = (IVCPENetworkBuilder) resource.getCapabilityByInterface(IVCPENetworkBuilder.class);
		cap.destroyVCPENetwork();

	}

}
