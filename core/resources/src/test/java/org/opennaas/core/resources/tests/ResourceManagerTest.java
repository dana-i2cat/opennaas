package org.opennaas.core.resources.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;

public class ResourceManagerTest {

	Log							logger			= LogFactory.getLog(ResourceManagerTest.class);
	private ResourceManager		resourceManager	= null;
	private IResourceRepository	mockRepository	= null;

	@Before
	public void setUp() {
		createAndInitializeMockObjects();
		createAndInitializeResourceManager();
	}

	private void createAndInitializeMockObjects() {
		mockRepository = createMock(IResourceRepository.class);
	}

	private void createAndInitializeResourceManager() {
		resourceManager = new ResourceManager();
		resourceManager.resourceRepositoryAdded(mockRepository, getMockRepositoryServiceProperties());
	}

	private Map<String, String> getMockRepositoryServiceProperties() {
		Map<String, String> serviceProperties = new Hashtable<String, String>();
		serviceProperties.put("type", "mock");
		return serviceProperties;
	}

	@Test
	public void testCreateResource() {
		try {
			ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
			Information info = new Information();
			info.setType("mock");
			resourceDescriptor.setInformation(info);
			prepareMockObjectsForTestCreateResource(resourceDescriptor);
			resourceManager.createResource(resourceDescriptor);
			verify(mockRepository);
		} catch (ResourceException ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	private void prepareMockObjectsForTestCreateResource(ResourceDescriptor resourceDescriptor) throws ResourceException {
		expect(mockRepository.createResource(resourceDescriptor)).andReturn(getMockResource("mock", new String("23")));
		replay(mockRepository);
	}

	private IResource getMockResource(String type, String id) {
		Resource mockResource = new Resource();
		IResourceIdentifier mockResourceIdentifier = new ResourceIdentifier(type, id);
		mockResource.setResourceIdentifier(mockResourceIdentifier);
		return mockResource;
	}

	private IResource getMockResourceWithInformation(String type, String id) {
		Resource mockResource = new Resource();
		IResourceIdentifier mockResourceIdentifier = new ResourceIdentifier(type, id);
		mockResource.setResourceIdentifier(mockResourceIdentifier);
		ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
		Information info = new Information();
		info.setType(type);
		resourceDescriptor.setInformation(info);
		mockResource.setResourceDescriptor(resourceDescriptor);
		return mockResource;
	}

	@Test
	public void testListEngines() {
		prepareMockObjectsForTestListResources();
		List<IResource> resources = resourceManager.listResources();
		Assert.assertEquals(2, resources.size());
		Assert.assertEquals("mock", resources.get(0).getResourceIdentifier().getType());
		Assert.assertEquals(new String("23"), resources.get(0).getResourceIdentifier().getId());
	}

	private void prepareMockObjectsForTestListResources() {
		expect(mockRepository.listResources()).andReturn(getResourcesList());
		replay(mockRepository);
	}

	private List<IResource> getResourcesList() {
		List<IResource> resourcesList = new ArrayList<IResource>();
		resourcesList.add(getMockResource("mock", new String("23")));
		resourcesList.add(getMockResource("mock", new String("24")));
		return resourcesList;
	}

	@Test
	public void testModify() {
		try {
			IResourceIdentifier mockResourceIdentifier = new ResourceIdentifier("mock", new String("23"));
			ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
			Information info = new Information();
			info.setType("mock");
			resourceDescriptor.setInformation(info);
			prepareMockObjectsForTestModify(mockResourceIdentifier, resourceDescriptor);
			resourceManager.modifyResource(mockResourceIdentifier, resourceDescriptor);
			verify(mockRepository);
		} catch (ResourceException ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	private void prepareMockObjectsForTestModify(IResourceIdentifier resourceIdentifier, ResourceDescriptor resourceDescriptor)
			throws ResourceException {
		expect(mockRepository.getResource(resourceIdentifier.getId())).andReturn(getMockResourceWithInformation("mock", new String("23")));
		expect(mockRepository.modifyResource(resourceIdentifier.getId(), resourceDescriptor)).andReturn(getMockResource("mock", new String("23")));
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
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveResource() {
		try {
			prepareMockObjectsForTestRemoveResource();
			resourceManager.removeResource(new ResourceIdentifier("mock", new String("23")));
			resourceManager.removeResource(new ResourceIdentifier("mock", new String("24")));
			verify(mockRepository);
		} catch (ResourceException ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	private void prepareMockObjectsForTestRemoveResource() throws ResourceException {
		mockRepository.removeResource(new String("23"));
		mockRepository.removeResource(new String("24"));
		replay(mockRepository);
	}

	@Test
	public void testResourceManagerList() {
		prepareMockObjectsForTestListResources();
		List<IResource> resources = resourceManager.listResources();
		Assert.assertEquals(2, resources.size());
		Assert.assertEquals("mock", resources.get(0).getResourceIdentifier().getType());
		Assert.assertEquals(new String("23"), resources.get(0).getResourceIdentifier().getId());
		Assert.assertEquals("mock", resources.get(1).getResourceIdentifier().getType());
		Assert.assertEquals(new String("24"), resources.get(1).getResourceIdentifier().getId());
	}

	@Test
	public void testGetResource() {
		try {
			prepareMockObjectsForTestGetResource();
			IResource resource = resourceManager.getResource(new ResourceIdentifier("mock", new String("23")));
			verify(mockRepository);
			Assert.assertNotNull(resource);
		} catch (ResourceException ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	private void prepareMockObjectsForTestGetResource() throws ResourceException {
		expect(mockRepository.getResource(new String("23"))).andReturn(getMockResource("mock", new String("23")));
		replay(mockRepository);
	}

}
