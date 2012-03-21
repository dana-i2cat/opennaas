package net.i2cat.luminis.protocols.wonesys.tests;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;

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

import net.i2cat.luminis.protocols.wonesys.alarms.IWonesysAlarmConfigurator;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarmEvent;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarmEventFilter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.junit.Assert.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class ReceiveAlarmsTest implements EventHandler
{
	private static Log log = LogFactory.getLog(ReceiveAlarmsTest.class);

	@Inject
	private BundleContext	bundleContext;

	// private String resourceId = "Proteus-Pedrosa";
	// private String hostIpAddress = "10.10.80.11";
	// private String hostPort = "27773";

	private String alarmsPort		= "32162";						// SNMP traps port (162). if different,
																										// 162 needs to be redirected to this port
																										// in order to receive traps.

	private long alarmWaittime	= 5 * 1000;						// 5sec

	private final CountDownLatch alarmReceived = new CountDownLatch(1);

	// Required services
	@Inject
	private IProtocolManager protocolManager;

	@Inject
	private IEventManager	eventManager;

	@Inject
	private IWonesysAlarmConfigurator	alarmConfig;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
					   noConsole(),
					   keepRuntimeFolder());
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

}
