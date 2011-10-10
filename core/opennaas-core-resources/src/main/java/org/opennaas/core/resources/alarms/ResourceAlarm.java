package org.opennaas.core.resources.alarms;

import java.util.Map;
import java.util.Properties;

import org.osgi.service.event.Event;

/**
 * Defines a ResourceAlarm.
 * 
 * Define which properties are common to ResourceAlarms, although any other property may be added.
 * 
 * @author isart
 * 
 */
public class ResourceAlarm extends Event {

	public static final String	TOPIC					= "org/opennaas/core/resources/resource/alarms/RECEIVED";

	public static final String	RESOURCE_ID_PROPERTY	= "resourceId";
	public static final String	ALARM_CODE_PROPERTY		= "alarmCode";
	public static final String	DESCRIPTION_PROPERTY	= "alarmDescription";
	public static final String	SEVERITY_PROPERTY		= "alarmSeverity";
	public static final String	ARRIVAL_TIME_PROPERTY	= "arrivalTime";

	/* Optional property */
	public static final String	CAUSE_PROPERY			= "cause";

	private String				resourceId;
	private String				alarmCode;
	private String				arrivalTime;

	public enum Severity {
		INFO, WARNING, MINOR, MAJOR, CRITICAL
	}

	public ResourceAlarm(Properties properties) {
		super(TOPIC, (Map) properties);

		resourceId = properties.getProperty(RESOURCE_ID_PROPERTY);
		alarmCode = properties.getProperty(ALARM_CODE_PROPERTY);
		arrivalTime = properties.getProperty(ARRIVAL_TIME_PROPERTY);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alarmCode == null) ? 0 : alarmCode.hashCode());
		result = prime * result + ((arrivalTime == null) ? 0 : arrivalTime.hashCode());
		result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceAlarm other = (ResourceAlarm) obj;
		if (alarmCode == null) {
			if (other.alarmCode != null)
				return false;
		} else if (!alarmCode.equals(other.alarmCode))
			return false;
		if (arrivalTime == null) {
			if (other.arrivalTime != null)
				return false;
		} else if (!arrivalTime.equals(other.arrivalTime))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}

}
