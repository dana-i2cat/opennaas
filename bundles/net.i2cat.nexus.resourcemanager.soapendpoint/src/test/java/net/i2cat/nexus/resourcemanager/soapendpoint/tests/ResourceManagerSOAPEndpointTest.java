package net.i2cat.nexus.resourcemanager.soapendpoint.tests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.nexus.resourcemanager.soapendpoint.IResourceManagerSOAPEndpoint;
import net.i2cat.nexus.resourcemanager.soapendpoint.ResourceData;
import net.i2cat.nexus.resourcemanager.soapendpoint.ResourceManagerSOAPEndpoint;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceManager;
import net.i2cat.nexus.resources.ResourceRepository;
import net.i2cat.nexus.resources.capability.ICapabilityFactory;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.Information;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;

/**
 * Test of the class ResourceManagerSOAPEndpoint
 * @author Ali LAHLOU (Synchromedia, ETS)
 *
 */
public class ResourceManagerSOAPEndpointTest {
	
	Logger logger = LoggerFactory.getLogger(ResourceManagerSOAPEndpointTest.class);
	//The client WebService
	private IResourceManagerSOAPEndpoint client = null;
	private static ResourceManager resourceManager = null;
	private static ResourceRepository resourceRepository = null;
	private static JaxWsServerFactoryBean server = null;
	private static ICapabilityFactory mockCapabilityFactory = null;
	
	@BeforeClass
	public static void initJaxFactory() {
		//Loading a server for testing the web services
		initResourceManager();
		server = new JaxWsServerFactoryBean();
		server.setServiceClass(ResourceManagerSOAPEndpoint.class); 
		ResourceManagerSOAPEndpoint managerService = new ResourceManagerSOAPEndpoint(resourceManager);
		server.setServiceBean(managerService);
		server.setAddress("http://localhost:9080/resourceManagerSOAPEndpoint");
		server.create();
	
	}
	
	private static void initResourceManager(){
		resourceManager = new ResourceManager();
		initResourceRepository();
		Map<String, String> serviceProperties = new Hashtable<String, String>();
		serviceProperties.put("type", "Mock");
		resourceManager.resourceRepositoryAdded(resourceRepository, serviceProperties);
	}
	
	private static void initResourceRepository(){
		mockCapabilityFactory = new MockCapabilityFactory();
		Map<String, ICapabilityFactory> capabilityFactories = new Hashtable<String,ICapabilityFactory>();
		capabilityFactories.put("MockCapability",mockCapabilityFactory);;
		resourceRepository = new ResourceRepository("Mock", capabilityFactories);
		resourceRepository.setResourceDescriptorRepository(new MockResourceDescriptorRepository());
	}
	
	@Before
	public void setup() {
		//Create a web service client
		createWebServiceClient();
	}
	
	private void createWebServiceClient() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IResourceManagerSOAPEndpoint.class);
		factory.setAddress("http://localhost:9080/resourceManagerSOAPEndpoint");
	    client = (IResourceManagerSOAPEndpoint) factory.create();
	}
	
	@Test
	public void testCreateResource() {
		try {
			ResourceData resourceData = client.createResource(getResourceDescriptorForCreateResource());
			Assert.assertNotNull(resourceData);
			Assert.assertEquals("Mock", resourceData.getType());
		}catch (ResourceException ex){
			ex.printStackTrace();
		}
	}

	public ResourceDescriptor getResourceDescriptorForCreateResource() {
		ResourceDescriptor descriptor = new ResourceDescriptor();
		Information info = new Information("Mock", "Mock Resource", "1.0.0");
		descriptor.setInformation(info);
		CapabilityDescriptor capabilityDescriptor = new CapabilityDescriptor();
		capabilityDescriptor.setCapabilityInformation(new Information("MockCapability", "mock cpability factory", "1.0.0"));
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(capabilityDescriptor);
		descriptor.setCapabilityDescriptors(capabilityDescriptors);
		return descriptor;
	}
	
	@Test
	public void testGetResource() {
		try {
			ResourceData resourceData = client.createResource(getResourceDescriptorForCreateResource());
			ResourceData resourceData1 = client.getResource(resourceData.getType(), resourceData.getId());
			Assert.assertNotNull(resourceData1);
		}catch (ResourceException ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testRemove() {
		try {
			ResourceData resourceData = client.createResource(getResourceDescriptorForCreateResource());
			client.removeResource(resourceData.getType(), resourceData.getId());
		}catch (ResourceException ex){
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testlistByType() {
		List<ResourceData> list = client.listReourcesByType("Mock");
		Assert.assertNotSame(0, list.size());
	}
	
	@Test
	public void testlist() {
		List<ResourceData> list = client.listResources();
		Assert.assertNotSame(0, list.size());
	}	
}