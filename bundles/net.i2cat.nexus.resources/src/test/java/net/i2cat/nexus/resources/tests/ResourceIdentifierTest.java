package net.i2cat.nexus.resources.tests;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceIdentifier;

public class ResourceIdentifierTest {

	private IResourceIdentifier resourceIdentifier = null;
	
	@Before
	public void setUp(){
		resourceIdentifier = new ResourceIdentifier("Simple");
	}
	
	@Test
	public void testIsURIWellFormed(){
		Assert.assertNotNull(resourceIdentifier.getURI());
	}
	
	@Test
	public void testGetTypeIsNotNull(){
		Assert.assertNotNull(resourceIdentifier.getType());
	}
	
	@Test
	public void testMarshalling() throws Exception{
		JAXBContext context = JAXBContext.newInstance(ResourceIdentifier.class); // using jaxb.index file
		Marshaller m = context.createMarshaller();
		m.setProperty("jaxb.formatted.output", Boolean.TRUE);
		ResourceIdentifier id = new ResourceIdentifier("Simple");
		m.marshal(id, System.out);
	}
}
