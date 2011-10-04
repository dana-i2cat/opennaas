package net.i2cat.luminis.transports.wonesys.mock;

import java.util.Dictionary;
import java.util.Hashtable;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolBundleActivator;
import net.i2cat.luminis.transports.wonesys.ITransport;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;
import net.i2cat.nexus.resources.ActivatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;

public class MockTransport implements ITransport {

	Log			log			= LogFactory.getLog(RawSocketTransport.class);

	ProteusMock	proteusMock	= new ProteusMock();

	// private List<ITransportListener> listeners = new ArrayList<ITransportListener>();

	@Override
	public Object sendMsg(Object message) throws Exception {
		sendAsync(message);
		return "";
	}

	@Override
	public void sendAsync(Object message) throws Exception {
		if (!(message instanceof String))
			throw new Exception("Incorrect format in message");
		Object response = (String) proteusMock.execCommand((String) message);
		notifyListeners(response);
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() throws Exception {
		// TODO Auto-generated method stub

	}

	// public void registerListener(ITransportListener listener) {
	// listeners.add(listener);
	// }
	//
	// public void unregisterListener(ITransportListener listener) {
	// listeners.remove(listener);
	// }

	public void notifyListeners(Object message) {

		Event ev = createEvent(RawSocketTransport.MSG_RCVD_EVENT_TOPIC, (String) message);

		try {
			WonesysProtocolBundleActivator.getEventManagerService().publishEvent(ev);
		} catch (ActivatorException e) {
			log.error("Could not notify transport listeners.", e);
			log.error("Received event will be lost. Event: " + ev.toString());
		}

		// // manually call handleEvent in all listeners (skip eventManager)
		// for (int i = listeners.size() - 1; i >= 0; i--) {
		// listeners.get(i).handleEvent(ev);
		// }

	}

	protected Event createEvent(String eventTopic, String message) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, getTransportID());
		if (message != null)
			properties.put(RawSocketTransport.MESSAGE_PROPERTY_NAME, message);

		return new Event(eventTopic, properties);
	}

	@Override
	public String getTransportID() {
		return "mockTransportID";
	}

}
