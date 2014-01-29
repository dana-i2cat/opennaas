package org.opennaas.extensions.router.capabilities.api.test;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFAreaWrapper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFProtocolEndpointWrapper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFServiceWrapper;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.xml.sax.SAXException;

public class OSPFApiModelSerializationTest {

	private static final String	PATH_FILE_URL	= "/ospfService.xml";

	@Test
	public void OSPFServiceSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		OSPFProtocolEndpointWrapper pE1 = generateProtocolEndpointWrapper("fe-0/3/3.1", EnabledState.ENABLED);
		OSPFProtocolEndpointWrapper pE2 = generateProtocolEndpointWrapper("fe-0/3/3.2", EnabledState.DISABLED);
		Collection<OSPFProtocolEndpointWrapper> endpoints = new ArrayList<OSPFProtocolEndpointWrapper>();
		endpoints.add(pE1);
		endpoints.add(pE2);

		OSPFAreaWrapper area = generateAreaWrapper("0.0.0.0", endpoints);
		Collection<OSPFAreaWrapper> ospfAreas = new ArrayList<OSPFAreaWrapper>();
		ospfAreas.add(area);

		OSPFServiceWrapper ospfService = new OSPFServiceWrapper();

		ospfService.setOspfArea(ospfAreas);

		String xml = ObjectSerializer.toXml(ospfService);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(PATH_FILE_URL));

		Assert.assertTrue(XmlHelper.compareXMLStrings(xml, expectedXml));

	}

	@Test
	public void OSPFServiceDeserializationTest() throws IOException, SerializationException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream(PATH_FILE_URL));
		OSPFServiceWrapper serviceWrapper = (OSPFServiceWrapper) ObjectSerializer.fromXml(xml, OSPFServiceWrapper.class);

		Assert.assertNotNull(serviceWrapper);
		Collection<OSPFAreaWrapper> ospfAreas = serviceWrapper.getOspfAreas();
		Assert.assertNotNull(ospfAreas);
		Assert.assertEquals(1, ospfAreas.size());

		OSPFAreaWrapper ospfArea = ospfAreas.iterator().next();
		Assert.assertEquals("0.0.0.0", ospfArea.getAreaID());
		Assert.assertNotNull(ospfArea.getOspfProtocolEndpoints());

		Collection<OSPFProtocolEndpointWrapper> protocolEndpoints = ospfArea.getOspfProtocolEndpoints();
		Assert.assertEquals(2, protocolEndpoints.size());

		Iterator<OSPFProtocolEndpointWrapper> iterator = protocolEndpoints.iterator();
		OSPFProtocolEndpointWrapper pE = iterator.next();

		OSPFProtocolEndpointWrapper pE2 = iterator.next();
		Assert.assertTrue((pE.getName().equals("fe-0/3/3.1") && pE.getEnabledState().equals(EnabledState.ENABLED) || (pE2.getName().equals(
				"fe-0/3/3.2") && pE2.getName().equals(EnabledState.DISABLED))));
	}

	private OSPFAreaWrapper generateAreaWrapper(String name, Collection<OSPFProtocolEndpointWrapper> endpoints) {

		OSPFAreaWrapper area = new OSPFAreaWrapper();
		area.setAreaID(name);
		area.setOspfProtocolEndpoints(endpoints);

		return area;
	}

	private OSPFProtocolEndpointWrapper generateProtocolEndpointWrapper(String name, EnabledState enabled) {

		OSPFProtocolEndpointWrapper pE = new OSPFProtocolEndpointWrapper();

		pE.setName(name);
		pE.setState(enabled);

		return pE;

	}

}
