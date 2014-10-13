package org.opennaas.extensions.ofertie.ncl.provisioner.components;

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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Pushes observations to SLAManager using rabbitMQ
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SLAManagerObservationsPusher {

	private static final String	RABBIT_MQ_NETWORK_QOS_OBSERVATIONS_EXCHANGE	= "observation_network_qos";
	private static final String	RABBIT_MQ_NCL_QOS_OBSERVATIONS_EXCHANGE		= "observation_ncl_qos";
	private static final String	RABBIT_MQ_OBSERVATIONS_CONTENT_TYPE			= "text/csv";
	private static final String	RABBIT_MQ_ROUTING_KEY						= "ofertie";

	private URI					connectionUri;

	/**
	 * 
	 * @param connectionUri
	 *            a URI of the form "amqp://userName:password@hostName:portNumber/virtualHost"
	 */
	public SLAManagerObservationsPusher(URI connectionUri) {
		super();
		this.connectionUri = connectionUri;
	}

	/**
	 * Sends given csvMessage to SLAManager rabbitMQ using SLAManager defined RABBIT_MQ_NCL_QOS_OBSERVATIONS_EXCHANGE, RABBIT_MQ_ROUTING_KEY and
	 * RABBIT_MQ_OBSERVATIONS_CONTENT_TYPE.
	 * 
	 * @param csvMessage
	 *            CSV message with QoS observations.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void sendNCLObservations(String csvMessage) throws IOException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		sendObservations(csvMessage, RABBIT_MQ_NCL_QOS_OBSERVATIONS_EXCHANGE);
	}

	/**
	 * Sends given csvMessage to SLAManager rabbitMQ using SLAManager defined RABBIT_MQ_NETWORK_QOS_OBSERVATIONS_EXCHANGE, RABBIT_MQ_ROUTING_KEY and
	 * RABBIT_MQ_OBSERVATIONS_CONTENT_TYPE.
	 * 
	 * @param csvMessage
	 *            CSV message with QoS observations.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void sendNetworkObservations(String csvMessage) throws IOException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		sendObservations(csvMessage, RABBIT_MQ_NETWORK_QOS_OBSERVATIONS_EXCHANGE);
	}

	/**
	 * Sends given observationsCSVMessage to SLAManager rabbitMQ using given exchangeName and SLAManager defined RABBIT_MQ_ROUTING_KEY and
	 * RABBIT_MQ_OBSERVATIONS_CONTENT_TYPE.
	 * 
	 * It uses a dedicated connection for sending the message, which is closed afterwards.
	 * 
	 * TODO: More efficient implementation reusing connection between separated calls.
	 * 
	 * @param csvMessage
	 *            CSV message with QoS observations.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private void sendObservations(String observationsCSVMessage, String exchangeName) throws IOException, KeyManagementException,
			NoSuchAlgorithmException, URISyntaxException {

		Connection conn = null;
		Channel channel = null;

		try {

			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri(connectionUri);
			conn = factory.newConnection();

			channel = conn.createChannel();

			// Declare exchange and queue, and bind them:
			// - a durable, non-autodelete exchange of "direct" type
			// - a non-durable, exclusive, autodelete queue with a generated name
			channel.exchangeDeclare(exchangeName, "direct", true);
			// typical way to declare a queue when only one client wants to work with it:
			// it doesn’t need a well-known name,
			// no other client can use it (exclusive)
			// and will be cleaned up automatically (autodelete).
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exchangeName, RABBIT_MQ_ROUTING_KEY);

			// Publish the message
			channel.basicPublish(exchangeName, RABBIT_MQ_ROUTING_KEY,
					new AMQP.BasicProperties.Builder().contentType(RABBIT_MQ_OBSERVATIONS_CONTENT_TYPE).build(),
					observationsCSVMessage.getBytes());

		} finally {
			// Close channel and connection
			if (channel != null)
				channel.close();
			if (conn != null)
				conn.close();
		}
	}

}
