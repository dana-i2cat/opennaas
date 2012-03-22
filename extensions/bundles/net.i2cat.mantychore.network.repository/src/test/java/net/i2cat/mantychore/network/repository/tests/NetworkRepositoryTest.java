package net.i2cat.mantychore.network.repository.tests;

import net.i2cat.mantychore.network.repository.NetworkRepository;
import net.i2cat.mantychore.tests.utils.mock.MockCapabilityFactory;
import net.i2cat.mantychore.tests.utils.mock.MockDescriptorRepository;

import net.i2cat.mantychore.tests.utils.ResourceHelper;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;

import org.opennaas.core.resources.descriptor.ResourceDescriptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class NetworkRepositoryTest {



	NetworkRepository	NetworkRepository;

	@Test
	public void testCapabilityFactories() {
		NetworkRepository = new NetworkRepository("NetworkRepository");
		NetworkRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		NetworkRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		NetworkRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		Assert.assertTrue(NetworkRepository.getCapabilityFactories().size() == 2);
		Assert.assertNotNull(NetworkRepository.getCapabilityFactories().get("factory1"));
		Assert.assertNotNull(NetworkRepository.getCapabilityFactories().get("factory2"));

	}

	@Test
	public void testCreateRemoveResources() {
		NetworkRepository = new NetworkRepository("network");
		NetworkRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		NetworkRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		NetworkRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		try {
			/* type of resources */

			ResourceDescriptor descriptor = ResourceHelper.newResourceDescriptorNetwork("network1");
			IResource resource = NetworkRepository.createResource(descriptor);
			String id = resource.getResourceIdentifier().getId();
			Assert.assertNotNull(id);
			Assert.assertTrue(resource.getResourceDescriptor().equals(descriptor));
			Assert.assertTrue(resource.getResourceIdentifier().getType().equals("network"));

			boolean thrown = false;
			NetworkRepository.removeResource(id);
			try {
				NetworkRepository.getResource(id);
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

		NetworkRepository = new NetworkRepository("network");
		NetworkRepository.setResourceDescriptorRepository(new MockDescriptorRepository());

		NetworkRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory1"));
		NetworkRepository.capabilityFactoryAdded(new MockCapabilityFactory("factory2"));

		try {
			/* type of resources */
			IResource resource = NetworkRepository.createResource(ResourceHelper.newResourceDescriptorNetwork("network1"));
			String id = resource.getResourceIdentifier().getId();
			Assert.assertTrue(resource.getResourceDescriptor().getCapabilityDescriptors().size() == 2);
			Assert.assertTrue(resource.getResourceIdentifier().getType().equals("network"));

			resource = NetworkRepository.modifyResource(id, oldResourceDescriptor("networkOld","network"));
			IResource newResource = NetworkRepository.getResource(resource.getResourceIdentifier().getId());
			Assert.assertTrue(newResource.getResourceDescriptor().getCapabilityDescriptors().size() == 1);
			Assert.assertTrue(newResource.getResourceIdentifier().getType().equals("network"));
			Assert.assertTrue(newResource.getResourceDescriptor().getInformation().getName().equals("networkOld"));
		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}



	private ResourceDescriptor oldResourceDescriptor(String name, String type) {
		ResourceDescriptor networkDescriptor = ResourceHelper.newResourceDescriptorNetwork(name);
		networkDescriptor.getInformation().setType(type);
		networkDescriptor.getCapabilityDescriptors().remove(0);
		return networkDescriptor;
	}

}
