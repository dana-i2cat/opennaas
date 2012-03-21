package net.i2cat.luminis.transports.wonesys.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.inject.Inject;

import net.i2cat.luminis.transports.wonesys.ITransport;
import net.i2cat.luminis.transports.wonesys.ITransportListener;
import net.i2cat.luminis.transports.wonesys.WonesysTransport;
import net.i2cat.luminis.transports.wonesys.WonesysTransportException;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class WonesysTransportTest implements ITransportListener
{
	Log	log	= LogFactory.getLog(WonesysTransportTest.class);

	@Inject
	private BundleContext	bundleContext;

	String					hostIpAddress	= "10.10.80.11";
	String					hostPort		= "27773";

	boolean					eventReceived	= false;
	String					receivedMessage;

	@Inject
	private IEventManager	eventManager;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
					   noConsole(),
					   keepRuntimeFolder());
	}

	// FIXME Uncomment to test transport works ok
	// @Test //uses real connection
	public void sendAsyncTest() {

		ProtocolSessionContext sessionContext = newWonesysProtocolSessionContext(hostIpAddress, hostPort);
		try {

			WonesysTransport transport = new WonesysTransport(sessionContext);

			transport.connect();

			String message = "5910ffffffffff01ffffffff0000b700"; // get inv command

			transport.sendAsync(message);
			// if socket is not connected will throw an exception

			// TODO check message reached device

			transport.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (WonesysTransportException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	// FIXME Uncomment to test transport works ok
	//@Test //uses real connection
	public void sendReceiveTest() {

		ProtocolSessionContext sessionContext = newWonesysProtocolSessionContext(hostIpAddress, hostPort);
		try {

			ITransport transport = new WonesysTransport(sessionContext);

			int regNum = registerAsListener(transport);

			eventReceived = false;

			transport.connect();

			String message = "5910ffffffffff01ffffffff0000b700"; // get inv command

			transport.sendAsync(message);
			log.debug("Message sent: " + message);
			Thread.sleep(5000);

			Assert.assertTrue(eventReceived);
			log.debug("Message received: " + receivedMessage);
			Assert.assertTrue(receivedMessage.startsWith("5910ffffffffff01ffffffff"));

			unregisterAsListener(regNum);
			transport.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	private ProtocolSessionContext newWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		return protocolSessionContext;
	}

	private int registerAsListener(ITransport transport) {
		// register as a transport listener
		// this will receive all events from its wonesysTransport

		String topic = RawSocketTransport.ALL_EVENTS_TOPIC;
		Properties properties = new Properties();
		properties.setProperty(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, transport.getTransportID());
		EventFilter filter = new EventFilter(new String[] { topic }, properties);

		return eventManager.registerEventHandler(this, filter);
	}

	private void unregisterAsListener(int regNum) {
		eventManager.unregisterHandler(regNum);
	}

	@Override
	public void handleEvent(Event event) {
		eventReceived = true;
		String transportId = (String) event.getProperty(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME);
		receivedMessage = (String) event.getProperty(RawSocketTransport.MESSAGE_PROPERTY_NAME);
		log.debug("EventReceived! TransportID=" + transportId + " message=" + receivedMessage);
	}

}
