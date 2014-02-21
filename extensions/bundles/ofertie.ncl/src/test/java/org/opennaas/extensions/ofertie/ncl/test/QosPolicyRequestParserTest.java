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
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestHelper;
import org.opennaas.extensions.ofertie.ncl.helpers.QosPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.exceptions.ProvisionerException;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class QosPolicyRequestParserTest {

	@Test
	public void toCircuitRequestTest() throws ProvisionerException {

		QosPolicyRequest qosRequest = QoSPolicyRequestHelper.generateSampleQosPolicyRequest();
		CircuitRequest circuitRequest = QosPolicyRequestParser.toCircuitRequest(qosRequest);

		Assert.assertNotNull(circuitRequest);

		Assert.assertEquals(QoSPolicyRequestHelper.TOS, circuitRequest.getLabel());

		Assert.assertNotNull(circuitRequest.getSource());
		Assert.assertEquals(QoSPolicyRequestHelper.SRC_IP, circuitRequest.getSource().getAddress());
		Assert.assertEquals(QoSPolicyRequestHelper.SRC_PORT, circuitRequest.getSource().getTransportPort());

		Assert.assertNotNull(circuitRequest.getDestination());
		Assert.assertEquals(QoSPolicyRequestHelper.DST_IP, circuitRequest.getDestination().getAddress());
		Assert.assertEquals(QoSPolicyRequestHelper.DST_PORT, circuitRequest.getDestination().getTransportPort());

		Assert.assertNotNull(circuitRequest.getQosPolicy());
		Assert.assertEquals(QoSPolicyRequestHelper.JITTER_MAX, String.valueOf(circuitRequest.getQosPolicy().getMaxJitter()));
		Assert.assertEquals(QoSPolicyRequestHelper.JITTER_MIN, String.valueOf(circuitRequest.getQosPolicy().getMinJitter()));
		Assert.assertEquals(QoSPolicyRequestHelper.LATENCY_MAX, String.valueOf(circuitRequest.getQosPolicy().getMaxLatency()));
		Assert.assertEquals(QoSPolicyRequestHelper.LATENCY_MIN, String.valueOf(circuitRequest.getQosPolicy().getMinLatency()));
		Assert.assertEquals(QoSPolicyRequestHelper.PACKETLOSS_MAX, String.valueOf(circuitRequest.getQosPolicy().getMaxPacketLoss()));
		Assert.assertEquals(QoSPolicyRequestHelper.PACKETLOSS_MIN, String.valueOf(circuitRequest.getQosPolicy().getMinPacketLoss()));
		Assert.assertEquals(QoSPolicyRequestHelper.THROUGHTPUT_MAX, String.valueOf(circuitRequest.getQosPolicy().getMaxThroughput()));
		Assert.assertEquals(QoSPolicyRequestHelper.THROUGHTPUT_MIN, String.valueOf(circuitRequest.getQosPolicy().getMinThroughput()));
	}
}
