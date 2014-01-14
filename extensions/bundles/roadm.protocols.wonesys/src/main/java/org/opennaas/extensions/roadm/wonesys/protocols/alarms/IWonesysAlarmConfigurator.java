package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Protocol
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
