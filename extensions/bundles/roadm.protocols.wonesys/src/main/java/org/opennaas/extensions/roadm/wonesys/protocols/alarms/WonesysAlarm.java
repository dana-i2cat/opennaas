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

import java.util.Map;

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
