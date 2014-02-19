package org.opennaas.extensions.genericnetwork.test.model.helpers;

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
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitParser;
import org.opennaas.extensions.genericnetwork.model.helpers.CircuitRequestHelper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

public class CircuitParserTest {

	private static final String	SRC_IP				= "192.168.1.10";
	private static final String	SRC_LINK_PORT		= "eth1";
	private static final String	SRC_TRANSPORT_PORT	= "80";

	private static final String	DST_IP				= "192.168.1.11";
	private static final String	DST_LINK_PORT		= "eth2";
	private static final String	DST_TRANSPORT_PORT	= "80";

	private static final String	TOS					= "4";

	private static final int	JITTER_MIN			= 0;
	private static final int	JITTER_MAX			= 2;
	private static final int	LATENCY_MIN			= 0;
	private static final int	LATENCY_MAX			= 1;
	private static final int	PACKET_LOSS_MIN		= 0;
	private static final int	PACKET_LOSS_MAX		= 1;
	private static final int	THROUGHPUT_MIN		= 0;
	private static final int	THROUGHPUT_MAX		= 5;

	@Test
	public void circuitRequestToCircuitTest() {

		CircuitRequest request = generateSampleRequest();
		Circuit circuit = CircuitParser.circuitRequestToCircuit(request);

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
		Assert.assertEquals(JITTER_MIN, qos.getMinJitter());
		Assert.assertEquals(JITTER_MAX, qos.getMaxJitter());
		Assert.assertEquals(LATENCY_MIN, qos.getMinLatency());
		Assert.assertEquals(LATENCY_MAX, qos.getMaxLatency());
		Assert.assertEquals(THROUGHPUT_MIN, qos.getMinThroughput());
		Assert.assertEquals(THROUGHPUT_MAX, qos.getMaxThroughput());
		Assert.assertEquals(PACKET_LOSS_MIN, qos.getMinPacketLoss());
		Assert.assertEquals(PACKET_LOSS_MAX, qos.getMaxPacketLoss());

	}

	private CircuitRequest generateSampleRequest() {

		Source source = CircuitRequestHelper.generateSource(SRC_IP, SRC_LINK_PORT, SRC_TRANSPORT_PORT);
		Destination destination = CircuitRequestHelper.generateDestination(DST_IP, DST_LINK_PORT, DST_TRANSPORT_PORT);
		QoSPolicy policy = generateSamplePolicy();

		CircuitRequest request = CircuitRequestHelper.generateCircuitRequest(source, destination, TOS, policy, null);

		return request;
	}

	private QoSPolicy generateSamplePolicy() {

		QoSPolicy policy = CircuitRequestHelper.generateQoSPolicy(JITTER_MIN, JITTER_MAX, LATENCY_MIN, LATENCY_MAX, THROUGHPUT_MIN, THROUGHPUT_MAX,
				PACKET_LOSS_MIN, PACKET_LOSS_MAX);

		return policy;
	}

}
