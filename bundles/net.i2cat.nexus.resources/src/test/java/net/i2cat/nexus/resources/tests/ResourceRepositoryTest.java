package net.i2cat.nexus.resources.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceRepository;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * Test class for the ResourceRepository class
 * @author Scott Campbell (CRC)
 *
 */
public class ResourceRepositoryTest
{
	/* The class under test - actually, it is a class that extends the class under test 
	 * but adds no new functionality */
	private ResourceRepository resourceRepository = null;
	private ResourceDescriptor descriptor = null;
	/* Use a mock module factory */
	private ICapabilityFactory mockCapabilityFactory = null;

	
	@Before
	public void setup() {
		mockCapabilityFactory = createMock(ICapabilityFactory.class);
		descriptor = new ResourceDescriptor();
		Information info = new Information("Mock", "Mock Resource", "1.0.0");
		descriptor.setInformation(info);
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityInformation(new Information("MockCapability", "mock cpability factory", "1.0.0"));
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		descriptor.setCapabilityDescriptors(capabilityDescriptors);
		Map<String, ICapabilityFactory> capabilityFactories = new Hashtable<String,ICapabilityFactory>();
		capabilityFactories.put("MockCapability",mockCapabilityFactory);
		resourceRepository = new ResourceRepository("Mock", capabilityFactories);
		resourceRepository.setResourceDescriptorRepository(new MockResourceDescriptorRepository());
	}

	@Test
	public void testCreateResource() throws ResourceException{		
		replay(mockCapabilityFactory);
		//Run the test
		resourceRepository.createResource(descriptor);
		verify(mockCapabilityFactory);
		
		//check that the expected resource is the one in the list
		IResource resource = resourceRepository.getResource(descriptor.getId());
		assertNotNull(resource);	
		
		//check that the resource descriptor has been added to the resource
		assertNotNull(resource.getResourceDescriptor());
	}
	
	@Test(expected=ResourceException.class)
	public void testEngineTypeNotInConfig() throws ResourceException {
		Information info = new Information();
		info.setType("WrongEngine");
		descriptor.setInformation(info);
		resourceRepository.createResource(descriptor);
	}
	
	@Test
	public void testModifyResource() throws ResourceException {
	}
	
	
	@Test 
	public void testRemoveResource() throws ResourceException {
		
	}
}
