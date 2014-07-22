package org.opennaas.core.resources.tests;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.mock.MockDescriptorRepository;

/**
 * Test class for the ResourceRepository class
 * 
 * @author Scott Campbell (CRC)
 * 
 */
public class ResourceRepositoryTest {
	/*
	 * The class under test - actually, it is a class that extends the class under test but adds no new functionality
	 */
	private static ResourceRepository	resourceRepository		= null;
	/* Use a mock module factory */
	private static ICapabilityFactory	mockCapabilityFactory	= null;

	@BeforeClass
	public static void setup() {
		mockCapabilityFactory = createMock(ICapabilityFactory.class);
		Map<String, ICapabilityFactory> capabilityFactories = new Hashtable<String, ICapabilityFactory>();
		capabilityFactories.put("MockCapability", mockCapabilityFactory);
		resourceRepository = new ResourceRepository("Mock", capabilityFactories);
		resourceRepository.setResourceDescriptorRepository(new MockDescriptorRepository());
	}

	@Test
	public void testCreateResource() {
		try {
			replay(mockCapabilityFactory);
			// Run the test
			ResourceDescriptor descriptor = newResourceDescriptor("Resource Mock Create");
			resourceRepository.createResource(descriptor);

			verify(mockCapabilityFactory);

			// check that the expected resource is the one in the list
			IResource resource = resourceRepository.getResource(descriptor.getId());
			assertNotNull(resource);

			// check that the resource descriptor has been added to the resource
			assertNotNull(resource.getResourceDescriptor());
			assertTrue(descriptor.equals(resource.getResourceDescriptor()));

		} catch (ResourceException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testModifyResource() {

		try {
			ResourceDescriptor descriptor = newResourceDescriptor("Resource Mock Modify");
			resourceRepository.createResource(descriptor);

			ResourceDescriptor descriptor2 = newResourceDescriptor("Mock Resource WORKS");

			assertNotNull(descriptor.getId());

			resourceRepository.modifyResource(descriptor.getId(), descriptor2);
			// check that the expected resource is the one in the list
			IResource resource = resourceRepository.getResource(descriptor.getId());

			assertNotNull(resource);
			assertTrue(resource.getResourceDescriptor().getInformation().getName().equalsIgnoreCase("Mock Resource WORKS"));

		} catch (ResourceException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testRemoveResource() {
		try {

			int previousSize = resourceRepository.listResources().size();

			ResourceDescriptor descriptor = newResourceDescriptor("Resource Mock CreateRemove");
			resourceRepository.createResource(descriptor);
			resourceRepository.removeResource(descriptor.getId());

			assertTrue(resourceRepository.listResources().size() == previousSize);

		} catch (ResourceException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test(expected = ResourceException.class)
	public void testEngineTypeNotInConfig() throws ResourceException {
		ResourceDescriptor descriptor = newResourceDescriptor("Resource Mock Modify");
		descriptor.getInformation().setType("WrongEngine");
		resourceRepository.createResource(descriptor);
	}

	public ResourceDescriptor newResourceDescriptor(String name) {
		ResourceDescriptor descriptor = new ResourceDescriptor();
		Information info = new Information("Mock", name, "1.0.0");
		descriptor.setInformation(info);
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityInformation(new Information("MockCapability", "mock cpability factory", "1.0.0"));
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		descriptor.setCapabilityDescriptors(capabilityDescriptors);
		return descriptor;
	}
}
