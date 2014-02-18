package org.opennaas.extensions.genericnetwork.test.model;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionInput;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class RouteSelectionInputSerializationTest {

	private static final String	SRC_IP					= "192.168.10.10";
	private static final String	DST_IP					= "192.168.10.11";
	private static final String	TOS						= "4";
	private static final String	SRC_PORT				= "portA";
	private static final String	DST_PORT				= "portB";

	private static final String	WITHOUT_PORTS_FILE_PATH	= "/routeSelectionInputWithoutPorts.xml";
	private static final String	WITH_PORTS_FILE_PATH	= "/routeSelectionInputWithPorts.xml";
	private static final Logger	LOG						= Logger.getLogger(RouteSelectionInputSerializationTest.class);

	@Test
	public void WithoutPortsSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		RouteSelectionInput input = new RouteSelectionInput(SRC_IP, DST_IP, TOS);
		String xml = ObjectSerializer.toXml(input);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(WITHOUT_PORTS_FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXML, xml));

		LOG.debug(xml);

	}

	@Test
	public void WithoutPortsDeSerializationTest() throws SerializationException, IOException {

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(WITHOUT_PORTS_FILE_PATH));
		RouteSelectionInput readedInput = (RouteSelectionInput) ObjectSerializer.fromXml(expectedXML, RouteSelectionInput.class);

		RouteSelectionInput expectedInput = new RouteSelectionInput(SRC_IP, DST_IP, TOS);

		Assert.assertEquals(readedInput, expectedInput);

	}

	@Test
	public void WithPortsSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		RouteSelectionInput input = new RouteSelectionInput(SRC_IP, DST_IP, TOS, SRC_PORT, DST_PORT);
		String xml = ObjectSerializer.toXml(input);

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(WITH_PORTS_FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXML, xml));

		LOG.debug(xml);

	}

	@Test
	public void WithPortsDeSerializationTest() throws SerializationException, IOException {

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(WITH_PORTS_FILE_PATH));
		RouteSelectionInput readedInput = (RouteSelectionInput) ObjectSerializer.fromXml(expectedXML, RouteSelectionInput.class);

		RouteSelectionInput expectedInput = new RouteSelectionInput(SRC_IP, DST_IP, TOS, SRC_PORT, DST_PORT);

		Assert.assertEquals(readedInput, expectedInput);

	}

}
