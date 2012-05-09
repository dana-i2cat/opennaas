package org.opennaas.itests.roadm.protocol;

import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.transports.ITransport;
import org.opennaas.extensions.roadm.wonesys.transports.ITransportListener;
import org.opennaas.extensions.roadm.wonesys.transports.WonesysTransport;
import org.opennaas.extensions.roadm.wonesys.transports.WonesysTransportException;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;



@RunWith(JUnit4TestRunner.class)
public class WonesysTransportTest implements ITransportListener
{
	Log						log				= LogFactory.getLog(WonesysTransportTest.class);

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
	// @Test //uses real connection
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
