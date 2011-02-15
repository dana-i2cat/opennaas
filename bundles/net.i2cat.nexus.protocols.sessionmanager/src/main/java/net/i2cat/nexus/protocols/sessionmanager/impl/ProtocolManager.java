package net.i2cat.nexus.protocols.sessionmanager.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolManager implements IProtocolManager {

	private Logger									logger				= LoggerFactory.getLogger(ProtocolManager.class);

	private Map<String, IProtocolSessionManager>	sessionManagers		= null;

	/**
	 * Stores the protocolFactories present in the platform, indexed by the
	 * protocol id
	 */
	private Map<String, IProtocolSessionFactory>	protocolFactories	= null;

	public ProtocolManager() {
		sessionManagers = new HashMap<String, IProtocolSessionManager>();
		protocolFactories = new HashMap<String, IProtocolSessionFactory>();
	}

	@Override
	public synchronized String createProtocolSessionManager(String deviceID) throws ProtocolException {
		if (deviceID == null) {
			deviceID = UUID.randomUUID().toString();
		}

		if (sessionManagers.containsKey(deviceID)) {
			throw new ProtocolException("This deviceID is already associated to an existing ProtocolSessionManager");
		}

		ProtocolSessionManager protocolSessionManager = new ProtocolSessionManager(deviceID);
		protocolSessionManager.setProtocolManager(this);
		sessionManagers.put(deviceID, protocolSessionManager);
		return deviceID;
	}

	@Override
	public synchronized void destroyProtocolSessionManager(String deviceID) throws ProtocolException {
		if (deviceID == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(deviceID)) {
			throw new ProtocolException("This deviceID is already associated to an existing ProtocolSessionManager");
		}

		IProtocolSessionManager protocolSessionManager = sessionManagers.remove(deviceID);
		Iterator<String> iterator = protocolSessionManager.getAllProtocolSessions().iterator();
		while (iterator.hasNext()) {
			try {
				protocolSessionManager.destroyProtocolSession(iterator.next());
			} catch (ProtocolException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public synchronized IProtocolSessionManager getProtocolSessionManager(String deviceID) throws ProtocolException {
		if (deviceID == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(deviceID)) {
			throw new ProtocolException("This deviceID is already associated to an existing ProtocolSessionManager");
		}

		return sessionManagers.get(deviceID);
	}

	/**
	 * Called by blueprint every time a sessionFactory is registered in the OSGi
	 * repository
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void sessionFactoryAdded(IProtocolSessionFactory serviceInstance, Map serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			logger.debug("New protocol session factory added for protocols: " + serviceProperties.get(ProtocolSessionContext.PROTOCOL));
			protocolFactories.put((String) serviceProperties.get(ProtocolSessionContext.PROTOCOL), serviceInstance);
		}
	}

	/**
	 * Called by blueprint every time a sessionFactory is unregistered from the
	 * OSGi repository
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void sessionFactoryRemoved(IProtocolSessionFactory serviceInstance, Map serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			logger.debug("Existing protocol session factory removed :" + serviceInstance.toString() + " " + serviceProperties
					.get(ProtocolSessionContext.PROTOCOL));
			protocolFactories.remove((String) serviceProperties.get(ProtocolSessionContext.PROTOCOL));
		}
	}

	public synchronized IProtocolSessionFactory getSessionFactory(String protocol) throws ProtocolException {
		if (protocol == null) {
			throw new ProtocolException("Protocol is null");
		}

		if (!protocolFactories.containsKey(protocol)) {
			throw new ProtocolException("Could not find a session factory associated to the " + protocol + " protocol");
		}

		return protocolFactories.get(protocol);
	}
}