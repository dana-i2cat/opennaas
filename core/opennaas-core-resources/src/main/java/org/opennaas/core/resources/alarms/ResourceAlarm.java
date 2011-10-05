package org.opennaas.core.resources.alarms;

import java.util.Map;
import java.util.Properties;

import org.osgi.service.event.Event;

public class ResourceAlarm extends Event {

	public static final String	TOPIC					= "org.opennaas.core/resources/resource/alarms/RECEIVED";

	public static final String	ARRIVAL_TIME_PROPERTY	= "arrivalTime";

	public static final String	RESOURCE_ID_PROPERTY	= "resourceId";
	public static final String	ALARM_CODE_PROPERTY		= "alarmCode";
	public static final String	CAUSE_PROPERY			= "cause";
	public static final String	DESCRIPTION_PROPERTY	= "alarmDescription";

	public enum Severity {
		INFO, WARNING, MINOR, MAJOR, CRITICAL
	}

	public ResourceAlarm(Properties properties) {
		super(TOPIC, (Map) properties);
	}

}
