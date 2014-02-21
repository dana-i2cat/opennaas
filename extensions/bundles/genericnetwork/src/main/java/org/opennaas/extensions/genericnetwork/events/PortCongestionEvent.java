package org.opennaas.extensions.genericnetwork.events;

/*
 * #%L
 * OpenNaaS :: OF Network
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

import org.osgi.service.event.Event;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class PortCongestionEvent extends Event {

	public static final String	TOPIC		= "org/opennaas/extensions/ofnetwork/event/link/congestion";

	/**
	 * PortId from which statistics has been read.
	 */
	public static final String	PORT_ID_KEY	= "portId";

	public PortCongestionEvent(Map<String, Object> properties) {

		super(TOPIC, properties);

	}

}
