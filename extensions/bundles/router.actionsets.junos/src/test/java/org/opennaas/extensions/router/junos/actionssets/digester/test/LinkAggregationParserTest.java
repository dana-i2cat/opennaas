package org.opennaas.extensions.router.junos.actionssets.digester.test;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.router.junos.commandsets.digester.IPInterfaceParser;
import org.opennaas.extensions.router.model.AggregatedLogicalPort;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.ModelHelper;
import org.xml.sax.SAXException;

public class LinkAggregationParserTest {

	/**
	 * Path to a file containing a junos configuration with several aggregated interfaces.</br>
	 * 
	 * Each aggregated interface in this file is assumed to have:</br>
	 * 
	 * - link-speed and minimum-links set.</br>
	 * 
	 * - lacp set to active</br>
	 * 
	 */
	private static final String	XML_FILE_PATH	= "/parsers/sampleConfigWithLinkAggregation.xml";

	String						linkAggregationXML;
	System						model;
	IPInterfaceParser			parser;

	@Before
	public void init() throws IOException {
		linkAggregationXML = readXML();
		model = generateSampleEmptyModel();
		parser = initParser();

	}

	private String readXML() throws IOException {
		InputStream inputFile = getClass().getResourceAsStream(XML_FILE_PATH);
		return IOUtils.toString(inputFile, "UTF-8");
	}

	private System generateSampleEmptyModel() {
		return new ComputerSystem();
	}

	private IPInterfaceParser initParser() {
		IPInterfaceParser parser = new IPInterfaceParser(model);
		parser.init();
		return parser;
	}

	@Test
	public void linkAggregationParserTest() throws IOException, SAXException {

		parser.configurableParse(new ByteArrayInputStream(linkAggregationXML.getBytes()));
		model = parser.getModel();

		Assert.assertNotNull(ModelHelper.getAggregatedLogicalPorts(model));
		Assert.assertFalse("Model should contain AggregatedLogicalPorts", ModelHelper.getAggregatedLogicalPorts(model).isEmpty());
		for (AggregatedLogicalPort aggregator : ModelHelper.getAggregatedLogicalPorts(model)) {
			// check aggregation options
			Assert.assertNotNull(aggregator.getAggregatedOptions());
			Assert.assertFalse("linkSpeed should be set", StringUtils.isEmpty(aggregator.getAggregatedOptions().getLinkSpeed()));
			Assert.assertFalse("minimumLinks should be set", StringUtils.isEmpty(aggregator.getAggregatedOptions().getMinimumLinks()));
			Assert.assertTrue("lacp should be active", aggregator.getAggregatedOptions().isLacpActive());

			Assert.assertNotNull(aggregator.getInterfaces());
			Assert.assertFalse("AggregatedInterface should contain at least one interface", aggregator.getInterfaces().isEmpty());
		}
	}

}
