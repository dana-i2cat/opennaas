package org.opennaas.extensions.genericnetwork.test.capability.nclprovisioner;

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
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class CircuitCollectionSerializationTest {

	private static final Logger	LOG				= Logger.getLogger(CircuitCollectionSerializationTest.class);
	private static final String	FILE_PATH		= "/circuitCollection.xml";

	private String				CIRCUIT_A_ID	= "1";
	private String				CIRCUIT_B_ID	= "2";

	@Test
	public void circuitCollectionSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		LOG.info("CircuitCollection Serialization Test");

		CircuitCollection circuitCollection = generateSampleCircuitCollection();

		String xml = ObjectSerializer.toXml(circuitCollection);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXml, xml));

		LOG.debug(xml);
	}

	@Test
	public void circuitCollectionDeserializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		LOG.info("CircuitCollection Deserialization Test");

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		CircuitCollection eCircuitCollection = (CircuitCollection) ObjectSerializer.fromXml(expectedXml, CircuitCollection.class);
		CircuitCollection cCircuitCollection = generateSampleCircuitCollection();

		Assert.assertEquals(cCircuitCollection, eCircuitCollection);

	}

	/**
	 * Generates a {@link CircuitCollection} with two {@link Circuit}
	 * 
	 * @return
	 */
	private CircuitCollection generateSampleCircuitCollection() {

		CircuitCollection circuitCollection = new CircuitCollection();
		Collection<Circuit> circuits = new ArrayList<Circuit>();

		Circuit circuitA = generateSampleCircuit(CIRCUIT_A_ID);
		Circuit circuitB = generateSampleCircuit(CIRCUIT_B_ID);
		circuits.add(circuitA);
		circuits.add(circuitB);

		circuitCollection.setCircuits(circuits);

		return circuitCollection;
	}

	/**
	 * Generates a simple {@link Circuit} only with ids. It's used only to check the {@link CircuitCollection} serialization. We assume the
	 * {@link Circuit} object itself is serializable and it's tested by its specific test.
	 * 
	 * @param id
	 * @return
	 */
	private Circuit generateSampleCircuit(String id) {

		Circuit circuit = new Circuit();
		circuit.setCircuitId(id);

		return circuit;
	}

}
