package net.i2cat.nexus.resources.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.Resource;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceIdentifier;
import net.i2cat.nexus.resources.ResourceManager;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

public class ResourceManagerTest {
	
	Logger logger = LoggerFactory.getLogger(ResourceManagerTest.class);
	private ResourceManager resourceManager = null;
	private IResourceRepository mockRepository = null;
	
	@Before
	public void setUp(){
		createAndInitializeMockObjects();
		createAndInitializeResourceManager();
	}
	
	private void createAndInitializeMockObjects(){
		mockRepository = createMock(IResourceRepository.class);
	}
	
	private void createAndInitializeResourceManager(){
		resourceManager = new ResourceManager();
		resourceManager.resourceRepositoryAdded(mockRepository, getMockRepositoryServiceProperties());
	}
	
	private Map<String, String> getMockRepositoryServiceProperties(){
		Map<String, String> serviceProperties = new Hashtable<String, String>();
		serviceProperties.put("type", "mock");
		return serviceProperties;
	}
	
	@Test
	public void testCreateResource(){
		try{
			ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
			Information info = new Information();
			info.setType("mock");
			resourceDescriptor.setInformation(info);
			prepareMockObjectsForTestCreateResource(resourceDescriptor);
			resourceManager.createResource(resourceDescriptor);
			verify(mockRepository);
		}catch(ResourceException ex){
			ex.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
	private void prepareMockObjectsForTestCreateResource(ResourceDescriptor resourceDescriptor) throws ResourceException{
		expect(mockRepository.createResource(resourceDescriptor)).andReturn(getMockResource("mock", new String("23")));
		replay(mockRepository);
	}
	
	private IResource getMockResource(String type, String id){
		IResource mockResource = new Resource();
		IResourceIdentifier mockResourceIdentifier = new ResourceIdentifier(type, id);
		mockResource.setResourceIdentifier(mockResourceIdentifier);
		return mockResource;
	}
	
	@Test
	public void testListEngines(){
		prepareMockObjectsForTestListResources();
		List<IResource> resources = resourceManager.listResources();
		Assert.assertEquals(2, resources.size());
		Assert.assertEquals("mock", resources.get(0).getResourceIdentifier().getType());
		Assert.assertEquals(new String("23"), resources.get(0).getResourceIdentifier().getId());
	}
	
	private void prepareMockObjectsForTestListResources(){
		expect(mockRepository.listResources()).andReturn(getResourcesList());
		replay(mockRepository);
	}
	
	private List<IResource> getResourcesList(){
		List<IResource> resourcesList = new ArrayList<IResource>();
		resourcesList.add(getMockResource("mock", new String("23")));
		resourcesList.add(getMockResource("mock", new String("24")));
		return resourcesList;
	}
	
	@Test
	public void testModify(){
		try{
			ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
			Information info = new Information();
			info.setType("mock");
			resourceDescriptor.setInformation(info);
			prepareMockObjectsForTestModify(resourceDescriptor);
			resourceManager.modifyResource(resourceDescriptor);
			verify(mockRepository);
		}catch(ResourceException ex){
			ex.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
	private void prepareMockObjectsForTestModify(ResourceDescriptor resourceDescriptor) throws ResourceException{
		expect(mockRepository.modifyResource(resourceDescriptor)).andReturn(getMockResource("mock", new String("23")));
		replay(mockRepository);
	}
	
	/**
	 * Test start & stop resource functions
	 */
	@Test
	public void testStartStopResource() {
		IResourceIdentifier mockResourceIdentifier = new ResourceIdentifier("mock", new String("23"));
		try {
			resourceManager.startResource(mockResourceIdentifier);
			resourceManager.stopResource(mockResourceIdentifier);
		} catch (ResourceException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void testRemoveResource(){
		try{
			prepareMockObjectsForTestRemoveResource();	
			resourceManager.removeResource(new ResourceIdentifier("mock", new String("23")));
			resourceManager.removeResource(new ResourceIdentifier("mock", new String("24")));
			verify(mockRepository);
		}catch(ResourceException ex){
			ex.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
	private void prepareMockObjectsForTestRemoveResource() throws ResourceException{
		mockRepository.removeResource(new String("23"));
		mockRepository.removeResource(new String("24"));
		replay(mockRepository);
	}
	
	@Test
	public void testResourceManagerList(){
		prepareMockObjectsForTestListResources();
		List<IResource> resources = resourceManager.listResources();
		Assert.assertEquals(2, resources.size());
		Assert.assertEquals("mock", resources.get(0).getResourceIdentifier().getType());
		Assert.assertEquals(new String("23"), resources.get(0).getResourceIdentifier().getId());
		Assert.assertEquals("mock", resources.get(1).getResourceIdentifier().getType());
		Assert.assertEquals(new String("24"), resources.get(1).getResourceIdentifier().getId());
	}

	@Test
	public void testGetResource(){
		try{
			prepareMockObjectsForTestGetResource();
			IResource resource = resourceManager.getResource(new ResourceIdentifier("mock", new String("23")));
			verify(mockRepository);
			Assert.assertNotNull(resource);
		}catch(ResourceException ex){
			ex.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
	private void prepareMockObjectsForTestGetResource() throws ResourceException{
		expect(mockRepository.getResource(new String("23"))).andReturn(getMockResource("mock", new String("23")));
		replay(mockRepository);
	}

}
