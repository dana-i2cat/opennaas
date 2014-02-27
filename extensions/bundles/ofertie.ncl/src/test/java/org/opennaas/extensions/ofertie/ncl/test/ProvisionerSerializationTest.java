package org.opennaas.extensions.ofertie.ncl.test;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Destination;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Source;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;

public class ProvisionerSerializationTest {

	@Test
	public void flowSerializationTest() throws SerializationException {
		QosPolicyRequest original = generateSampleFlow();
		String xml = ObjectSerializer.toXml(original);
		QosPolicyRequest generated = (QosPolicyRequest) ObjectSerializer.fromXml(xml, QosPolicyRequest.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void flowRequestSerializationTest() throws SerializationException {
		QosPolicyRequest original = generateSampleFlow();
		String xml = ObjectSerializer.toXml(original);
		QosPolicyRequest generated = (QosPolicyRequest) ObjectSerializer.fromXml(xml, QosPolicyRequest.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void qoSPolicyRequestsWrapperSerializationTes() throws SerializationException {
		QoSPolicyRequestsWrapper original = generateSampleQoSPolicyRequestsWrapper();
		String xml = ObjectSerializer.toXml(original);
		QoSPolicyRequestsWrapper generated = (QoSPolicyRequestsWrapper) ObjectSerializer.fromXml(xml, QoSPolicyRequestsWrapper.class);
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
		System.out.println(xml);
	}

	// <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	// <flow>
	// <id>1</id>
	// <flowRequest>
	// <requestId>1</requestId>
	// <sourceIPAddress>192.168.0.1</sourceIPAddress>
	// <destinationIPAddress>192.168.0.2</destinationIPAddress>
	// <sourcePort>8080</sourcePort>
	// <destinationPort>8080</destinationPort>
	// <tos>1</tos>
	// <sourceVlanId>1100</sourceVlanId>
	// <destinationVlanId>1100</destinationVlanId>
	// <qoSRequirements>
	// <minDelay>-1</minDelay>
	// <maxDelay>10</maxDelay>
	// <minJitter>-1</minJitter>
	// <maxJitter>10</maxJitter>
	// <minBandwidth>100000000</minBandwidth>
	// <maxBandwidth>-1</maxBandwidth>
	// <minPacketLoss>-1</minPacketLoss>
	// <maxPacketLoss>10</maxPacketLoss>
	// </qoSRequirements>
	// </flowRequest>
	// </flow>
	private QosPolicyRequest generateSampleFlow() {

		QosPolicyRequest req = new QosPolicyRequest();

		Source source = new Source();
		source.setAddress("192.168.0.1");
		source.setPort("8080");
		req.setSource(source);

		Destination destination = new Destination();
		destination.setAddress("192.168.0.2");
		destination.setPort("8080");
		req.setDestination(destination);

		req.setLabel("1");

		QosPolicy qosPolicy = new QosPolicy();

		Latency latency = new Latency();
		latency.setMin(null);
		latency.setMax("10");
		qosPolicy.setLatency(latency);

		Jitter jitter = new Jitter();
		jitter.setMin(null);
		jitter.setMax("10");
		qosPolicy.setJitter(jitter);

		Throughput throughput = new Throughput();
		throughput.setMin(String.valueOf(100 * 1000 * 1000));
		throughput.setMax(null);
		qosPolicy.setThroughput(throughput);

		PacketLoss packetLoss = new PacketLoss();
		packetLoss.setMin(null);
		packetLoss.setMax("10");
		qosPolicy.setPacketLoss(packetLoss);

		req.setQosPolicy(qosPolicy);

		return req;

	}

	private QoSPolicyRequestsWrapper generateSampleQoSPolicyRequestsWrapper() {

		Map<String, QosPolicyRequest> requests = new HashMap<String, QosPolicyRequest>();
		for (int i = 0; i <= 3; i++) {
			requests.put(String.valueOf(i), generateSampleFlow());
		}

		return new QoSPolicyRequestsWrapper(requests);
	}

}
