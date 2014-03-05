package org.opennaas.extensions.ofertie.ncl.helpers;

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

import org.opennaas.extensions.genericnetwork.model.circuit.QoSPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Jitter;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Latency;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.PacketLoss;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicy;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.Throughput;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class QoSPolicyParser {

	/**
	 * Translates the QoS of GenericNetwork model to the QoS of NCLProvisioner.
	 * 
	 * @param genericNetQos
	 * @return
	 */
	public static QosPolicy fromGenericNetworkQoS(QoSPolicy genericNetQos) {

		QosPolicy qos = new QosPolicy();

		qos.setLatency(parseLatency(genericNetQos));
		qos.setPacketLoss(parsePacketLoss(genericNetQos));
		qos.setThroughput(parseThroughput(genericNetQos));
		qos.setJitter(parseJitter(genericNetQos));

		return qos;
	}

	public static Latency parseLatency(QoSPolicy genericNetQos) {

		Latency latency = new Latency();

		latency.setMax(String.valueOf(genericNetQos.getMaxLatency()));
		latency.setMin(String.valueOf(genericNetQos.getMinLatency()));

		return latency;
	}

	public static PacketLoss parsePacketLoss(QoSPolicy genericNetQos) {

		PacketLoss packetLoss = new PacketLoss();

		packetLoss.setMax(String.valueOf(genericNetQos.getMaxPacketLoss()));
		packetLoss.setMin(String.valueOf(genericNetQos.getMinPacketLoss()));

		return packetLoss;
	}

	public static Throughput parseThroughput(QoSPolicy genericNetQos) {

		Throughput throughput = new Throughput();

		throughput.setMax(String.valueOf(genericNetQos.getMaxThroughput()));
		throughput.setMin(String.valueOf(genericNetQos.getMinThroughput()));

		return throughput;
	}

	public static Jitter parseJitter(QoSPolicy genericNetQos) {

		Jitter jitter = new Jitter();

		jitter.setMax(String.valueOf(genericNetQos.getMaxJitter()));
		jitter.setMin(String.valueOf(genericNetQos.getMinJitter()));

		return jitter;
	}
}
