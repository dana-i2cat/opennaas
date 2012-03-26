package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

import java.util.Map;
import java.util.Properties;

import org.opennaas.core.resources.alarms.SessionAlarm;

public class WonesysAlarm extends SessionAlarm {

	public static final String	CHASSIS_PROPERTY	= "chassis";
	public static final String	SLOT_PROPERTY		= "slot";
	public static final String	SEVERITY_PROPERTY	= "severity";
	public static final String	ALARM_ID_PROPERTY	= "alarmId";
	public static final String	ALARM_DATA_PROPERTY	= "alarmRawData";
	public static final String	ARRIVAL_TIME		= "arrivalTime";

	public enum Severity {
		NORMAL, WARNING, MINOR, MAJOR, CRITICAL
	}

	public WonesysAlarm(Map<String, Object> properties) {
		super(properties);
	}
}
