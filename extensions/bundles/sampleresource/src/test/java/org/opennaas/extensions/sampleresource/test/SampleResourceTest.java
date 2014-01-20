package org.opennaas.extensions.sampleresource.test;

/*
 * #%L
 * OpenNaaS :: Sample Resource
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

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.extensions.sampleresource.capability.example.ExampleCapability;
import org.opennaas.extensions.sampleresource.capability.example.IExampleCapability;
import org.opennaas.extensions.sampleresource.repository.SampleResourceBootstrapper;
import org.opennaas.extensions.sampleresource.repository.SampleResourceBootstrapperFactory;

public class SampleResourceTest {

	private final static String	resourceType	= "sampleresource";

	private final static String	name			= "OpenNaaS";

	@Test
	public void SampleResourceBootrapperTest() {

		SampleResourceBootstrapperFactory factory = new SampleResourceBootstrapperFactory();

		IResourceBootstrapper bootstrapper = factory.createResourceBootstrapper();
		Assert.assertNotNull(bootstrapper);
		Assert.assertTrue(bootstrapper instanceof SampleResourceBootstrapper);

	}

	@Test
	public void ExampleCapabilityTest() throws CapabilityException {
		IExampleCapability capab = new ExampleCapability(sampleDescriptor(), null);
		String greetings = capab.sayHello(name);
		Assert.assertNotNull(greetings);
		Assert.assertEquals("Hello " + name, greetings);
	}

	private CapabilityDescriptor sampleDescriptor() {

		Information capabilityInformation = new Information();
		capabilityInformation.setType(resourceType);
		
		
		CapabilityDescriptor descriptor = new CapabilityDescriptor();
		descriptor.setCapabilityInformation(capabilityInformation);

		return descriptor;
	}
}
