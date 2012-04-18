package org.opennaas.core.events;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public interface IEventManager {

	public int registerEventHandler(EventHandler handler, EventFilter filter);

	public void unregisterHandler(int handlerServiceID);

	public void publishEvent(Event event);

}
