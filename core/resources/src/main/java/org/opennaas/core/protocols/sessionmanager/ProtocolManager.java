package org.opennaas.core.protocols.sessionmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class ProtocolManager implements IProtocolManager {

	private Log										log					= LogFactory.getLog(ProtocolManager.class);

	private Map<String, IProtocolSessionManager>	sessionManagers		= null;
	private IEventManager							eventManager;

	/**
	 * Stores the protocolFactories present in the platform, indexed by the protocol id
	 */
	private Map<String, IProtocolSessionFactory>	protocolFactories	= null;

	public ProtocolManager() {
		sessionManagers = new HashMap<String, IProtocolSessionManager>();
		protocolFactories = new HashMap<String, IProtocolSessionFactory>();
	}

	private synchronized String createProtocolSessionManager(String resourceID) throws ProtocolException {
		log.debug("Creating new ProtocolSessionManager for resource " + resourceID);

		// FIXME in the near future, a check should be done here to avoid creating PSM for resources that don't exist in ResourceManager.
		// It will imply changing a lot of tests, be prepared :P

		if (sessionManagers.containsKey(resourceID)) {
			throw new ProtocolException("This deviceID is already associated to an existing ProtocolSessionManager");
		}

		ProtocolSessionManager protocolSessionManager = new ProtocolSessionManager(resourceID);
		protocolSessionManager.setProtocolManager(this);
		protocolSessionManager.setEventManager(getEventManager());

		protocolSessionManager.registerAsOSGiService();

		sessionManagers.put(resourceID, protocolSessionManager);

		return resourceID;
	}

	@Override
	public synchronized void destroyProtocolSessionManager(String resourceID) throws ProtocolException {
		log.debug("Destroying ProtocolSessionManager for resource " + resourceID);

		if (resourceID == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(resourceID)) {
			throw new ProtocolException("This deviceID is not associated to any existing ProtocolSessionManager");
		}

		IProtocolSessionManager protocolSessionManager = sessionManagers.get(resourceID);

		if (protocolSessionManager instanceof ProtocolSessionManager) {
			((ProtocolSessionManager) protocolSessionManager).unregisterAsOSGiService();
		}

		for (ProtocolSessionContext toUnregister : protocolSessionManager.getRegisteredContexts()) {
			protocolSessionManager.unregisterContext(toUnregister);
		}

		sessionManagers.remove(resourceID);
	}

	@Override
	public synchronized IProtocolSessionManager getProtocolSessionManagerWithContext(String resourceId, ProtocolSessionContext context)
			throws ProtocolException {

		if (resourceId == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(resourceId)) {
			log.debug("No existing ProtocolSessionManager for resource " + resourceId);

			createProtocolSessionManager(resourceId);
		}

		sessionManagers.get(resourceId).registerContext(context);

		return sessionManagers.get(resourceId);
	}

	@Override
	public synchronized IProtocolSessionManager getProtocolSessionManager(String resourceId) throws ProtocolException {
		if (resourceId == null) {
			throw new ProtocolException("deviceID is null");
		}

		if (!sessionManagers.containsKey(resourceId)) {
			log.debug("No existing ProtocolSessionManager for resource " + resourceId);

			createProtocolSessionManager(resourceId);
		}

		return sessionManagers.get(resourceId);
	}

	@Override
	public List<String> getAllResourceIds() {
		Iterator<String> iterator = sessionManagers.keySet().iterator();
		List<String> result = new ArrayList<String>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}

		return result;
	}

	@Override
	public List<String> getAllSupportedProtocols() {
		return getAllSessionFactories();
	}

	public boolean isSupportedProtocol(String protocol) {
		if (protocol == null)
			return false;
		return protocolFactories.containsKey(protocol);
	}

	/*
	 * SESSION FACTORIES
	 */

	public List<String> getAllSessionFactories() {
		List<String> result = new ArrayList<String>();
		result.addAll(protocolFactories.keySet());
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

		for (IProtocolSessionManager sessionManager : sessionManagers.values()) {
			if (sessionManager instanceof ProtocolSessionManager) {
				((ProtocolSessionManager) sessionManager).setEventManager(eventManager);
			}
		}
	}

	private IEventManager getEventManager() throws ProtocolException {
		return this.eventManager;
	}
}