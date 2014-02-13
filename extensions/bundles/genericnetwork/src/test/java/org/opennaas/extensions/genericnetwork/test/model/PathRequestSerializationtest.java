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
import org.opennaas.extensions.genericnetwork.model.path.Destination;
import org.opennaas.extensions.genericnetwork.model.path.Jitter;
import org.opennaas.extensions.genericnetwork.model.path.Latency;
import org.opennaas.extensions.genericnetwork.model.path.PacketLoss;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;
import org.opennaas.extensions.genericnetwork.model.path.PathRequestHelper;
import org.opennaas.extensions.genericnetwork.model.path.QosPolicy;
import org.opennaas.extensions.genericnetwork.model.path.Source;
import org.opennaas.extensions.genericnetwork.model.path.Throughput;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class PathRequestSerializationtest {

	private static final Logger	LOG					= Logger.getLogger(PathRequestSerializationtest.class);

	private static final String	FILE_PATH			= "/pathRequest.xml";

	private static final String	SRC_IP				= "147.84.157.98";
	private static final String	DST_IP				= "89.104.14.13";

	private static final String	SRC_LINK_PORT		= "2";
	private static final String	DST_LINK_PORT		= "3";

	private static final String	SRC_TRANSPORT_PORT	= "22";
	private static final String	DST_TRANSPORT_PORT	= "22";

	private static final String	LABEL				= "4";

	private static final String	JITTER_MIN			= "0";
	private static final String	JITTER_MAX			= "2";

	private static final String	LATENCY_MIN			= "0";
	private static final String	LATENCY_MAX			= "1";

	private static final String	PACKET_LOSS_MIN		= "0";
	private static final String	PACKET_LOSS_MAX		= "1";

	private static final String	THROUGHPUT_MIN		= "0";
	private static final String	THROUGHPUT_MAX		= "5";

	private static final String	DELAY				= "10";
	private static final String	TIMEOUT				= "60";
	private static final String	PRIORITY			= "1";

	@Test
	public void PathRequestSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		LOG.info("PathRequest Serialization Test");

		PathRequest request = generateSampleRequest();

		String xml = ObjectSerializer.toXml(request);
		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		Assert.assertTrue(XmlHelper.compareXMLStrings(expectedXML, xml));

		LOG.debug(xml);
	}

	@Test
	public void PathRequestDeSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		LOG.info("PathRequest Serialization Test");

		String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(FILE_PATH));

		PathRequest dRequest = (PathRequest) ObjectSerializer.fromXml(expectedXML, PathRequest.class);
		PathRequest eRequest = generateSampleRequest();

		Assert.assertEquals(eRequest, dRequest);

	}

	private PathRequest generateSampleRequest() {

		PathRequest request = new PathRequest();

		Source source = PathRequestHelper.generateSource(SRC_IP, SRC_LINK_PORT, SRC_TRANSPORT_PORT);
		Destination destination = PathRequestHelper.generateDestination(DST_IP, DST_LINK_PORT, DST_TRANSPORT_PORT);
		QosPolicy policy = generateSamplePolicy();

		request.setSource(source);
		request.setDestination(destination);
		request.setQosPolicy(policy);
		request.setLabel(LABEL);

		return request;
	}

	private QosPolicy generateSamplePolicy() {

		QosPolicy policy = new QosPolicy();

		Jitter jitter = PathRequestHelper.generateJitter(JITTER_MIN, JITTER_MAX, DELAY, TIMEOUT, PRIORITY);
		Latency latency = PathRequestHelper.generateLatency(LATENCY_MIN, LATENCY_MAX, DELAY, TIMEOUT, PRIORITY);
		PacketLoss packetLoss = PathRequestHelper.generatePacketLoss(PACKET_LOSS_MIN, PACKET_LOSS_MAX, DELAY, TIMEOUT, PRIORITY);
		Throughput throughput = PathRequestHelper.generateThroughPut(THROUGHPUT_MIN, THROUGHPUT_MAX, DELAY, TIMEOUT, PRIORITY);

		policy.setJitter(jitter);
		policy.setLatency(latency);
		policy.setPacketLoss(packetLoss);
		policy.setThroughput(throughput);

		return policy;
	}
}
