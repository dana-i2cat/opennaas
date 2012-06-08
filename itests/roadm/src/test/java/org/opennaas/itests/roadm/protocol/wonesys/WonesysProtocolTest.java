package org.opennaas.itests.roadm.protocol.wonesys;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.GetInventoryCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannels;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.IWonesysAlarmConfigurator;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarmEvent;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarmEventFilter;
import org.opennaas.extensions.roadm.wonesys.transports.ITransport;
import org.opennaas.extensions.roadm.wonesys.transports.ITransportListener;
import org.opennaas.extensions.roadm.wonesys.transports.WonesysTransport;
import org.opennaas.extensions.roadm.wonesys.transports.WonesysTransportException;
import org.opennaas.extensions.roadm.wonesys.transports.rawsocket.RawSocketTransport;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import uk.co.westhawk.snmp.pdu.OneTrapPduv2;
import uk.co.westhawk.snmp.stack.AsnInteger;
import uk.co.westhawk.snmp.stack.AsnObject;
import uk.co.westhawk.snmp.stack.AsnObjectId;
import uk.co.westhawk.snmp.stack.AsnOctets;
import uk.co.westhawk.snmp.stack.AsnUnsInteger;
import uk.co.westhawk.snmp.stack.PduException;
import uk.co.westhawk.snmp.stack.SnmpContext;
import uk.co.westhawk.snmp.stack.SnmpContextv2c;
import uk.co.westhawk.snmp.stack.TrapPduv2;
import uk.co.westhawk.snmp.stack.varbind;

@RunWith(JUnit4TestRunner.class)
public class WonesysProtocolTest implements EventHandler, ITransportListener
{
	private static Log					log				= LogFactory.getLog(WonesysProtocolTest.class);

	private String						alarmsPort		= "32162";										// SNMP traps port (162). if different,
																										// 162 needs to be redirected to this port
	private String						resourceId		= "Proteus-Pedrosa";
	private String						hostIpAddress	= "10.10.80.11";
	private String						hostPort		= "27773";										// in order to receive traps.

	private long						alarmWaittime	= 5 * 1000;									// 5sec

	private final CountDownLatch		alarmReceived	= new CountDownLatch(1);

	boolean								eventReceived	= false;
	String								receivedMessage;

	// Required services
	@Inject
	private BundleContext				bundleContext;

