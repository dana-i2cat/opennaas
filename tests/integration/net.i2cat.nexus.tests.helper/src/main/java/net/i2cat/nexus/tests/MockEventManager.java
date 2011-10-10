package net.i2cat.nexus.tests;

import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Event Manager which does not publish nor notify anything.
 * @author isart
 *
 */
public class MockEventManager implements IEventManager {

	int count = 0;
	
	@Override
	public void publishEvent(Event arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public int registerEventHandler(EventHandler arg0, EventFilter arg1) {
		// TODO Auto-generated method stub
		return count++;
	}

	@Override
	public void unregisterHandler(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
