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
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitRequestHelper;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public class CircuitRequestSerializationtest {

	private static final Logger	LOG					= Logger.getLogger(CircuitRequestSerializationtest.class);

	private static final String	FILE_PATH			= "/circuitRequest.xml";

	private static final String	SRC_IP				= "147.84.157.98";
	private static final String	DST_IP				= "89.104.14.13";

	private static final String	SRC_LINK_PORT		= "2";
	private static final String	DST_LINK_PORT		= "3";

	private static final String	SRC_TRANSPORT_PORT	= "22";
	private static final String	DST_TRANSPORT_PORT	= "22";

	private static final String	LABEL				= "4";

	private static final int	JITTER_MIN			= 0;
	private static final int	JITTER_MAX			= 2;

	private static final int	LATENCY_MIN			= 0;
	private static final int	LATENCY_MAX			= 1;

	private static final int	PACKET_LOSS_MIN		= 0;
	private static final int	PACKET_LOSS_MAX		= 1;

	private static final int	THROUGHPUT_MIN		= 0;
	private static final int	THROUGHPUT_MAX		= 5;

	@Test
	public void CircuitRequestSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		LOG.info("CircuitRequest Serialization Test");

		CircuitRequest request = generateSampleRequest();

		String xml = ObjectSerializer.toXml(request);
		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXML, xml));

		LOG.debug(xml);
	}

	@Test
	public void CircuitRequestDeSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		LOG.info("CircuitRequest Serialization Test");

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		CircuitRequest dRequest = (CircuitRequest) ObjectSerializer.fromXml(expectedXML, CircuitRequest.class);
		CircuitRequest eRequest = generateSampleRequest();

		Assert.assertEquals(eRequest, dRequest);

	}

	private CircuitRequest generateSampleRequest() {

		Source source = CircuitRequestHelper.generateSource(SRC_IP, SRC_LINK_PORT, SRC_TRANSPORT_PORT);
		Destination destination = CircuitRequestHelper.generateDestination(DST_IP, DST_LINK_PORT, DST_TRANSPORT_PORT);
		QoSPolicy policy = generateSamplePolicy();

		CircuitRequest request = CircuitRequestHelper.generateCircuitRequest(source, destination, LABEL, policy, null);

		return request;
	}

	private QoSPolicy generateSamplePolicy() {

		QoSPolicy policy = CircuitRequestHelper.generateQoSPolicy(JITTER_MIN, JITTER_MAX, LATENCY_MIN, LATENCY_MAX, THROUGHPUT_MIN, THROUGHPUT_MAX,
				PACKET_LOSS_MIN, PACKET_LOSS_MAX);

		return policy;
	}
}
