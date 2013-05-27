package org.opennaas.core.resources.alarms;

import java.util.Map;
import java.util.Properties;

import org.osgi.service.event.Event;

public class CapabilityAlarm extends Event {

	public static final String	TOPIC					= "org/opennaas/core/resources/capabilities/alarms/RECEIVED";

	/**
	 * ResourceId of the resource this alarm refers to
	 */
	public static final String	RESOURCE_ID_PROPERTY	= "resourceId";

	/**
	 * Event that caused this alarm
	 */
	public static final String	CAUSE_PROPERTY			= "cause";

	public CapabilityAlarm(Properties properties) {
		super(TOPIC, (Map) properties);
	}

}
