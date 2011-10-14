package org.opennaas.core.protocols.sessionmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProtocolManager implements IProtocolManager {

	private  Log								log					= LogFactory.getLog(ProtocolManager.class);

	private Map<String, IProtocolSessionManager>	sessionManagers		= null;
	private IEventManager						eventManager;
	

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
		protocolSessionManager.setEventManager(getEventManager());

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

		String[] sessionIdsToRemove = new String[protocolSessionManager.getAllProtocolSessionIds().size()];
		sessionIdsToRemove = protocolSessionManager.getAllProtocolSessionIds().toArray(sessionIdsToRemove);
		for (int i = 0; i < sessionIdsToRemove.length; i++) {
			protocolSessionManager.destroyProtocolSession(sessionIdsToRemove[i]);
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
			protocolSessionManager.setEventManager(getEventManager());
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
	public void sessionFactoryAdded(IProtocolSessionFactory serviceInstance, Map<?, ?> serviceProperties) {
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
	public void sessionFactoryRemoved(IProtocolSessionFactory serviceInstance, Map<?, ?> serviceProperties) {
		if (serviceInstance != null && serviceProperties != null) {
			log.debug("Existing protocol session factory removed :"
					+ serviceInstance.toString() + " "
					+ serviceProperties.get(ProtocolSessionContext.PROTOCOL));
			protocolFactories.remove(serviceProperties.get(ProtocolSessionContext.PROTOCOL));
		}
	}
	
	/**
	 * Blueprint callback (executed when EventManager is available)
	 * 
	 * @param eventManager
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
		
		for (IProtocolSessionManager sessionManager: sessionManagers.values()){
			if (sessionManager instanceof ProtocolSessionManager){
				((ProtocolSessionManager)sessionManager).setEventManager(eventManager);
			}
		}
	}
	
	private IEventManager getEventManager() throws ProtocolException {
		return this.eventManager;
	}

}