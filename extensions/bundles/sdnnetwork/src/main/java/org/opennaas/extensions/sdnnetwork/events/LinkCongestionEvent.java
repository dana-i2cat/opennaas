package org.opennaas.extensions.sdnnetwork.events;

import java.util.Map;

import org.osgi.service.event.Event;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class LinkCongestionEvent extends Event {

	public static final String	TOPIC			= "org/opennaas/extensions/sdnnetwork/event/link/congestion";

	/**
	 * ResourceId of the switch from which statistics has been read.
	 */
	public static final String	SWITCH_ID_KEY	= "switchId";

	/**
	 * PortId of the switch from which statistics has been read.
	 */
	public static final String	PORT_ID_KEY		= "portId";

	public LinkCongestionEvent(Map<String, Object> properties) {

		super(TOPIC, properties);

	}

}
