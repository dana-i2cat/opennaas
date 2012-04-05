package org.opennaas.core.resources.protocol;

import java.util.List;

/**
 * The manager of the protocol session managers. There's one per IaaS container, and handles the creation and deletion of individual protocol session
 * managers. ProtocolSessionManagers manage the sessions to a single device, identified by deviceId.
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolManager {

	/**
	 * Returns a pointer to the IProtocolSessionManager associated to the deviceID
	 * 
	 * @param resourceId
	 * @return
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing protocol session manager
	 */
	public IProtocolSessionManager getProtocolSessionManager(String resourceId) throws ProtocolException;

	/**
	 * Returns a pointer to the IProtocolSessionManager associated to the deviceID and a context
	 * 
	 * @param resourceId
	 * @param context
	 * @return
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing protocol session manager
	 */
	public IProtocolSessionManager getProtocolSessionManagerWithContext(String resourceId, ProtocolSessionContext context) throws ProtocolException;

	/**
	 * Removes an existing protocol session manager. Will cause all its protocol sessions to be disconnected.
	 * 
	 * @param resourceId
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing protocol session manager
	 */
	public void destroyProtocolSessionManager(String resourceId) throws ProtocolException;

	/**
	 * Return all the device ids of the protocol manager
	 * 
	 * @return
	 */
	public List<String> getAllResourceIds();

	/**
	 * Return all protocols registered to the protocol manager
	 * 
	 * @return
	 */
	public List<String> getAllSupportedProtocols();

}
