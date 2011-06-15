package net.i2cat.nexus.protocols.sessionmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.IProtocolSessionFactory;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProtocolManager implements IProtocolManager {

	private final Log								log					= LogFactory.getLog(ProtocolManager.class);

	private Map<String, IProtocolSessionManager>	sessionManagers		= null;

	/**
	 * Stores the protocolFactories present in the platform, indexed by the protocol id
	 */
	private Map<String, IProtocolSessionFactory>	protocolFactories	= null;

	public ProtocolManager() {
		sessionManagers = new HashMap<String, IProtocolSessionManager>();
		protocolFactories = new HashMap<String, IProtocolSessionFactory>();
	}

	private synchronized String createProtocolSessionManager(String resourceID) throws ProtocolException {

		if (sessionManagers.containsKey(resourceID)) {
			throw new ProtocolException("This deviceID is already associated to an existing ProtocolSessionManager");
		}

		ProtocolSessionManager protocolSessionManager = new ProtocolSessionManager(resourceID);
		protocolSessionManager.setProtocolManager(this);

		sessionManagers.put(resourceID, protocolSessionManager);

		return resourceID;
	}

	public synchronized void destroyProtocolSessionManager(String resourceID) throws ProtocolException {
		if (resourceID == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(resourceID)) {
			throw new ProtocolException("This deviceID is already associated to an existing ProtocolSessionManager");
		}

		IProtocolSessionManager protocolSessionManager = sessionManagers.remove(resourceID);
		Iterator<String> iterator = protocolSessionManager.getAllProtocolSessionIds().iterator();

		while (iterator.hasNext()) {
			try {
				protocolSessionManager.destroyProtocolSession(iterator.next());
			} catch (ProtocolException ex) {
				ex.printStackTrace();
			}
		}
	}

	public synchronized IProtocolSessionManager getProtocolSessionManagerWithContext(String resourceId, ProtocolSessionContext context)
			throws ProtocolException {
		if (resourceId == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(resourceId)) {
			log.debug("No existing ProtocolSessionManager for resource " + resourceId + ". Creating one...");

			ProtocolSessionManager protocolSessionManager = new ProtocolSessionManager(resourceId);
			protocolSessionManager.setProtocolManager(this);
			protocolSessionManager.registerContext(context);

			sessionManagers.put(resourceId, protocolSessionManager);
		}
		return sessionManagers.get(resourceId);

	}

	public synchronized IProtocolSessionManager getProtocolSessionManager(String resourceId) throws ProtocolException {
		if (resourceId == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(resourceId)) {
			log.debug("No existing ProtocolSessionManager for resource " + resourceId + ". Creating one...");

			// FIXME in the near future, a check to ResourceManager should be done here to avoid creating PSM for resources that don't exist.
			createProtocolSessionManager(resourceId);
		}

		return sessionManagers.get(resourceId);
	}

	public List<String> getAllResourceIds() {
		Iterator<String> iterator = sessionManagers.keySet().iterator();
		List<String> result = new ArrayList<String>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}

		return result;
	}

	/*
	 * SESSION FACTORIES
	 */

	public List<String> getAllSessionFactories() {
		Iterator<String> iterator = protocolFactories.keySet().iterator();
		List<String> result = new ArrayList<String>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}

		return result;
	}

	protected synchronized IProtocolSessionFactory getSessionFactory(String protocol) throws ProtocolException {
		if (protocol == null) {
			throw new ProtocolException("Protocol is null");
		}

		if (!protocolFactories.containsKey(protocol)) {
			throw new ProtocolException("Could not find a session factory associated to the " + protocol + " protocol");
		}

		return protocolFactories.get(protocol);
	}

	/*
	 * BLUEPRINT LISTENERS
	 */

	/**
	 * Called by blueprint every time a sessionFactory is registered in the OSGi repository
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void sessionFactoryAdded(IProtocolSessionFactory serviceInstance, Map serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			log.debug("New protocol session factory added for protocols: " + serviceProperties.get(ProtocolSessionContext.PROTOCOL));
			protocolFactories.put((String) serviceProperties.get(ProtocolSessionContext.PROTOCOL), serviceInstance);
		}
	}

	/**
	 * Called by blueprint every time a sessionFactory is unregistered from the OSGi repository
	 * 
	 * @param serviceInstance
	 * @param serviceProperties
	 */
	public void sessionFactoryRemoved(IProtocolSessionFactory serviceInstance, Map serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			log.debug("Existing protocol session factory removed :"
					+ serviceInstance.toString() + " "
					+ serviceProperties.get(ProtocolSessionContext.PROTOCOL));
			protocolFactories.remove(serviceProperties.get(ProtocolSessionContext.PROTOCOL));
		}
	}

}