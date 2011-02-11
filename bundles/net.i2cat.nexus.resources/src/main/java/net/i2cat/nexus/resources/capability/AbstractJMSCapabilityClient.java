package net.i2cat.nexus.resources.capability;

import java.util.concurrent.ArrayBlockingQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import net.i2cat.nexus.resources.message.CapabilityMessage;

public class AbstractJMSCapabilityClient implements MessageListener{
	
	protected Session jmsSession = null;
	protected MessageProducer messageProducer = null;
	protected MessageConsumer messageConsumer = null;
	private Connection connection = null;
	protected Destination destination = null;
	protected ArrayBlockingQueue<Message> receivedMessages = null;
	protected String resourceId = null;
	
	public AbstractJMSCapabilityClient(String resourceId) throws CapabilityException{
		this.resourceId = resourceId;
		ConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
		receivedMessages = new ArrayBlockingQueue<Message>(1);
		try{
			connection = factory.createConnection();
			connection.start();
			jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = jmsSession.createTopic(resourceId);
			messageProducer = jmsSession.createProducer(destination);
		}catch(JMSException ex){
			throw new CapabilityException("Problems initializing the JMS infrastructure", ex);
		}
	}
	
	public synchronized void setResourceId(String resourceId) throws CapabilityException{
		try{
			this.resourceId = resourceId;
			destination = jmsSession.createTopic(resourceId);
			messageProducer = jmsSession.createProducer(destination);
		}catch(JMSException ex){
			throw new CapabilityException("Problems initializing the JMS infrastructure", ex);
		}
	}
	
	protected String getMessageSelector(String messageID){
		return "CAPABILITY = '" + messageID + "'";
	}
	
	protected void send(CapabilityMessage message, String capabilityId) throws JMSException{
		Message jmsMessage = jmsSession.createObjectMessage(message);
		jmsMessage.setStringProperty("CAPABILITY", capabilityId + "-" + resourceId);
		messageProducer.send(jmsMessage);
	}
	
	/**
	 * Just call it if you are not going to use this class any more, it will free
	 * the resources it was using
	 */
	public void dispose(){
		try{
			jmsSession.close();
			connection.close();
		}catch(JMSException ex){
			ex.printStackTrace();
		}
	}
	
	public void onMessage(Message jmsMessage) {
		receivedMessages.add(jmsMessage);
	}
	
	protected Message receive() throws InterruptedException{
		return receivedMessages.take();
	}

}