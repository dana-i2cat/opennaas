package org.opennaas.itests.sampleresource;

/*
 * #%L
 * org.opennaas.itests.sampleresource
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
public class ExampleCapabilityTest {

	@Inject
	protected IResourceManager	resourceManager;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.sampleresource)", timeout = 50000)
	private BlueprintContainer	sampleResourceBlueprintContainer;

	private IResource			sampleResource;

	private final static String	RESOURCE_TYPE			= "sampleresource";
	private final static String	SAMPLE_CAPABILITY_TYPE	= "example";
	private final static String	CAPABILITY_IMPL_VERSION	= "1.0";
	private final static String	CAPABILITY_IMPL_NAME	= "dummy";

	@Configuration
	public static Option[] configuration() {

		return new Option[] {
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-core", "opennaas-sampleresource", "itests-helpers"),
				OpennaasExamOptions.noConsole(),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder()
		};
	}

	@Test
	public void sampleTest() throws CapabilityException {
		Assert.assertNotNull(sampleResource.getCapabilities());
		Assert.assertEquals(1,
				sampleResource.getCapabilities().size());

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

		CapabilityDescriptor exampleCapabilityDescriptor = ResourceHelper.newCapabilityDescriptor(CAPABILITY_IMPL_NAME, CAPABILITY_IMPL_VERSION,
				SAMPLE_CAPABILITY_TYPE, "mock://user:pass@host.net:2212/mocksubsystem");
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
