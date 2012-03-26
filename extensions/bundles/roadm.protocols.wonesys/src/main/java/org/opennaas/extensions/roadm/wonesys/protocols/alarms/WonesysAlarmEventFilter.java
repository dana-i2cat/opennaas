package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

import java.util.Properties;

import org.opennaas.core.events.EventFilter;

/**
 * Filter matching wonesys alarms
 *
 * @author isart
 *
 */
public class WonesysAlarmEventFilter extends EventFilter {

	public static final String	WONESYS_ALARM_EVENT_FILTER_TOPIC	= WonesysAlarmEvent.WONESYS_ALARM_EVENT_TOPIC;

	/**
	 * Constructor of filters matching any wonesys alarm
	 */
	public WonesysAlarmEventFilter() {
		super(WONESYS_ALARM_EVENT_FILTER_TOPIC);
	}

	/**
	 * Constructor of filters matching wonesys alarms with specified properties
	 *
	 * @param propertiesFilter
	 */
	public WonesysAlarmEventFilter(String propertiesFilter) {
		super(WONESYS_ALARM_EVENT_FILTER_TOPIC, propertiesFilter);
	}

	/**
	 * Constructor of filters matching wonesys alarms with specified properties
	 *
	 * @param propertiesFilter
	 */
	public WonesysAlarmEventFilter(Properties properties) {
		super(new String[] { WONESYS_ALARM_EVENT_FILTER_TOPIC }, properties);
	}

}
