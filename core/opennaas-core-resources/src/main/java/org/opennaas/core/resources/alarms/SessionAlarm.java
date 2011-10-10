package org.opennaas.core.resources.alarms;

import java.util.Map;
import java.util.Properties;

import org.osgi.service.event.Event;

public class SessionAlarm extends Event {

	public static final String	TOPIC				= "org.opennaas.core/resources/sessions/alarms/RECEIVED";

	/**
	 * ResourceId of the resource this alarm refers to
	 */
	public static final String	SESSION_ID_PROPERTY	= "sessionId";

	/**
	 * Event that caused this alarm (optional)
	 */
	public static final String	CAUSE_PROPERY		= "cause";

	public SessionAlarm(Properties properties) {
		super(TOPIC, (Map) properties);
	}

}
