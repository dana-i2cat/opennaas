package org.opennaas.extensions.router.tests;

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
import org.opennaas.extensions.router.repository.MantychoreRepository;

public class MantychoreRepositoryTest {
	MantychoreRepository	mantychoreRepository;
	String					resourceType	= "router";

	@Test
	public void testCapabilityFactories() {
		mantychoreRepository = new MantychoreRepository("mantychorerepository");
		mantychoreRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		mantychoreRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		mantychoreRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		Assert.assertTrue(mantychoreRepository.getCapabilityFactories().size() == 2);
		Assert.assertNotNull(mantychoreRepository.getCapabilityFactories().get("factory1"));
		Assert.assertNotNull(mantychoreRepository.getCapabilityFactories().get("factory2"));

	}

	@Test
	public void testCreateRemoveResources() {
		mantychoreRepository = new MantychoreRepository("router");
		mantychoreRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		mantychoreRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		mantychoreRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		try {
			/* type of resources */

			ResourceDescriptor descriptor = newResourceDescriptor("router", "router1");
			IResource resource = mantychoreRepository.createResource(descriptor);
			String id = resource.getResourceIdentifier().getId();
			Assert.assertNotNull(id);
			Assert.assertTrue(resource.getResourceDescriptor().equals(descriptor));
			Assert.assertTrue(resource.getResourceIdentifier().getType().equals("router"));

			boolean thrown = false;
			mantychoreRepository.removeResource(id);
			try {
				mantychoreRepository.getResource(id);
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

		mantychoreRepository = new MantychoreRepository("router");
		mantychoreRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		mantychoreRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		mantychoreRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		try {
			/* type of resources */
			IResource resource = mantychoreRepository.createResource(newResourceDescriptor("router", "router1"));
			String id = resource.getResourceIdentifier().getId();
			Assert.assertTrue(resource.getResourceDescriptor().getCapabilityDescriptors().size() == 2);
			Assert.assertTrue(resource.getResourceIdentifier().getType().equals("router"));

			resource = mantychoreRepository.modifyResource(id, oldResourceDescriptor(id, "router", "router1"));
			IResource newResource = mantychoreRepository.getResource(resource.getResourceIdentifier().getId());
			Assert.assertTrue(newResource.getResourceDescriptor().getCapabilityDescriptors().size() == 1);
			Assert.assertTrue(newResource.getResourceIdentifier().getType().equals("router"));

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
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory1"));

		/* factory2 descriptor */
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory2"));

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
		capabilityDescriptors.add(MockResource.createCapabilityDescriptor("factory1"));

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
