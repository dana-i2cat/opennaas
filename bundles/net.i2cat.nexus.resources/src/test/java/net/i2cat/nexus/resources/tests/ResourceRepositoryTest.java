package net.i2cat.nexus.resources.tests;

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

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceRepository;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

import org.junit.BeforeClass;
import org.junit.Test;

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
		resourceRepository = new ResourceRepository("Mock", "", capabilityFactories);
		resourceRepository.setResourceDescriptorRepository(new MockResourceDescriptorRepository());
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

	public void testRemoveResource() {
		try {
			ResourceDescriptor descriptor = newResourceDescriptor("Resource Mock CreateRemove");
			resourceRepository.createResource(descriptor);
			resourceRepository.removeResource(descriptor.getId());

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
