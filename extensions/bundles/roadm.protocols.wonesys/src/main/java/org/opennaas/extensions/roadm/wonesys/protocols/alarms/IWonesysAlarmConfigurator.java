package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

import java.io.IOException;
import java.util.Properties;

public interface IWonesysAlarmConfigurator {

	public static final String	ALARM_PORT_PROPERTY_NAME		= "protocol.alarmport";
	public static final String	ALARM_WAITTIME_PROPERTY_NAME	= "protocol.alarmwaittime";

	/**
	 * Allows alarms configuration through given properties.
	 */
	public void configureAlarms(Properties properties);

	/**
	 * Activates the receiving of alarms. System may receive alarms after this call.
	 *
	 * @throws IOException
	 */
	public void enableAlarms() throws IOException;

	/**
	 * Stops the receiving of alarms. No alarms will be received after this call has returned.
	 */
	public void disableAlarms();

}
