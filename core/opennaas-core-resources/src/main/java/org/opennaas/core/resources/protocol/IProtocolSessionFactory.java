package org.opennaas.core.resources.protocol;

/**
 * Protocol sessions must implement this interface and publish a service that
 * implements this interface to the OSGi registry, with a property called
 * "Protocol" whose value is the name of the protocol that the protocol session
 * implements.
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolSessionFactory {

	/**
	 * Create a protocol session and configure it using the information in the
	 * ProtocolSessionContext.
	 * 
	 * @param sessionID
	 *            The ID associated to the session, cannot be null
	 * @param context
	 *            the context associated to the session, cannot be null
	 * @return the new protocol session (not connected)
	 * @throws ProtocolException
	 *             if something goes wrong or the information provided to the
	 *             operation is not correct
	 */
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext context) throws ProtocolException;
}
