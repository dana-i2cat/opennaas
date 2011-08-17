package org.opennaas.core.resources.protocol;

/**
 * Represents a single protocol session to a device. Each protocol session runs in its own separated thread (should we try to see how nio works to
 * improve this? It is assumed that only one thread at a time will be able to access a protocol session (this must be guaranteed by the protocol
 * session manager)
 * 
 * @author eduardgrasa
 */
public interface IProtocolSession {

	public enum Status {
		CONNECTED, DISCONNECTED_BY_USER, CONNECTION_LOST
	};

	/**
	 * Returns the ID of this protocol session, unique within the context of a protocol session manager (i.e. a device)
	 * 
	 * @return
	 */
	public String getSessionId();

	/**
	 * This gives the implementation to chance to know its session id. Called when the session is first created. getSessionId() is required return the
	 * same string, but not String instance.
	 * 
	 * @return the session id
	 */
	public void setSessionId(String sessionId);

	/**
	 * Returns the parameters that define the state of this session
	 * 
	 * @return
	 */
	public ProtocolSessionContext getSessionContext();

	/**
	 * ProtocolSession implementations will receive the context on creation via this setter too. Alternatively, its *SessionFactory can pass it via
	 * the constructor too.
	 * 
	 * @param context
	 */
	public void setSessionContext(ProtocolSessionContext context);

	/**
	 * The status of this session
	 * 
	 * @return
	 */
	public Status getStatus();

	/**
	 * Connects to the managed device and carries out all the tasks to start the session, including possible authentication with the device
	 */
	public void connect() throws ProtocolException;

	/**
	 * Terminates the session and disconnects from the device
	 */
	public void disconnect() throws ProtocolException;

	/**
	 * Send a message to the device, and wait for the response
	 * 
	 * @param message
	 *            The message to be sent to the device
	 * @return The response message from the device
	 */
	public Object sendReceive(Object message) throws ProtocolException;

	/**
	 * Send a message to the device, but don't wait for the response.
	 * 
	 * @param message
	 */
	public void asyncSend(Object message) throws ProtocolException;

	/**
	 * Register a class that will receive the messages from the device that match the filter
	 * 
	 * @param listener
	 * @param filter
	 */
	public void registerProtocolSessionListener(IProtocolSessionListener listener, IProtocolMessageFilter filter, String idListener);

	/**
	 * Unregister a protocol session listener, it will stop receiving messages from the device
	 * 
	 * @param listener
	 */
	public void unregisterProtocolSessionListener(IProtocolSessionListener listener, String idListener);
}
