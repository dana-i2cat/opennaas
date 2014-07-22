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
import java.util.List;

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
import org.opennaas.extensions.genericnetwork.model.circuit.NetworkConnection;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.topology.Port;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class CircuitCollectionSerializationTest {

	private static final Logger	LOG					= Logger.getLogger(CircuitCollectionSerializationTest.class);
	private static final String	FILE_PATH			= "/circuitCollection.xml";

	private static String		CIRCUIT_A_ID		= "1";
	private static String		CIRCUIT_B_ID		= "2";
	private static String		CIRCUIT_C_ID		= "3";

	private static final String	SRC_PORT_EXTERNAL	= "p0In";
	private static final String	DST_PORT_EXTERNAL	= "p0Out";

	private static final String	TOS					= "4";

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
	public void completeCircuitCollectionSerializationDeserializationTest() throws SerializationException, IOException, SAXException,
			TransformerException,
			ParserConfigurationException {

		LOG.info("CircuitCollection Serialization Test");

		CircuitCollection circuitCollection = generateSampleCircuitCollection();
		circuitCollection.getCircuits().add(generateCircuit());

		String xml = ObjectSerializer.toXml(circuitCollection);

		LOG.debug(xml);

		CircuitCollection generatedCircuitCollection = (CircuitCollection) ObjectSerializer.fromXml(xml, CircuitCollection.class);
		Assert.assertEquals(circuitCollection, generatedCircuitCollection);
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

	private static Circuit generateCircuit() {
		Circuit circuit = new Circuit();

		circuit.setTrafficFilter(generateTrafficFilter(SRC_PORT_EXTERNAL));

		circuit.setCircuitId(CIRCUIT_C_ID);

		circuit.setQos(generateQoSPolicy());

		// set Route with a NetworkConnection
		Route route = new Route();
		route.setId("R1");

		List<NetworkConnection> networkConnections = new ArrayList<NetworkConnection>();
		NetworkConnection networkConnection = new NetworkConnection();
		networkConnection.setName("NC1");
		networkConnection.setId("NC1");
		networkConnection.setSource(generatePort(SRC_PORT_EXTERNAL));
		networkConnection.setDestination(generatePort(DST_PORT_EXTERNAL));
		networkConnections.add(networkConnection);

		NetworkConnection networkConnection2 = new NetworkConnection();
		networkConnection2.setName("NC2");
		networkConnection2.setId("NC2");
		networkConnection2.setSource(generatePort(SRC_PORT_EXTERNAL));
		networkConnection2.setDestination(generatePort(DST_PORT_EXTERNAL));
		networkConnections.add(networkConnection2);

		route.setNetworkConnections(networkConnections);

		circuit.setRoute(route);

		return circuit;
	}

	private static FloodlightOFMatch generateTrafficFilter(String ingressPort) {
		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(ingressPort);
		match.setTosBits(TOS);
		return match;
	}

	private static QoSPolicy generateQoSPolicy() {
		QoSPolicy qoSPolicy = new QoSPolicy();
		qoSPolicy.setMinJitter(123);
		qoSPolicy.setMaxJitter(234);
		qoSPolicy.setMinLatency(345);
		qoSPolicy.setMaxLatency(456);
		qoSPolicy.setMinThroughput(567);
		qoSPolicy.setMaxThroughput(678);
		qoSPolicy.setMinPacketLoss(789);
		qoSPolicy.setMaxPacketLoss(890);
		return qoSPolicy;
	}

	private static Port generatePort(String portId) {
		Port port = new Port();
		port.setId(portId);
		return port;
	}

}
