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

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestHelper;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class QosPolicyRequestHelperTest {

	private static final String	NEW_VALUE	= "12345678987654321";

	@Test
	public void mergeJitterTest() {

		// case 1 : original jitter was null, but updated is not null
		Jitter originalJitter = null;
		Jitter updatedJitter = new Jitter();
		Assert.assertEquals("Returned jitter should be the updated one.", updatedJitter,
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter));

		// case 2 : original jitter was not null, but updated is null
		originalJitter = new Jitter();
		updatedJitter = null;
		Assert.assertEquals("Returned jitter should be the original one.", originalJitter,
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter));

		// case 3 : both jitters are equals
		originalJitter = QoSPolicyRequestHelper.generateSampleJitter();
		updatedJitter = QoSPolicyRequestHelper.generateSampleJitter();

		Assert.assertEquals("Returned jitter should be equals to original jitter.", originalJitter,
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter));
		Assert.assertEquals("Returned jitter should be equals to  updated jitter.", updatedJitter,
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter));

		// case 4 : modify parameters
		updatedJitter.setDelay(NEW_VALUE);
		updatedJitter.setMax(NEW_VALUE);
		updatedJitter.setMin(NEW_VALUE);
		updatedJitter.setPriority(NEW_VALUE);
		updatedJitter.setTimeout(NEW_VALUE);

		Assert.assertEquals("Returned jitter delay should be equals to  updated one.", updatedJitter.getDelay(),
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter).getDelay());
		Assert.assertEquals("Returned min jitter  should be equals to  updated one.", updatedJitter.getMin(),
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter).getMin());
		Assert.assertEquals("Returned max jitter  should be equals to  updated one.", updatedJitter.getMax(),
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter).getMax());
		Assert.assertEquals("Returned jitter priority should be equals to  updated one.", updatedJitter.getPriority(),
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter).getPriority());
		Assert.assertEquals("Returned jitter timeout should be equals to  updated one.", updatedJitter.getTimeout(),
				QoSPolicyRequestHelper.mergeJitter(originalJitter, updatedJitter).getTimeout());

	}

	@Test
	public void mergePacketlossTest() {

		// case 1 : original packetloss was null, but updated is not null
		PacketLoss originalPL = null;
		PacketLoss updatedPL = new PacketLoss();
		Assert.assertEquals("Returned packetloss should be the updated one.", updatedPL,
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL));

		// case 2 : original packetloss was not null, but updated is null
		originalPL = new PacketLoss();
		updatedPL = null;
		Assert.assertEquals("Returned packetloss should be the original one.", originalPL,
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL));

		// case 3 : both packetloss are equals
		originalPL = QoSPolicyRequestHelper.generateSamplePacketLoss();
		updatedPL = QoSPolicyRequestHelper.generateSamplePacketLoss();

		Assert.assertEquals("Returned packetloss should be equals to original packetloss.", originalPL,
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL));
		Assert.assertEquals("Returned packetloss should be equals to  updated packetloss.", updatedPL,
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL));

		// case 4 : modify parameters
		updatedPL.setDelay(NEW_VALUE);
		updatedPL.setMax(NEW_VALUE);
		updatedPL.setMin(NEW_VALUE);
		updatedPL.setPriority(NEW_VALUE);
		updatedPL.setTimeout(NEW_VALUE);

		Assert.assertEquals("Returned packetloss delay should be equals to  updated one.", updatedPL.getDelay(),
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL).getDelay());
		Assert.assertEquals("Returned min packetloss  should be equals to  updated one.", updatedPL.getMin(),
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL).getMin());
		Assert.assertEquals("Returned max packetloss  should be equals to  updated one.", updatedPL.getMax(),
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL).getMax());
		Assert.assertEquals("Returned packetloss priority should be equals to  updated one.", updatedPL.getPriority(),
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL).getPriority());
		Assert.assertEquals("Returned packetloss timeout should be equals to  updated one.", updatedPL.getTimeout(),
				QoSPolicyRequestHelper.mergePacketLoss(originalPL, updatedPL).getTimeout());

	}

	@Test
	public void mergeThroughputTest() {

		// case 1 : original throughput was null, but updated is not null
		Throughput originalThroughput = null;
		Throughput updatedThroughput = new Throughput();
		Assert.assertEquals("Returned throughput should be the updated one.", updatedThroughput,
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput));

		// case 2 : original throughput was not null, but updated is null
		originalThroughput = new Throughput();
		updatedThroughput = null;
		Assert.assertEquals("Returned throughput should be the original one.", originalThroughput,
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput));

		// case 3 : both throughput are equals
		originalThroughput = QoSPolicyRequestHelper.generateSampleThroughput();
		updatedThroughput = QoSPolicyRequestHelper.generateSampleThroughput();

		Assert.assertEquals("Returned throughput should be equals to original throughput.", originalThroughput,
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput));
		Assert.assertEquals("Returned throughput should be equals to  updated throughput.", updatedThroughput,
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput));

		// case 4 : modify parameters
		updatedThroughput.setDelay(NEW_VALUE);
		updatedThroughput.setMax(NEW_VALUE);
		updatedThroughput.setMin(NEW_VALUE);
		updatedThroughput.setPriority(NEW_VALUE);
		updatedThroughput.setTimeout(NEW_VALUE);

		Assert.assertEquals("Returned throughput delay should be equals to  updated one.", updatedThroughput.getDelay(),
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput).getDelay());
		Assert.assertEquals("Returned min throughput  should be equals to  updated one.", updatedThroughput.getMin(),
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput).getMin());
		Assert.assertEquals("Returned max throughput  should be equals to  updated one.", updatedThroughput.getMax(),
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput).getMax());
		Assert.assertEquals("Returned throughput priority should be equals to  updated one.", updatedThroughput.getPriority(),
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput).getPriority());
		Assert.assertEquals("Returned throughput timeout should be equals to  updated one.", updatedThroughput.getTimeout(),
				QoSPolicyRequestHelper.mergeThroughput(originalThroughput, updatedThroughput).getTimeout());

	}

	@Test
	public void mergeLatencyTest() {

		// case 1 : original latency was null, but updated is not null
		Latency originalLatency = null;
		Latency updatedLatency = new Latency();
		Assert.assertEquals("Returned latency should be the updated one.", updatedLatency,
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency));

		// case 2 : original latency was not null, but updated is null
		originalLatency = new Latency();
		updatedLatency = null;
		Assert.assertEquals("Returned latency should be the original one.", originalLatency,
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency));

		// case 3 : both latencies are equals
		originalLatency = QoSPolicyRequestHelper.generateSampleLatency();
		updatedLatency = QoSPolicyRequestHelper.generateSampleLatency();

		Assert.assertEquals("Returned latency should be equals to original latency.", originalLatency,
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency));
		Assert.assertEquals("Returned latency should be equals to  updated latency.", updatedLatency,
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency));

		// case 4 : modify parameters
		updatedLatency.setDelay(NEW_VALUE);
		updatedLatency.setMax(NEW_VALUE);
		updatedLatency.setMin(NEW_VALUE);
		updatedLatency.setPriority(NEW_VALUE);
		updatedLatency.setTimeout(NEW_VALUE);

		Assert.assertEquals("Returned latency delay should be equals to  updated one.", updatedLatency.getDelay(),
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency).getDelay());
		Assert.assertEquals("Returned min latency  should be equals to  updated one.", updatedLatency.getMin(),
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency).getMin());
		Assert.assertEquals("Returned max latency  should be equals to  updated one.", updatedLatency.getMax(),
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency).getMax());
		Assert.assertEquals("Returned latency priority should be equals to  updated one.", updatedLatency.getPriority(),
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency).getPriority());
		Assert.assertEquals("Returned latency timeout should be equals to  updated one.", updatedLatency.getTimeout(),
				QoSPolicyRequestHelper.mergeLatency(originalLatency, updatedLatency).getTimeout());

	}

	@Test
	public void mergeQosTest() {

		// case 1 : original Qos was null, but updated is not null
		QosPolicy originalQos = null;
		QosPolicy updatedQos = new QosPolicy();
		Assert.assertEquals("Returned QosPolicy should be the updated one.", updatedQos,
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos));

		// case 2 : original Qos was not null, but updated is null
		originalQos = new QosPolicy();
		updatedQos = null;
		Assert.assertEquals("Returned QosPolicy should be the original one.", originalQos,
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos));

		// case 3 : both Qos policies are equals
		originalQos = QoSPolicyRequestHelper.generateSampleQosPolicy();
		updatedQos = QoSPolicyRequestHelper.generateSampleQosPolicy();

		Assert.assertEquals("Returned latency should be equals to original latency.", originalQos,
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos));
		Assert.assertEquals("Returned latency should be equals to  updated latency.", updatedQos,
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos));

		// case 4 : modify random parameters parameters
		updatedQos.getJitter().setMax(NEW_VALUE);
		updatedQos.getLatency().setMin(NEW_VALUE);
		updatedQos.getPacketLoss().setPriority(NEW_VALUE);
		updatedQos.getThroughput().setTimeout(NEW_VALUE);

		Assert.assertEquals("New Qos should contain modified jitter values.", updatedQos.getJitter(),
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos).getJitter());
		Assert.assertEquals("New Qos should contain modified latency values.", updatedQos.getLatency(),
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos).getLatency());
		Assert.assertEquals("New Qos should contain modified packetloss values.", updatedQos.getPacketLoss(),
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos).getPacketLoss());
		Assert.assertEquals("New Qos should contain modified throughput values.", updatedQos.getThroughput(),
				QoSPolicyRequestHelper.mergeQosPolicies(originalQos, updatedQos).getThroughput());
	}
}
