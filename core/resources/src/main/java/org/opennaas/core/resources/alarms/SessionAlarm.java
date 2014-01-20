package org.opennaas.core.resources.alarms;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.Map;

import org.osgi.service.event.Event;

public class SessionAlarm extends Event {

	public static final String	TOPIC				= "org/opennaas/core/resources/sessions/alarms/RECEIVED";

	/**
	 * ResourceId of the resource this alarm refers to
	 */
	public static final String	SESSION_ID_PROPERTY	= "sessionId";

	/**
	 * Event that caused this alarm (optional)
	 */
	public static final String	CAUSE_PROPERY		= "cause";

	public SessionAlarm(Map<String, Object> properties) {
		super(TOPIC, properties);
	}

}
