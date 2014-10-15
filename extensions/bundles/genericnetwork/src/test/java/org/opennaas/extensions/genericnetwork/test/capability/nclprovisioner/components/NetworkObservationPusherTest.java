package org.opennaas.extensions.genericnetwork.test.capability.nclprovisioner.components;

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

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components.NetworkObservationsPusher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkObservationPusherTest {

	private final static String	RABBITMQ_MESSAGE	= "This is a sample message";

	NetworkObservationsPusher	slaManagerClient;
	NetworkOBservationConsumer	slaManagerServer;

	URI							uri;

	class NetworkOBservationConsumer implements Runnable {

		private Connection	conn;
		private Channel		channel;
		private String		message;
		private String		exchangeName;

		public NetworkOBservationConsumer(String exchangeName) throws IOException {
			this.exchangeName = exchangeName;
			initialize();
		}

		public String getMessage() {
			return message;
		}

		@Override
		public void run() {
			try {
				receiveMessage();
			} catch (Exception e) {
				System.out.println("Error consuming RabbitMQ message : e");
			}
		}

		public void receiveMessage() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
			try {

				channel.exchangeDeclare(exchangeName, "direct");
				String queueName = channel.queueDeclare().getQueue();
				channel.queueBind(queueName, exchangeName, NetworkObservationsPusher.RABBIT_MQ_ROUTING_KEY);

				QueueingConsumer consumer = new QueueingConsumer(channel);

				channel.basicConsume(queueName, true, consumer);
				while (StringUtils.isEmpty(message)) {
					QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					message = new String(delivery.getBody());
				}

			} finally {
				if (channel != null && channel.isOpen())
					channel.close();
				if (conn != null && conn.isOpen())
					conn.close();
			}
		}

		private void initialize() throws IOException {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setPort(5672);
			conn = factory.newConnection();
			channel = conn.createChannel();

		}

	}

	@Before
	public void prepareTest() throws URISyntaxException, IOException {

		uri = new URI("http://localhost:5672");
		slaManagerClient = new NetworkObservationsPusher(uri);

	}

	@After
	public void tearDown() {
		slaManagerClient = null;
	}

	@Test
	public void sendCicuitStatisticsTest() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException,
			ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {

		slaManagerServer = new NetworkOBservationConsumer(NetworkObservationsPusher.CIRCUIT_OBSERVATION_TOPIC);

		Thread thread = new Thread(slaManagerServer);
		thread.start();

		slaManagerClient.sendCircuitStatistics(RABBITMQ_MESSAGE);
		thread.join();

		Assert.assertEquals("RabbitMQ Consumer should have receive following message : " + RABBITMQ_MESSAGE, RABBITMQ_MESSAGE,
				slaManagerServer.getMessage());
	}

	@Test
	public void sendPortStatisticsTest() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException,
			InterruptedException {
		slaManagerServer = new NetworkOBservationConsumer(NetworkObservationsPusher.SWITCH_OBSERVATION_TOPIC);

		Thread thread = new Thread(slaManagerServer);
		thread.start();

		slaManagerClient.sendPortStatistics(RABBITMQ_MESSAGE);
		thread.join();

		Assert.assertEquals("RabbitMQ Consumer should have receive following message : " + RABBITMQ_MESSAGE, RABBITMQ_MESSAGE,
				slaManagerServer.getMessage());

	}

}
