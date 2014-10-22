package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Exchange;
import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * <p>
 * RabbitMQ client to push network statistics.
 * </p>
 * <p>
 * This class offers methods to report both port statistics and circuit statistics to an SLA Manager using RabbitMQ. The client does not know which
 * {@link Queue} the consumer(s) uses. Therefore, for each type of message, the client defines a {@link Exchange}, so the server would be able to
 * dispatch this type of message to the queues consuming this exchange.
 * </p>
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkObservationsPusher {

	private Log					log								= LogFactory.getLog(NetworkObservationsPusher.class);

	public static final String	CIRCUIT_OBSERVATION_EXCHANGE	= "observations_NCL_qos_flow";
	public static final String	SWITCH_OBSERVATION_EXCHANGE		= "observations_NCL_qos_switch";
	public static final String	OBSERVATIONS_CONTENT_TYPE		= "text/csv";
	public static final String	RABBIT_MQ_ROUTING_KEY			= "ofertie";

	private URI					slaManagerUri;

	public NetworkObservationsPusher(URI slaManagerUri) {
		this.slaManagerUri = slaManagerUri;
	}

	public void sendCircuitStatistics(String circuitStatisticsCSV) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException,
			IOException {
		sendStatistics(CIRCUIT_OBSERVATION_EXCHANGE, circuitStatisticsCSV);

	}

	public void sendPortStatistics(String portStatisticsCSV) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException {
		sendStatistics(SWITCH_OBSERVATION_EXCHANGE, portStatisticsCSV);

	}

	private void sendStatistics(String topicName, String csvMessage) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException,
			IOException {

		Connection conn = null;
		Channel channel = null;

		try {

			log.debug("Establishing RabbitMQ connection with sla manager with URI: " + slaManagerUri);

			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(slaManagerUri.getHost());

			conn = factory.newConnection();
			channel = conn.createChannel();

			channel.exchangeDeclare(topicName, "direct");

			log.debug("Publishing message in RabbitMQ channel.");

			channel.basicPublish(topicName, RABBIT_MQ_ROUTING_KEY, new AMQP.BasicProperties.Builder()
					.contentType(OBSERVATIONS_CONTENT_TYPE).build(), csvMessage.getBytes());

			log.debug("Message successfully sent to SLA manager.");

		} finally {
			if (channel != null && channel.isOpen())
				channel.close();
			if (conn != null && conn.isOpen())
				conn.close();

			log.debug("Connection to RabbitMQ server closed.");

		}

	}
}
