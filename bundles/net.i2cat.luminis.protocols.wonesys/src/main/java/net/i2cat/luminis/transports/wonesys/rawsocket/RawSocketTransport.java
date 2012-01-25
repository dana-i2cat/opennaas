package net.i2cat.luminis.transports.wonesys.rawsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolBundleActivator;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.resources.ActivatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Creates a Socket and uses it to send and receive messages. Messages to send are expected to be HexStrings. Received messages are expected to be
 * byte chunks , and will be transformed to HexString.
 * 
 * Notifies listeners when a message is received, connection is closed or an error occurs during reading from socket.
 * 
 * Notice it uses osgi events without using EventManager
 * 
 * @author isart
 * 
 */
public class RawSocketTransport {

	/* Event topic */
	public static final String		ALL_EVENTS_TOPIC						= "net/i2cat/luminis/transports/wonesys/rawsocket/*";
	public static final String		MSG_RCVD_EVENT_TOPIC					= "net/i2cat/luminis/transports/wonesys/rawsocket/MESSAGE_RCV";
	public static final String		MSG_SENT_EVENT_TOPIC					= "net/i2cat/luminis/transports/wonesys/rawsocket/MESSAGE_SENT";
	public static final String		CONNECTION_CLOSED_EVENT_TOPIC			= "net/i2cat/luminis/transports/wonesys/rawsocket/CONNECTION_CLOSED";
	public static final String		CONNECTION_CLOSED_BY_USER_EVENT_TOPIC	= "net/i2cat/luminis/transports/wonesys/rawsocket/CONNECTION_CLOSED_BY_USER";
	public static final String		ERROR_EVENT_TOPIC						= "net/i2cat/luminis/transports/wonesys/rawsocket/ERROR";

	public static final String		MESSAGE_PROPERTY_NAME					= "message";
	public static final String		ERROR_PROPERTY_NAME						= "error";
	public static final String		TRANSPORT_ID_PROPERTY_NAME				= "transportid";
	public static final String		ARRIVAL_TIME_PROPERTY_NAME					= "arrivalTime";
	

	public static final int			DEFAULT_PORT							= 27773;
	public static final int			DEFAULT_TIMEOUT							= 1000;

	private Socket					sock;
	private List<EventHandler>		listeners								= new ArrayList<EventHandler>();

	Log								log										= LogFactory.getLog(RawSocketTransport.class);

	private final String			transportID;
	private static AtomicInteger	inc										= new AtomicInteger(0);

	public RawSocketTransport() {
		sock = new Socket();
		transportID = generateTransportID();
	}

	private String generateTransportID() {
		return String.valueOf(inc.getAndIncrement());
	}

	public void connectSocket(String ip) throws IOException {
		connectSocket(ip, DEFAULT_PORT);
	}

	public void connectSocket(String ip, int port) throws IOException {

		int timeout = DEFAULT_TIMEOUT;

		// Bind to a local ephemeral port
		sock.bind(null);
		sock.connect(new InetSocketAddress(ip, port), timeout);

		// attach reader to receive async msgs
		SocketReader reader = new SocketReader();
		reader.start();

	}

	public void disconnectSocket() throws IOException {
		if (!sock.isClosed()) {
			sock.close();
			notifyListeners(createEvent(CONNECTION_CLOSED_BY_USER_EVENT_TOPIC, null));
		}
	}

	public void sendAsyncToSocket(String message) throws IOException {

		byte[] bts = new BigInteger(message, 16).toByteArray();

		OutputStream out = sock.getOutputStream();

		out.write(bts);
		out.flush();
	}

	// public int registerListener(EventHandler listener) throws WonesysTransportException {
	//
	// EventFilter allMyEventsFilter = createAllMyEventsFilter();
	// try {
	// return Activator.getEventManagerService().registerEventHandler(listener, allMyEventsFilter);
	// } catch (ActivatorException e) {
	// throw new WonesysTransportException("Unable to register listener", e);
	// }
	//
	// // if (listener != null && !listeners.contains(listener))
	// // listeners.add(listener);
	// }
	//
	// public void unregisterListener(int registrationNum) throws WonesysTransportException {
	// try {
	// Activator.getEventManagerService().unregisterHandler(registrationNum);
	// } catch (ActivatorException e) {
	// throw new WonesysTransportException("Unable to unregister listener", e);
	// }
	// // listeners.remove(listener);
	// }

	public void notifyListeners(Event ev) {

		try {
			WonesysProtocolBundleActivator.getEventManagerService().publishEvent(ev);
		} catch (ActivatorException e) {
			log.error("Could not notify transport listeners.", e);
			log.error("Received event will be lost. Event: " + ev.toString());
		}

		// for (int i = listeners.size() - 1; i >= 0; i--) {
		// listeners.get(i).handleEvent(ev);
		// }
	}

	protected Event createEvent(String eventTopic, String message) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, getTransportID());
		properties.put(RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME, new Date().getTime());
		if (message != null)
			properties.put(MESSAGE_PROPERTY_NAME, message);

		return new Event(eventTopic, properties);
	}

	protected Event createErrorEvent(String eventTopic, Exception err) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, getTransportID());
		properties.put(RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME, new Date().getTime());
		if (err != null)
			properties.put(ERROR_PROPERTY_NAME, err);

		return new Event(eventTopic, properties);
	}

	protected EventFilter createAllMyEventsFilter() {

		String topic = RawSocketTransport.ALL_EVENTS_TOPIC;
		Properties properties = new Properties();
		properties.setProperty(RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME, getTransportID());
		return new EventFilter(new String[] { topic }, properties);
	}

	public String getTransportID() {
		return transportID.toString();
	}

	/**
	 * Thread that listens the socket for messages arrival, until socket is closed.
	 * 
	 * When a message arrives, listeners are notified.
	 * 
	 * @author isart
	 * 
	 */
	class SocketReader extends Thread {

		public void run() {

			try {
				InputStream in = sock.getInputStream();

				while (!sock.isClosed()) {
					String message = readMessage(in);
					if (!message.equals("")) {
						log.debug("Message received: " + message);
						notifyListeners(createEvent(MSG_RCVD_EVENT_TOPIC, message));
					}
				}
			} catch (IOException e1) {
				notifyListeners(createErrorEvent(ERROR_EVENT_TOPIC, e1));
			}
		}

		private String readMessage(InputStream in) throws IOException {

			String outS = "";
			byte[] buffer = new byte[256];

			// while ((i = in.read(buffer)) != 0){

			int i = in.read(buffer);

			String salida = ""; //$NON-NLS-1$

			for (int j = 0; j < i; j++) {
				byte o = buffer[j];
				if ((o < 0x10) && (o >= 0))
					salida += "0"; //$NON-NLS-1$
				String bite = Integer.toHexString(o);
				if (bite.length() > 2)
					salida += bite.substring(bite.length() - 2);
				else
					salida += bite;
			}

			outS += salida;
			// }
			return outS;
		}
	}
}
