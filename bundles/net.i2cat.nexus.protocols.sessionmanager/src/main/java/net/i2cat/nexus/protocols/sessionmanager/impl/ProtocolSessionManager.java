package net.i2cat.nexus.protocols.sessionmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.i2cat.nexus.resources.protocol.IProtocolMessageFilter;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSession.Status;
import net.i2cat.nexus.resources.protocol.IProtocolSessionFactory;
import net.i2cat.nexus.resources.protocol.IProtocolSessionListener;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProtocolSessionManager implements IProtocolSessionManager, IProtocolSessionListener, IProtocolMessageFilter {

	private String								resourceID				= null;
	private Map<String, ProtocolPooled>			liveSessions			= null;
	private Map<String, ProtocolSessionContext>	liveSessionContexts		= null;
	private Map<String, ProtocolSessionContext>	registeredContexts		= null;
	private List<String>						lockedProtocolSessions	= null;
	private ProtocolManager						protocolManager			= null;

	Log											log						= LogFactory.getLog(ProtocolSessionManager.class);

	// TODO get this from the configuration
	private static long							expirationTime			= 3000 * 1000;										// milis

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

	public String getResourceID() {
		return resourceID;
	}

	public Set<String> getAllProtocolSessionIds() {
		return liveSessions.keySet();
	}

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
		liveSessions.put(sessionID, new ProtocolPooled(protocolSession, now));
		liveSessionContexts.put(sessionID, protocolSessionContext);
		protocolSession.connect();

		return protocolSession;
	}

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

		lockedProtocolSessions.remove(sessionID);
	}

	public void registerContext(ProtocolSessionContext context) throws ProtocolException {

		// ignore returned object, we do this for the throw.
		protocolManager.getSessionFactory((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));

		registeredContexts.put((String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL), context);
	}

	public void unregisterContext(String protocol) {
		registeredContexts.remove(protocol);
	}

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

	public synchronized void releaseSession(String sessionId) throws ProtocolException {
		touchSession(sessionId);
		unlockProtocolSession(sessionId);
	}

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
	public boolean notify(Object message) {
		if (message instanceof Status) {
			Status status = (Status) message;
			return status.equals(Status.CONNECTION_LOST);
		}

		return false;
	}
}