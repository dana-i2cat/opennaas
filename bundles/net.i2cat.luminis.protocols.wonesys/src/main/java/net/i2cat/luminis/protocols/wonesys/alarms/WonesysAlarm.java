package net.i2cat.luminis.protocols.wonesys.alarms;

import java.util.Map;
import java.util.Properties;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;

import org.osgi.service.event.Event;

public class WonesysAlarm extends Event {

	public static final String	TOPIC				= WonesysProtocolSession.ALARM_RCVD_EVENT_TOPIC;

	public static final String	CHASSIS_PROPERTY	= "chassis";
	public static final String	SLOT_PROPERTY		= "slot";
	public static final String	SEVERITY_PROPERTY	= "severity";
	public static final String	ALARM_ID_PROPERTY	= "alarmId";
	public static final String	ALARM_DATA_PROPERTY	= "alarmRawData";
	public static final String	ARRIVAL_TIME		= "arrivalTime";
	public static final String	SESSION_ID_PROPERTY	= WonesysProtocolSession.SESSION_ID_PROPERTY;

	public enum Severity {
		NORMAL, WARNING, MINOR, MAJOR, CRITICAL
	}

	public WonesysAlarm(Properties properties) {
		super(TOPIC, (Map) properties);
	}
}
