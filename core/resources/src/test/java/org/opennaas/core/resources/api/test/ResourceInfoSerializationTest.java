package org.opennaas.core.resources.api.test;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.api.model.ResourceInfo;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class ResourceInfoSerializationTest {

	private static final String	FILE_PATH	= "/api/resourceInfo.xml";

	@Test
	public void serializationTest() throws SerializationException, IOException, SAXException, TransformerException, ParserConfigurationException {

		ResourceInfo info = new ResourceInfo();

		info.setName("router1");
		info.setType("router");
		info.setResourceId("xfh3-125gfw-123");
		info.setState(State.ACTIVE);

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("ospf");
		capabilities.add("chassis");
		info.setCapabilityNames(capabilities);

		String xml = ObjectSerializer.toXml(info);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXML, xml));
	}

	@Test
	public void deserializationTest() throws IOException, SerializationException {

		String readedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));
		ResourceInfo resourceInfo = (ResourceInfo) ObjectSerializer.fromXml(readedXML, ResourceInfo.class);

		Assert.assertNotNull(resourceInfo);
		Assert.assertEquals("router", resourceInfo.getType());
		Assert.assertEquals("router1", resourceInfo.getName());
		Assert.assertEquals("xfh3-125gfw-123", resourceInfo.getResourceId());
		Assert.assertEquals(State.ACTIVE, resourceInfo.getState());

		Assert.assertEquals(2, resourceInfo.getCapabilityNames().size());

		Iterator<String> iterator = resourceInfo.getCapabilityNames().iterator();
		String capabilityA = iterator.next();
		String capabilityB = iterator.next();

		Assert.assertTrue(capabilityA.equals("ospf") || capabilityA.equals("chassis"));
		Assert.assertTrue(capabilityB.equals("ospf") || capabilityB.equals("chassis"));
		Assert.assertFalse(capabilityA.equals(capabilityB));

	}
}
