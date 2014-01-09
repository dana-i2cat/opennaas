package org.opennaas.extensions.sampleresource.test;

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
