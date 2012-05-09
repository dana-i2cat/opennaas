package org.opennaas.extensions.bod.repository.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.mock.MockCapabilityFactory;
import org.opennaas.core.resources.mock.MockDescriptorRepository;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.extensions.bod.repository.BoDRepository;

public class BoDRepositoryTest {
	BoDRepository	bodRepository;
	String			resourceType	= "bod";

	@Test
	public void testCapabilityFactories() {

		bodRepository = new BoDRepository("bodrerepository");
		bodRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		bodRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		bodRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		Assert.assertTrue(bodRepository.getCapabilityFactories().size() == 2);
		Assert.assertNotNull(bodRepository.getCapabilityFactories().get("factory1"));
		Assert.assertNotNull(bodRepository.getCapabilityFactories().get("factory2"));
	}

	@Test
	public void testCreateRemoveResources() {

		bodRepository = new BoDRepository("bod");
		bodRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		bodRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		bodRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		try {
			/* type of resources */

			ResourceDescriptor descriptor = newResourceDescriptor("bod", "bod1");
			IResource resource = bodRepository.createResource(descriptor);
			String id = resource.getResourceIdentifier().getId();
			Assert.assertNotNull(id);
			Assert.assertTrue(resource.getResourceDescriptor().equals(descriptor));
			Assert.assertTrue(resource.getResourceIdentifier().getType().equals("bod"));

			boolean thrown = false;
			bodRepository.removeResource(id);
			try {
				bodRepository.getResource(id);
			} catch (Exception e) {
				thrown = true;
			}
			Assert.assertTrue(thrown);

		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testModifyResources() {

		bodRepository = new BoDRepository("bod");
		bodRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		bodRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		bodRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		try {
			/* type of resources */
			IResource resource = bodRepository.createResource(newResourceDescriptor("bod", "bod1"));
			String id = resource.getResourceIdentifier().getId();
			Assert.assertTrue(resource.getResourceDescriptor().getCapabilityDescriptors().size() == 2);
			Assert.assertTrue(resource.getResourceIdentifier().getType().equals("bod"));

			resource = bodRepository.modifyResource(id, oldResourceDescriptor(id, "bod", "bod1"));
			IResource newResource = bodRepository.getResource(resource.getResourceIdentifier().getId());
			Assert.assertTrue(newResource.getResourceDescriptor().getCapabilityDescriptors().size() == 1);
			Assert.assertTrue(newResource.getResourceIdentifier().getType().equals("bod"));

		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private ResourceDescriptor newResourceDescriptor(String type, String name) {

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		Map<String, String> properties = new HashMap<String, String>();

		/* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		/* factory1 descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory1", "go_figure_what_this_does"));

		/* factory2 descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory2", "go_figure_what_this_does"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId("JunosTest");
		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName(name);
		resourceDescriptor.setInformation(information);
		return resourceDescriptor;
	}

	private ResourceDescriptor oldResourceDescriptor(String resourceId, String type, String name) {

		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();

		Map<String, String> properties = new HashMap<String, String>();

		/* FIXME PUT PROTOCOL_URI IN RESOURCE DESCRIPTOR CONSTANTS */
		properties.put(ResourceDescriptorConstants.PROTOCOL_URI,
				"user:pass@host.net:2212");

		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		/* factory1 descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory1", "go_figure_what_this_does"));

		resourceDescriptor.setProperties(properties);
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);
		resourceDescriptor.setId(resourceId);

		/* information. It is only necessary to add type */
		Information information = new Information();
		information.setType(type);
		information.setName(name);
		resourceDescriptor.setInformation(information);
		return resourceDescriptor;
	}

}
