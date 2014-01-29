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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.api.model.ResourceListWrapper;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class ResourceListWrapperSerializationTest {

	private static final String	FILE_PATH	= "/api/resourceList.xml";

	@Test
	public void serializationTest() throws SerializationException, IOException, SAXException, TransformerException, ParserConfigurationException {

		ResourceListWrapper wrapper = new ResourceListWrapper();
		List<String> resourcesNames = new ArrayList<String>();

		resourcesNames.add("router1");
		resourcesNames.add("router2");
		wrapper.setResources(resourcesNames);

		String xml = ObjectSerializer.toXml(wrapper);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXML, xml));
	}

	@Test
	public void deserializationTest() throws IOException, SerializationException {

		String readedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));
		ResourceListWrapper wrapper = (ResourceListWrapper) ObjectSerializer.fromXml(readedXML, ResourceListWrapper.class);

		Assert.assertNotNull(wrapper);
		Assert.assertNotNull(wrapper.getResources());

		Collection<String> resources = wrapper.getResources();
		Assert.assertEquals(2, resources.size());

		Iterator<String> resourceIterator = resources.iterator();
		String resourceA = resourceIterator.next();
		String resourceB = resourceIterator.next();

		Assert.assertTrue(resourceA.equals("router1") || resourceA.equals("router2"));
		Assert.assertTrue(resourceB.equals("router1") || resourceB.equals("router2"));
		Assert.assertFalse(resourceA.equals(resourceB));
	}
}
