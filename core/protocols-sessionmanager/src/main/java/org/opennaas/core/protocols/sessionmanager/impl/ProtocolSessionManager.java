package org.opennaas.core.protocols.sessionmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.alarms.SessionAlarm;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSession.Status;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ProtocolSessionManager implements IProtocolSessionManager, IProtocolSessionListener, IProtocolMessageFilter, EventHandler {

	private String								resourceID									= null;
	private Map<String, ProtocolPooled>			liveSessions								= null;
	private Map<String, ProtocolSessionContext>	liveSessionContexts							= null;
	private Map<String, ProtocolSessionContext>	registeredContexts							= null;
	private List<String>						lockedProtocolSessions						= null;
	private ProtocolManager						protocolManager								= null;
	private IEventManager						eventManager;

	Log											log											= LogFactory.getLog(ProtocolSessionManager.class);

	/**
	 * key: sessionId value: registration number
	 */
	private Map<String, Integer>				sessionEventsListenerRegistrationNumbers	= new HashMap<String, Integer>();

	// TODO get this from the configuration
	private static long							expirationTime								= 3000 * 1000;										// milis

	class ProtocolPooled {
		private long				lastUsed;
		private IProtocolSession	session;

		private Lock				lock;

		public ProtocolPooled(IProtocolSession protocolSession, long time) {
			this.lastUsed = time;
			this.session = protocolSession;

			lock = new java.util.concurrent.locks.ReentrantLock();
		}

		public void setProtocolSession(IProtocolSession session) {
			this.session = session;
		}

		public IProtocolSession getProtocolSession() {
			return session;
		}

		public void setLastUsed(long time) {
			this.lastUsed = time;
		}

		public long getLastUsed() {
			return lastUsed;
		}

		public Lock getLock() {
			return lock;
		}

	}

	public ProtocolSessionManager(String deviceID) {
		this.resourceID = deviceID;
		this.liveSessions = new HashMap<String, ProtocolPooled>();
		this.liveSessionContexts = new HashMap<String, ProtocolSessionContext>();
		this.registeredContexts = new HashMap<String, ProtocolSessionContext>();
		this.lockedProtocolSessions = new ArrayList<String>();
	}

	public void setProtocolManager(ProtocolManager protocolManager) {
		this.protocolManager = protocolManager;
	}

	@Override
	public String getResourceID() {
		return resourceID;
	}

	@Override
	public Set<String> getAllProtocolSessionIds() {
		return liveSessions.keySet();
	}

	@Override
	public List<ProtocolSessionContext> getRegisteredContexts() {
		List<ProtocolSessionContext> contexts = new ArrayList<ProtocolSessionContext>();
		for (ProtocolSessionContext context : getRegisteredProtocolSessionContexts().values()) {
			contexts.add(context);
		}
		return contexts;
	}

	public Map<String, ProtocolSessionContext> getRegisteredProtocolSessionContexts() {
		return registeredContexts;
	}

	/**
	 * Get the miliseconds since this session was last released.
	 * 
	 * @param sessionIds
	 * @return System.currentTimeMillis() snapshot taken when released.
	 */
	public long getSessionlastUsed(String sessionIds) {
		// This function is a bit bad placed, but saves us from having a lastUsed field in the IProtocolSession implementations, which are potentially
		// done by third parties.
		return liveSessions.get(sessionIds).getLastUsed();
	}

	private synchronized IProtocolSession createProtocolSession(ProtocolSessionContext protocolSessionContext) throws ProtocolException {

		long now = System.currentTimeMillis();

		String protocol = (String) protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		IProtocolSessionFactory protocolFactory = protocolManager.getSessionFactory(protocol);
		String sessionID = UUID.randomUUID().toString();
		IProtocolSession protocolSession = protocolFactory.createProtocolSession(sessionID, protocolSessionContext);

		// Don't trust Factory implementations to do this.
		protocolSession.setSessionId(sessionID);
		protocolSession.setSessionContext(protocolSessionContext);

		/* Activate listener */
		protocolSession.registerProtocolSessionListener(this, this, sessionID);
		try {
			registerAsSessionAlarmListener(this, sessionID);
		} catch (ProtocolException e) {
			throw new ProtocolException("Failed to register as session alarms listener for session " + sessionID, e);
		}

		liveSessions.put(sessionID, new ProtocolPooled(protocolSession, now));
		liveSessionContexts.put(sessionID, protocolSessionContext);
		protocolSession.connect();

		return protocolSession;
	}

	@Override
	public synchronized void destroyProtocolSession(String sessionID) throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!liveSessions.containsKey(sessionID)) {
			throw new ProtocolException("There is no existing session with this ID: " + sessionID);
		}

		lockProtocolSession(sessionID);

		IProtocolSession protocolSession = liveSessions.get(sessionID).getProtocolSession();

		/* disconnect the session */
		if (protocolSession.getStatus().equals(Status.CONNECTED)) {
			protocolSession.disconnect();
		}

		liveSessions.remove(sessionID);
		liveSessionContexts.remove(sessionID);
		protocolSession.unregisterProtocolSessionListener(this, sessionID);
		try {
			unregisterAsSessionAlarmListener(this, sessionID);
		} catch (ProtocolException e) {
			// ignored (even if unregistration fails, no events can be received as session is destroyed)
			log.warn("Failed to unregister as session alarms listener for session " + sessionID + " No events can be received as session is destroyed.");
		}

		lockedProtocolSessions.remove(sessionID);
	}

	@Override
	public void registerContext(ProtocolSessionContext context) throws ProtocolException {

		// ignore returned object, we do this only to check a sessionFactory for this protocol is available
		protocolManager.getSessionFactory((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));

		unregisterContext((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));

		registeredContexts.put((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL), context);
	}

	@Override
	public void unregisterContext(String protocol) throws ProtocolException {
		ProtocolSessionContext removedContext = registeredContexts.remove(protocol);
		if (removedContext != null) {
			for (IProtocolSession session : getLiveSessionsByContext(removedContext)) {
				destroyProtocolSession(session.getSessionId());
			}
		}
	}

	private List<IProtocolSession> getLiveSessionsByContext(ProtocolSessionContext context) {
		List<IProtocolSession> sessionsWithGivenContext = new ArrayList<IProtocolSession>();
		for (ProtocolPooled pooledSession : liveSessions.values()) {
			if (pooledSession.getProtocolSession().getSessionContext().equals(context)) {
				sessionsWithGivenContext.add(pooledSession.getProtocolSession());
			}
		}
		return sessionsWithGivenContext;
	}

	@Override
	public synchronized boolean isLocked(String sessionId) throws ProtocolException {

		if (sessionId == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!liveSessions.containsKey(sessionId)) {
			throw new ProtocolException("There is no existing session with ID: " + sessionId);
		}

		ProtocolPooled pooled = liveSessions.get(sessionId);

		return ((ReentrantLock) pooled.getLock()).isLocked();
	}

	private synchronized void lockProtocolSession(String sessionID) throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!liveSessions.containsKey(sessionID)) {
			throw new ProtocolException("There is no existing session with ID: " + sessionID);
		}

		// if (lockedProtocolSessions.contains(sessionID)) {
		// throw new ProtocolException("Trying to lock a sessionID that is currently locked");
		// }

		// while (lockedProtocolSessions.contains(sessionID)) {
		// try {
		// lockedProtocolSessions.wait();
		// } catch (InterruptedException e) {
		// }
		// }

		liveSessions.get(sessionID).getLock().lock();

		lockedProtocolSessions.add(sessionID);
	}

	private synchronized void unlockProtocolSession(String sessionID) throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!liveSessions.containsKey(sessionID)) {
			throw new ProtocolException("There is no existing session with ID: " + sessionID);
		}

		if (!lockedProtocolSessions.contains(sessionID)) {
			log.warn("The session identified by this sessionID is not currently locked. Ignoring unlock.");
		}

		lockedProtocolSessions.remove(sessionID);
		liveSessions.get(sessionID).getLock().unlock();
	}

	public synchronized void purgeOldSessions() throws ProtocolException {
		purgeOldSessions(expirationTime);
	}

	public synchronized void purgeOldSessions(long milis) throws ProtocolException {

		long now = System.currentTimeMillis();

		List<ProtocolPooled> toRemove = new ArrayList<ProtocolSessionManager.ProtocolPooled>();

		for (ProtocolPooled pooledSession : liveSessions.values()) {
			if ((now - pooledSession.lastUsed) > milis) {
				toRemove.add(pooledSession);
			}
		}

		for (int i = toRemove.size() - 1; i >= 0; i--) {
			ProtocolPooled pooledSession = toRemove.get(i);
			log.debug("Destroying session: " + pooledSession.getProtocolSession().getSessionId());
			destroyProtocolSession(pooledSession.getProtocolSession().getSessionId());
		}
	}

	@Override
	public synchronized IProtocolSession obtainSessionByProtocol(String protocol, boolean lock) throws ProtocolException {

		if (protocol == null)
			throw new ProtocolException("Requested protocol is null.");

		ProtocolSessionContext context = registeredContexts.get(protocol);
		if (context == null)
			throw new ProtocolException("No such registered context for protocol: " + protocol);

		return obtainSession(context, lock);
	}

	@Override
	public synchronized IProtocolSession obtainSessionById(String sessionId, boolean lock) throws ProtocolException {

		ProtocolPooled pooled = liveSessions.get(sessionId);

		if (pooled == null)
			throw new ProtocolException("Session id " + sessionId + "not found.");

		if (lock)
			lockProtocolSession(pooled.getProtocolSession().getSessionId());

		return pooled.getProtocolSession();
	}

	@Override
	public synchronized IProtocolSession obtainSession(ProtocolSessionContext context, boolean lock) throws ProtocolException {

		IProtocolSession session = null;

		if (liveSessions.size() > 0) {
			for (ProtocolPooled pooledSession : liveSessions.values()) {
				if (pooledSession.getProtocolSession().getSessionContext().equals(context))
					session = pooledSession.getProtocolSession();
				break;
			}
		}

		if (session == null) {
			session = createProtocolSession(context);
		}

		if (lock)
			lockProtocolSession(session.getSessionId());

		return session;
	}

	@Override
	public synchronized void releaseSession(String sessionId) throws ProtocolException {
		touchSession(sessionId);
		unlockProtocolSession(sessionId);
	}

	@Override
	public synchronized void releaseSession(IProtocolSession session) throws ProtocolException {
		releaseSession(session.getSessionId());
	}

	private void touchSession(String sessionId) {
		for (ProtocolPooled pooled : liveSessions.values())
			if (pooled.getProtocolSession().getSessionId().equals(sessionId))
				pooled.lastUsed = System.currentTimeMillis();
	}

	/**
	 * If you receive a message its a CONNECTION_LOST so destroy the session.
	 */
	@Override
	public void messageReceived(Object message) {
		if (message instanceof String) {
			try {
				destroyProtocolSession((String) message);
			} catch (ProtocolException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * Specify the type of message which we want to listen
	 */
	@Override
	public boolean notify(Object message) {
		if (message instanceof Status) {
			Status status = (Status) message;
			return status.equals(Status.CONNECTION_LOST);
		}

		return false;
	}

	private void registerAsSessionAlarmListener(EventHandler handler, String sessionId) throws ProtocolException {

		Properties filterProperties = new Properties();
		filterProperties.put(SessionAlarm.SESSION_ID_PROPERTY, sessionId);
		EventFilter filter = new EventFilter(new String[] { SessionAlarm.TOPIC }, filterProperties);

		int registrationNum = getEventManager().registerEventHandler(this, filter);
		sessionEventsListenerRegistrationNumbers.put(sessionId, registrationNum);

	}

	private void unregisterAsSessionAlarmListener(EventHandler handler, String sessionId) throws ProtocolException {
		getEventManager().unregisterHandler(sessionEventsListenerRegistrationNumbers.get(sessionId));
	}

	/**
	 * Callback called when events are received
	 */
	@Override
	public void handleEvent(Event event) {
		log.debug("ProtocolSessionManager received an event");
		if (event instanceof SessionAlarm) {
			Properties prop = new Properties();
			prop.put(CapabilityAlarm.RESOURCE_ID_PROPERTY, getResourceID());
			prop.put(CapabilityAlarm.CAUSE_PROPERTY, event);

			CapabilityAlarm alarm = new CapabilityAlarm(prop);
			try {
				publish(alarm);
			} catch (ProtocolException e) {
				log.error("Failed to publish alarm for resource: " + getResourceID(), e);
			}
		}
	}

	private void publish(Event event) throws ProtocolException {
		getEventManager().publishEvent(event);
	}

	/**
	 * @param eventManager
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	private IEventManager getEventManager() throws ProtocolException {
		if (this.eventManager == null)
			throw new ProtocolException("No eventManager found!");

		return this.eventManager;
	}

}