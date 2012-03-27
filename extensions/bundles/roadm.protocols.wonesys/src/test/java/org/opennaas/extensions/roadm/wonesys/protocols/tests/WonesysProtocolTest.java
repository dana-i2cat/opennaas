package org.opennaas.extensions.roadm.wonesys.protocols.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.opennaas.extensions.roadm.wonesys.protocols.alarms.IWonesysAlarmConfigurator;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarmConfigurator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class WonesysProtocolTest {

	Log				log				= LogFactory.getLog(WonesysProtocolTest.class);

	String			hostIpAddress	= "10.10.80.11";
	String			hostPort		= "27773";

	/**
	 * SNMP traps port (162). if different, 162 needs to be redirected to this port in order to receive traps.
	 */
	private String	alarmsPort		= "11162";

	private long	alarmWaittime	= 5 * 1000;

	@Test
	public void testConfigureAlarms() throws IOException {

		Properties alarmProperties = new Properties();
		alarmProperties.setProperty(IWonesysAlarmConfigurator.ALARM_PORT_PROPERTY_NAME, alarmsPort);
		alarmProperties.setProperty(IWonesysAlarmConfigurator.ALARM_WAITTIME_PROPERTY_NAME, Long.valueOf(alarmWaittime).toString());

		WonesysAlarmConfigurator alarmConfigurator = new WonesysAlarmConfigurator();
		alarmConfigurator.configureAlarms(alarmProperties);
		assertTrue(alarmConfigurator.getStatus().equals(WonesysAlarmConfigurator.CONFIGURED_ALARM_STATUS));

		alarmConfigurator.enableAlarms();
		assertTrue(alarmConfigurator.getStatus().equals(WonesysAlarmConfigurator.ACTIVE_ALARM_STATUS));

		try {
			log.info("Sleeping...");
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			log.info("Interrupted! ", e);
		}

		alarmConfigurator.disableAlarms();
		assertTrue(alarmConfigurator.getStatus().equals(WonesysAlarmConfigurator.CONFIGURED_ALARM_STATUS));
	}

	// // should know setCommand parameters before using it
	// // @Test // MOVED to WonesysAlarmsTest
	// public void testSendMessageAndAlarm() throws IOException {
	// // MOVED to WonesysAlarmsTest
	//
	// ProtocolSessionContext protocolSessionContext = newWonesysProtocolSessionContext(
	// hostIpAddress, hostPort, alarmPort, Long.valueOf(15 * 1000)
	// .toString());
	// String sessionId1 = "1";
	//
	// WonesysAlarmConfigurator alarmReceiver = new WonesysAlarmConfigurator(
	// protocolSessionContext);
	// alarmReceiver.registerListener(this, this.getClass().getName());
	//
	// Object command = createSetCommand();
	//
	// WonesysProtocolSessionFactory factory = new WonesysProtocolSessionFactory();
	// try {
	//
	// IProtocolSession protocolSession = factory.createProtocolSession(
	// sessionId1, protocolSessionContext);
	// protocolSession.connect();
	// log.info("Sending message...");
	// Object response = protocolSession.sendReceive(command);
	// log.info("Received response: " + (String) response);
	// protocolSession.disconnect();
	//
	// } catch (ProtocolException e) {
	// log.info("Failed to send message!");
	// log.info(e.getMessage());
	// }
	// }

	private ProtocolSessionContext newWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		return protocolSessionContext;
	}

	private Object createGetCommand() {
		// getInventory command
		String cmd = "5910ffffffffff01ffffffff0000";
		String xor = getXOR(cmd);

		cmd += xor + "00";

		return cmd;
	}

	private Object createSetCommand() {

		// FIXME: Use hardcoded variables
		String chasis = "";
		String slot = "";
		String schannel = "";
		String sport = "";

		// SetChannelCommand
		String cmd = "5910" + chasis + slot + "ffff0b02ffffffff0300" + schannel
				+ sport;
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

}
