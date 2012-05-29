package org.opennaas.core.resources.mock;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MockEventManager implements IEventManager {

	int count = 0;

	@Override
	public int registerEventHandler(EventHandler handler, EventFilter filter) {
		return ++count;
	}

	@Override
	public void unregisterHandler(int handlerServiceID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishEvent(Event event) {
		// TODO Auto-generated method stub
	}

}
