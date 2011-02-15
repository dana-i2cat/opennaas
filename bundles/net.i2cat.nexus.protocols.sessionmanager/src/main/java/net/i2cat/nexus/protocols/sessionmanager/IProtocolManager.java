package net.i2cat.nexus.protocols.sessionmanager;

import java.util.List;

/**
 * The manager of the protocol session managers. There's one per IaaS container,
 * and handles the creation and deletion of individual protocol session
 * managers. ProtocolSessionManagers manage the sessions to a single device,
 * identified by deviceId.
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolManager {

	/**
	 * Creates a protocol session manager, associated to the deviceID. If a
	 * deviceID is provided, this will be the one associated to the protocol
	 * session manager, if it is null, a new deviceID is generated and returned
	 * 
	 * @param deviceID
	 * @return
	 * @throws ProtocolException
	 *             if the provided deviceID is already associated to an existing
	 *             protocol session manager
	 */
	public String createProtocolSessionManager(String deviceID) throws ProtocolException;

	/**
	 * Returns a pointer to the IProtocolSessionManager associated to the
	 * deviceID
	 * 
	 * @param deviceID
	 * @return
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing
	 *             protocol session manager
	 */
	public IProtocolSessionManager getProtocolSessionManager(String deviceID) throws ProtocolException;

	/**
	 * Removes an existing protocol session manager. Will cause all its protocol
	 * sessions to be disconnected.
	 * 
	 * @param deviceID
	 * @throws ProtocolException
	 *             if deviceID is null or is not associated to an existing
	 *             protocol session manager
	 */
	public void destroyProtocolSessionManager(String deviceID) throws ProtocolException;
	
	/**
	 * Return all the device ids of the protocol manager
	 * @return
	 */
	public List<String> getAllDeviceIds();
	
	/**
	 * Return all the protocol factories registered to the protocol manager
	 * @return
	 */
	public List<String> getAllProtocolFactories();

}
