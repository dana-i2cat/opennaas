package org.opennaas.extensions.genericnetwork.test.model.helper;

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

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;
import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitParser;
import org.opennaas.extensions.genericnetwork.model.path.Destination;
import org.opennaas.extensions.genericnetwork.model.path.Jitter;
import org.opennaas.extensions.genericnetwork.model.path.Latency;
import org.opennaas.extensions.genericnetwork.model.path.PacketLoss;
import org.opennaas.extensions.genericnetwork.model.path.PathRequest;
import org.opennaas.extensions.genericnetwork.model.path.PathRequestHelper;
import org.opennaas.extensions.genericnetwork.model.path.QosPolicy;
import org.opennaas.extensions.genericnetwork.model.path.Source;
import org.opennaas.extensions.genericnetwork.model.path.Throughput;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class CircuitParserTest {

	private static final String	SRC_IP				= "192.168.1.10";
	private static final String	SRC_LINK_PORT		= "eth1";
	private static final String	SRC_TRANSPORT_PORT	= "80";

	private static final String	DST_IP				= "192.168.1.11";
	private static final String	DST_LINK_PORT		= "eth2";
	private static final String	DST_TRANSPORT_PORT	= "80";

	private static final String	TOS					= "4";

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
	public void pathRequestToCircuitTest() {

		PathRequest request = generateSampleRequest();
		Circuit circuit = CircuitParser.pathRequestToCircuit(request);

		Assert.assertNotNull(circuit);

		Assert.assertTrue(StringUtils.isEmpty(circuit.getCircuitId()));
		Assert.assertNull(circuit.getRoute());
		Assert.assertNotNull(circuit.getQos());
		Assert.assertNotNull(circuit.getTrafficFilter());

		FloodlightOFMatch match = circuit.getTrafficFilter();
		Assert.assertEquals(SRC_IP, match.getSrcIp());
		Assert.assertEquals(DST_IP, match.getDstIp());
		Assert.assertEquals(SRC_TRANSPORT_PORT, match.getSrcPort());
		Assert.assertEquals(DST_TRANSPORT_PORT, match.getDstPort());
		Assert.assertEquals("2048", match.getEtherType());
		Assert.assertTrue(Integer.valueOf(TOS) == Integer.valueOf(match.getTosBits()) * 4);

		QoSPolicy qos = circuit.getQos();
		Assert.assertEquals(JITTER_MIN, String.valueOf(qos.getMinJitter()));
		Assert.assertEquals(JITTER_MAX, String.valueOf(qos.getMaxJitter()));
		Assert.assertEquals(LATENCY_MIN, String.valueOf(qos.getMinLatency()));
		Assert.assertEquals(LATENCY_MAX, String.valueOf(qos.getMaxLatency()));
		Assert.assertEquals(THROUGHPUT_MIN, String.valueOf(qos.getMinThroughput()));
		Assert.assertEquals(THROUGHPUT_MAX, String.valueOf(qos.getMaxThroughput()));
		Assert.assertEquals(PACKET_LOSS_MIN, String.valueOf(qos.getMinPacketLoss()));
		Assert.assertEquals(PACKET_LOSS_MAX, String.valueOf(qos.getMaxPacketLoss()));

	}

	private PathRequest generateSampleRequest() {

		PathRequest request = new PathRequest();

		Source source = PathRequestHelper.generateSource(SRC_IP, SRC_LINK_PORT, SRC_TRANSPORT_PORT);
		Destination destination = PathRequestHelper.generateDestination(DST_IP, DST_LINK_PORT, DST_TRANSPORT_PORT);

		request.setSource(source);
		request.setDestination(destination);
		request.setQosPolicy(generateSampleQosPolicy());
		request.setLabel(TOS);

		return request;
	}

	private QosPolicy generateSampleQosPolicy() {

		QosPolicy qos = new QosPolicy();

		Jitter jitter = PathRequestHelper.generateJitter(JITTER_MIN, JITTER_MAX, DELAY, TIMEOUT, PRIORITY);
		Latency latency = PathRequestHelper.generateLatency(LATENCY_MIN, LATENCY_MAX, DELAY, TIMEOUT, PRIORITY);
		PacketLoss packetLoss = PathRequestHelper.generatePacketLoss(PACKET_LOSS_MIN, PACKET_LOSS_MAX, DELAY, TIMEOUT, PRIORITY);
		Throughput throughput = PathRequestHelper.generateThroughPut(THROUGHPUT_MIN, THROUGHPUT_MAX, DELAY, TIMEOUT, PRIORITY);

		qos.setJitter(jitter);
		qos.setLatency(latency);
		qos.setPacketLoss(packetLoss);
		qos.setThroughput(throughput);

		return qos;
	}

}
