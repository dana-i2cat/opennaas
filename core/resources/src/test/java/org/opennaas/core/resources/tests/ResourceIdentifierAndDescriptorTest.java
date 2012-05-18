package org.opennaas.core.resources.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;

public class ResourceIdentifierAndDescriptorTest {

	private IResourceIdentifier	resourceIdentifier	= null;

	private ResourceDescriptor	resourceDescriptor	= null;

	@Before
	public void setUp() {
		resourceIdentifier = new ResourceIdentifier("Simple");

		CapabilityDescriptor queueCapabDesc = ResourceHelper.newCapabilityDescriptor("junos", "10.10", "queue", "uri");
		CapabilityDescriptor chassisCapabDesc = ResourceHelper.newCapabilityDescriptor("junos", "10.10", "chassis", "uri");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(chassisCapabDesc);
		capabilityDescriptors.add(queueCapabDesc);
		resourceDescriptor = ResourceHelper.newResourceDescriptor(capabilityDescriptors, "router", "http://example.org/resources/testRouter",
				"testRouter");
	}

	@Test
	public void testIsURIWellFormed() {
		Assert.assertNotNull(resourceIdentifier.getURI());
	}

	@Test
	public void testGetTypeIsNotNull() {
		Assert.assertNotNull(resourceIdentifier.getType());
	}

	@Test
	public void testMarshalling() throws Exception {
		JAXBContext context = JAXBContext.newInstance(ResourceIdentifier.class); // using jaxb.index file
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(resourceIdentifier, System.out);
	}

	@Test
	public void testMarshallingUnMarshalling() throws Exception {
		JAXBContext context = JAXBContext.newInstance(ResourceIdentifier.class); // using jaxb.index file

		StringWriter writer = new StringWriter();

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(resourceIdentifier, writer);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		StringReader reader = new StringReader(writer.toString());
		ResourceIdentifier loaded = (ResourceIdentifier) unmarshaller.unmarshal(reader);

		assertEquals(resourceIdentifier, loaded);
	}

	@Test
	public void testMarshallingUnMarshallingDescriptor() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ResourceDescriptor.class); // using jaxb.index file

		StringWriter writer = new StringWriter();

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(resourceDescriptor, writer);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		StringReader reader = new StringReader(writer.toString());
		ResourceDescriptor loaded = (ResourceDescriptor) unmarshaller.unmarshal(reader);

		assertEquals(resourceDescriptor.getId(), loaded.getId());
		assertEquals(resourceDescriptor.getInformation(), loaded.getInformation());
		for (String key : resourceDescriptor.getProperties().keySet()) {
			assertEquals(resourceDescriptor.getProperties().get(key), loaded.getProperties().get(key));
		}
		for (String key : loaded.getProperties().keySet()) {
			assertTrue(resourceDescriptor.getProperties().containsKey(key));
		}
		assertEquals(resourceDescriptor.getCapabilityDescriptors().size(), loaded.getCapabilityDescriptors().size());
		for (CapabilityDescriptor capabDesc : resourceDescriptor.getCapabilityDescriptors()) {
			assertTrue(loaded.getCapabilityDescriptors().contains(capabDesc));
		}
	}
}
