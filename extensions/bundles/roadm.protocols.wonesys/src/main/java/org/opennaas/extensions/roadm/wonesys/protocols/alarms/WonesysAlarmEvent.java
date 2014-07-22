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

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;

import com.wonesys.emsModule.alarms.Alarm;

public class WonesysAlarmEvent extends Event {

	public static final String	TYPEID_ALARM_PROPERTY			= "typeID";
	public static final String	RECEPTIONDATE_ALARM_PROPERTY	= "receptionDate";
	public static final String	IP_ALARM_PROPERTY				= "ip";
	public static final String	SLOT_ALARM_PROPERTY				= "slot";
	public static final String	PORT_ALARM_PROPERTY				= "port";
	public static final String	DESCRIPTION_ALARM_PROPERTY		= "description";
	public static final String	SEVERITY_ALARM_PROPERTY			= "severity";

	public static final String	WONESYS_ALARM_EVENT_TOPIC		= "com/wonesys/emsModule/alarms/RECEIVED";

	private WonesysAlarm		alarm;

	public WonesysAlarmEvent(Alarm a) {

		super(WONESYS_ALARM_EVENT_TOPIC, getAlarmProperties(a));
		this.alarm = WonesysAlarmFactory.createAlarm(a);
	}

	private static Map<String, Object> getAlarmProperties(Alarm a) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(TYPEID_ALARM_PROPERTY, a.getTypeID());
		properties.put(RECEPTIONDATE_ALARM_PROPERTY, a.getDataRecepcio());
		properties.put(IP_ALARM_PROPERTY, a.getIp());
		properties.put(SLOT_ALARM_PROPERTY, a.getSlot());
		properties.put(PORT_ALARM_PROPERTY, a.getPort());
		properties.put(DESCRIPTION_ALARM_PROPERTY, a.getRawIndo());
		properties.put(SEVERITY_ALARM_PROPERTY, a.getSeverity());

		return properties;
	}

	public WonesysAlarm getAlarm() {
		return this.alarm;
	}
}
