package org.opennaas.extensions.network.repository.tests;

/*
 * #%L
 * OpenNaaS :: Network :: Repository
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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.mock.MockCapabilityFactory;
import org.opennaas.core.resources.mock.MockDescriptorRepository;
import org.opennaas.extensions.network.repository.NetworkRepository;

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

			resource = NetworkRepository.modifyResource(id, oldResourceDescriptor("networkOld", "network"));
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
