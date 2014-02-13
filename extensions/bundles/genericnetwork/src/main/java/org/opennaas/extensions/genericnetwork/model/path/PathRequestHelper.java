package org.opennaas.extensions.genericnetwork.model.path;

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

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class PathRequestHelper {

	public static Source generateSource(String ipAddress, String linkPort, String transportPort) {

		Source source = new Source();

		source.setAddress(ipAddress);
		source.setLinkPort(linkPort);
		source.setTransportPort(transportPort);

		return source;
	}

	public static Destination generateDestination(String ipAddress, String linkPort, String transportPort) {

		Destination destination = new Destination();

		destination.setAddress(ipAddress);
		destination.setLinkPort(linkPort);
		destination.setTransportPort(transportPort);

		return destination;
	}

	public static QosPolicy generateQoSPolicy() {
		QosPolicy qosPolicy = new QosPolicy();

		return qosPolicy;
	}

	public static Jitter generateJitter(String minValue, String maxValue, String delay, String timeout, String priority) {

		Jitter jitter = new Jitter();

		jitter.setMax(maxValue);
		jitter.setMin(minValue);
		jitter.setTimeout(timeout);
		jitter.setDelay(delay);
		jitter.setPriority(priority);

		return jitter;
	}

	public static Latency generateLatency(String minValue, String maxValue, String delay, String timeout, String priority) {

		Latency latency = new Latency();

		latency.setMax(maxValue);
		latency.setMin(minValue);
		latency.setTimeout(timeout);
		latency.setDelay(delay);
		latency.setPriority(priority);

		return latency;
	}

	public static PacketLoss generatePacketLoss(String minValue, String maxValue, String delay, String timeout, String priority) {

		PacketLoss packetLoss = new PacketLoss();

		packetLoss.setMax(maxValue);
		packetLoss.setMin(minValue);
		packetLoss.setTimeout(timeout);
		packetLoss.setDelay(delay);
		packetLoss.setPriority(priority);

		return packetLoss;
	}

	public static Throughput generateThroughPut(String minValue, String maxValue, String delay, String timeout, String priority) {

		Throughput throughPut = new Throughput();

		throughPut.setMax(maxValue);
		throughPut.setMin(minValue);
		throughPut.setTimeout(timeout);
		throughPut.setDelay(delay);
		throughPut.setPriority(priority);

		return throughPut;
	}
}
