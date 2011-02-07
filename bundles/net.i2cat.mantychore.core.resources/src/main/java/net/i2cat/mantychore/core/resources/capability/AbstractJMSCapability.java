package net.i2cat.mantychore.core.resources.capability;

import java.util.Enumeration;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.mantychore.core.resources.descriptor.CapabilityDescriptor;
import net.i2cat.mantychore.core.resources.message.ICapabilityMessage;

/**
 * This class provides an abstract implementation for the ICapability
 * interface. This class must be extended by each module and must implement the
 * abstract lifecycle methods.
 * 
 * @author Mathieu Lemay (ITI)
 * @author Scott Campbell(CRC)
 * @author Eduard Grasa (i2CAT)
 * 
 */
public abstract class AbstractJMSCapability extends AbstractCapability implements MessageListener {
	/** logger */
	private Logger logger = LoggerFactory
			.getLogger(AbstractJMSCapability.class);

	protected static transient ConnectionFactory factory;
	protected transient Connection connection;
	protected transient Session session;
	private transient MessageProducer producer = null;
	private transient MessageConsumer messageConsumer = null;
	protected Destination destination = null;
	protected Message newestMessage = null;
	protected static final String brokerURL = "vm://localhost";

	public AbstractJMSCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor, resourceId);
	}

	private void initializeJMS() throws CapabilityException {
		try {
			factory = new ActiveMQConnectionFactory(brokerURL);
			connection = factory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic(resourceId);

			// Create the producer
			producer = session.createProducer(destination);

			// create the consumer and listen for message to this module
			String selector = "CAPABILITY = '" + capabilityId + "-" + resourceId + "'";
			messageConsumer = session.createConsumer(destination, selector);
			messageConsumer.setMessageListener(this);
		}catch (JMSException e) {
			throw new CapabilityException("Error shutting down JMS queues", e);
		}
	}
	
	private void shutdownJMS() throws CapabilityException {
		try {
			producer.close();
			messageConsumer.close();
			session.close();
			connection.stop();
			connection.close();
		}catch (JMSException e) {
			throw new CapabilityException("Error shutting down JMS queues", e);
		}
	}
	
	@Override
	/**
	 * Initialize the JMS queues and call the subclass initializeCapability operation to 
	 * continue with the capability initialization
	 */
	public void initialize() throws CapabilityException {
		initializeJMS();
		initializeCapability();
		state = State.INITIALIZED;
	}
	
	@Override
	/**
	 * Shutdown the JMS queues and call the subclass shutdownCapability operation to 
	 * continue with the capability shutdown
	 */
	public void shutdown() throws CapabilityException {
		shutdownJMS();
		shutdownCapability();
		state = State.SHUTDOWN;
	}

	public void sendMessage(ICapabilityMessage payload, Properties properties) throws CapabilityException {
		try {
			payload.setRequestor(capabilityId + "-" + resourceId);
			Message message = session.createObjectMessage(payload);
			message.setJMSCorrelationID(payload.getMessageID());

			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String propertyName = (String) keys.nextElement();
				message.setStringProperty(propertyName, properties.getProperty(propertyName));
			}

			logger.debug(capabilityId +"-" + resourceId + " Sending Message with properties " + properties.toString()
					+ ". Payload: " + payload.toString());
			producer.send(message);
		}
		catch(JMSException e) {
			CapabilityException exception = new CapabilityException(e.getMessage(), e);
			exception.setCapabilityDescriptor(descriptor);
			throw exception;
		}
	}
	
	public void onMessage(Message msg) {
		ObjectMessage message = (ObjectMessage) msg;

		try {
			newestMessage = msg;
			ICapabilityMessage engineMessage = (ICapabilityMessage) message.getObject();
			logger.debug(capabilityId +"-" + resourceId + " received message: " + engineMessage.toString());
			handleMessage(engineMessage);
		}
		catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public Session getSession() {
		return session;
	}

	public Destination getDestination() {
		return destination;
	}

	protected abstract void handleMessage(ICapabilityMessage msg) throws JMSException;
}
