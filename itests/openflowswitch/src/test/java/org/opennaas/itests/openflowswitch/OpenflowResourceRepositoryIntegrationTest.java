package org.opennaas.itests.openflowswitch;

/*
 * #%L
 * OpenNaaS :: iTests :: Openflow Switch
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
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class OpenflowResourceRepositoryIntegrationTest {

	private final static Log	log	= LogFactory.getLog(OpenflowResourceRepositoryIntegrationTest.class);

	/**
	 * Make sure blueprint for org.opennaas.extensions.opernflowswitch bundle has finished its initialization
	 */
	@SuppressWarnings("unused")
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.openflowswitch)", timeout = 20000)
	private BlueprintContainer	blueprintContainer;

	@Inject
	private IResourceManager	resourceManager;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-openflowswitch", "itests-helpers"),
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
