package org.opennaas.itests.vcpe;

/*
 * #%L
 * OpenNaaS :: iTests :: VCPENetwork
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.vcpe.helper.VCPENetworkDescriptorHelper;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.capability.builder.VCPENetworkBuilderCapability;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class VCPENetworkTest {

	@Inject
	private IResourceManager	rm;

	@Inject
	@Filter("(type=vcpenet)")
	private IResourceRepository	resourceRepo;

	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.vcpe)", timeout = 20000)
	private BlueprintContainer	vcpeBundleBlueprintContainer;

	private String				resourceName	= "vcpenet1";
	private String				resourceType	= "vcpenet";

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-vcpe"),
				OpennaasExamOptions.noConsole(),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
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

			String oldValue = ((VCPENetworkModel) resource.getModel()).getTemplateType();
			String changed = "AAABBBCCC";
			((VCPENetworkModel) resource.getModel()).setTemplateType(changed);

			rm.stopResource(resource.getResourceIdentifier());
			rm.startResource(resource.getResourceIdentifier());

			Assert.assertEquals(changed, ((VCPENetworkModel) resource.getModel()).getTemplateType());

			((VCPENetworkModel) resource.getModel()).setTemplateType(oldValue);
			Assert.assertEquals(model, resource.getModel());

		} finally {
			rm.destroyAllResources();
		}

	}

	private IResource createResource() throws ResourceException {

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

	private void startResource() throws ResourceException {
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

	private void createVCPENetScenario(IResource resource) throws ResourceException {

		IVCPENetworkBuilderCapability cap = (VCPENetworkBuilderCapability) resource.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
		cap.buildVCPENetwork(VCPENetworkModelHelper.generateSampleModel());

	}

	private void destroyVCPENetScenario(IResource resource) throws ResourceException {

		IVCPENetworkBuilderCapability cap = (IVCPENetworkBuilderCapability) resource.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
		cap.destroyVCPENetwork();

	}

}