	@Inject
	private IProtocolManager			protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.protocols.wonesys)")
	BlueprintContainer					wonesysProtocolService;

	@Inject
	private IEventManager				eventManager;

	@Inject
	private IWonesysAlarmConfigurator	alarmConfig;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				noConsole(),
				keepRuntimeFolder());
	}

	// FIXME Uncomment to test transport works ok
	//
	@Test
	// uses real connection
	@Ignore
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
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	// FIXME Uncomment to test transport works ok
	@Test
	// uses real connection
	@Ignore
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
			Assert.fail(e.getLocalizedMessage());
		} catch (WonesysTransportException e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	/**
	 * 0. Obtain services <br>
	 * 1. Use WonesysAlarmActivator to configure and activate alarms <br>
	 * 2. Create a WonesysAlarmEventFilter <br>
	 * 3. Use eventManager to register an EventHandler (this), using created filter <br>
	 * 4. Trigger an alarm <br>
	 * 5. Wait for alarm reception <br>
	 * 6. Receive the alarm and checks <br>
	 * 7. Unregister Handlers and disable alarms <br>
	 * 
	 * @throws PduException
	 * 
	 * @throws CommandException
	 * @throws ProtocolException
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAlarms() throws IOException {

		// set up

		Properties alarmProperties = new Properties();
		alarmProperties.setProperty(IWonesysAlarmConfigurator.ALARM_PORT_PROPERTY_NAME, alarmsPort);
		alarmProperties.setProperty(IWonesysAlarmConfigurator.ALARM_WAITTIME_PROPERTY_NAME, Long.valueOf(alarmWaittime).toString());

		alarmConfig.configureAlarms(alarmProperties);
		alarmConfig.enableAlarms();

		EventFilter filter = new WonesysAlarmEventFilter();
		int serviceID = eventManager.registerEventHandler(this, filter);

		// test

		TrapPduv2 pdu = null;
		try {

			pdu = triggerAlarm();

			try {
				log.info("Waiting for an alarm...");
				alarmReceived.await(alarmWaittime * 3, MILLISECONDS);
			} catch (InterruptedException e) {
				log.warn("Interrupted!");
			}

			assertEquals(0, alarmReceived.getCount());

		} finally {
			// clean up
			if (pdu != null)
				pdu.getContext().destroy();

			eventManager.unregisterHandler(serviceID);
			alarmConfig.disableAlarms();
		}
	}

	@Test
	public void testSendMultipleMessages() throws ProtocolException {

		// loadBundles();

		sendMultipleMessages(1);

		sendMultipleMessages(5);
	}

	@Test
	public void sendCommandTest() throws ProtocolException, CommandException {

		// loadBundles();

		/* Wait for the activation of all the bundles */
		// IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		ProtocolSessionContext sessionContext = createWonesysProtocolSessionContext(hostIpAddress, hostPort);
		sessionContext.addParameter("protocol.mock", "true");
		try {
			// get WonesysProtocolSession using ProtocolSessionManager
			IProtocolSession protocolSession = getProtocolSession(resourceId, sessionContext);
			if (protocolSession == null)
				throw new ProtocolException("Could not get a valid ProtocolSession");

			// create command
			WonesysCommand command = new GetInventoryCommand();
			command.initialize();

			// send command & receive response
			Object rawResponse = protocolSession.sendReceive(command.message());
			WonesysResponse response = (WonesysResponse) command.checkResponse(rawResponse);

			assertNotNull(response);

			// assure response is correct
			log.info("Received response " + response.getWonesysResponseMessage().toString());
			if (response.getStatus() == Response.Status.OK) {
				log.info("Response is OK");
			} else if (response.getStatus() == Response.Status.ERROR) {
				log.info("Response is ERROR");
				assertTrue(response.getErrors().size() > 0);
				for (String error : response.getErrors())
					log.info(error);
			}

			List<int[]> model = new ArrayList<int[]>();

			// command.parseResponse(response, model);
			String responseData = response.getInformation();
			int resultLength = 4 * 2;
			int resultsCount = responseData.length() / resultLength;
			String[] results = new String[resultsCount];
			log.info("Node has " + resultsCount + " cards");

			for (int i = 0; i < resultsCount; i++) {
				results[i] = responseData.substring(i * resultLength, (i + 1) * resultLength);

				String schasis = results[i].substring(0, 2);
				String sslot = results[i].substring(2, 4);
				String stype = results[i].substring(4, 6);
				String ssubtype = results[i].substring(6, 8);

				int chasis = Integer.parseInt(schasis, 16);
				int slot = Integer.parseInt(sslot, 16);
				int type = Integer.parseInt(stype, 16);
				int subtype = Integer.parseInt(ssubtype, 16);

				log.info("Chasis:" + chasis + " Slot:" + slot + " Type:" + type + " SubType:" + subtype);

				model.add(new int[] { chasis, slot, type, subtype });
			}

			for (int[] card : model) {
				if (card[2] == 11 &&
						(card[3] == 1 || card[3] == 3)) {
					command = new GetChannels(card[0], card[1]);
					command.initialize();

					rawResponse = protocolSession.sendReceive(command.message());
					response = (WonesysResponse) command.checkResponse(rawResponse);

					// assure response is correct
					log.info("Received response " + response.getWonesysResponseMessage().toString());
					if (response.getStatus() == Response.Status.OK) {
						log.info("Response is OK");
					} else if (response.getStatus() == Response.Status.ERROR) {
						log.info("Response is ERROR");
						assertTrue(response.getErrors().size() > 0);
						for (String error : response.getErrors())
							log.info(error);
					}

					log.info(response.getInformation());
				}
			}

		} catch (ProtocolException e) {
			Assert.fail(e.getMessage());
		} catch (CommandException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Called when the alarm is received
	 */
	@Override
	public void handleEvent(Event event) {
		assertNotNull(event);
		assertTrue(event instanceof WonesysAlarmEvent);

		WonesysAlarmEvent wevent = (WonesysAlarmEvent) event;
		WonesysAlarm a = wevent.getAlarm();
		assertNotNull(a);
		log.info("Alarm received: " + a.toString());

		alarmReceived.countDown();
	}

	private TrapPduv2 triggerAlarm() throws IOException {

		TrapPduv2 pdu = createSNMPTrap();
		sendSNMPTrap(pdu);
		return pdu;

		// try {
		// // Send setChannel command to trigger an alarm
		// IProtocolSession protocolSession = getProtocolSession(resourceId, createWonesysProtocolSessionContext(hostIpAddress, hostPort));
		//
		// WonesysCommand command = new SetChannel();
		// command.initialize();
		//
		// // send command & receive response
		// Object rawResponse = protocolSession.sendReceive(command.message());
		// WonesysResponse response = (WonesysResponse) command.checkResponse(rawResponse);
		// assertNotNull(response);
		// assertTrue(response.getStatus().equals(Response.Status.OK));
		// } catch (ProtocolException e) {
		// log.error("Could not trigger alarm. Command sending failed", e);
		// throw e;
		// } catch (CommandException e) {
		// log.error("Could not trigger alarm. Command failed", e);
		// throw e;
		// }

	}

	/**
	 * Simulates an alarm has been received.
	 * 
	 * @throws IOException
	 * @throws PduException
	 */
	private void sendSNMPTrap(TrapPduv2 pdu) throws IOException {

		try {
			log.info("Sending Pdu");
			pdu.send();
		} catch (PduException e) {
			log.error("PDUException", e);
			pdu.getContext().destroy();
			throw new IOException(e);
		}
	}

	private TrapPduv2 createSNMPTrap() throws IOException {

		SnmpContext snmpContext = new SnmpContextv2c("127.0.0.1", Integer.parseInt(alarmsPort)); // should send from a different port
		snmpContext.setCommunity("publica");
		TrapPduv2 pdu = new OneTrapPduv2(snmpContext);

		AsnObjectId varbindID;
		AsnObject varbindValue;

		// pdu.setIpAddress(new byte[] { 127, 0, 0, 1 });
		// pdu.setEnterprise("1.3.6.1.4.1.9");
		// pdu.setGenericTrap(SnmpConstants.SNMP_TRAP_ENTERPRISESPECIFIC);
		// pdu.setSpecificTrap(0);
		// pdu.setTimeTicks(20646400);

		varbindID = new AsnObjectId("1.3.6.1.2.1.1.3.0");
		varbindValue = new AsnUnsInteger(20646400);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.6.3.1.1.4.1.0");
		varbindValue = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.2");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.3");
		varbindValue = new AsnInteger(0);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.4");
		varbindValue = new AsnInteger(4);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.5");
		varbindValue = new AsnOctets("0B");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.6");
		varbindValue = new AsnOctets("586376724c617365724f6666");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.7");
		varbindValue = new AsnOctets("5472616E73636569766572204C61736572206F6666");
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.8");
		varbindValue = new AsnInteger(0);
		pdu.addOid(new varbind(varbindID, varbindValue));

		varbindID = new AsnObjectId("1.3.6.1.4.1.18223.9.8.2.9");
		varbindValue = new AsnOctets("0B0001");
		pdu.addOid(new varbind(varbindID, varbindValue));

		return pdu;
	}

	private void sendMultipleMessages(int numMessagesToSend) throws ProtocolException {

		ProtocolSessionContext protocolSessionContext1 = createWonesysProtocolSessionContext(
				hostIpAddress, hostPort);
		// use mock transport
		protocolSessionContext1.addParameter("protocol.mock", "true");

		String sessionId1 = "1";

		Object command = createGetCommand();

		WonesysProtocolSessionFactory factory = new WonesysProtocolSessionFactory();
		try {

			IProtocolSession protocolSession = factory.createProtocolSession(
					sessionId1, protocolSessionContext1);
			protocolSession.connect();
			for (int i = 0; i < numMessagesToSend; i++) {
				log.info("Sending message... " + command);
				Object response = protocolSession.sendReceive(command);
				log.info("Received response: " + (String) response);
				assertNotNull(response);
				assertFalse(response.equals(""));
			}
			protocolSession.disconnect();

		} catch (ProtocolException e) {
			log.info("Failed to send message!", e);
			throw e;
		}
	}

	private ProtocolSessionContext createWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		return protocolSessionContext;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {
		// IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		if (protocolManager == null)
			return null;

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceId);
		return protocolSessionManager.obtainSession(sessionContext, true);
	}

	private Object createGetCommand() {
		// getInventory command
		String cmd = "5910ffffffffff01ffffffff0000";
		String xor = getXOR(cmd);

		cmd += xor + "00";

		return cmd;
	}

	private String getXOR(String cmd) {

		int xor = Integer.parseInt(cmd.substring(0, 2), 16)
				^ Integer.parseInt(cmd.substring(2, 4), 16);
		for (int i = 4; i <= cmd.length() - 2; i++) {
			xor = xor ^ Integer.parseInt(cmd.substring(i, i + 2), 16);
			i++;
		}
		String hxor = Integer.toHexString(xor);
		if (hxor.length() < 2) {
			hxor = "0" + hxor;
		}
		return hxor;
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

}
