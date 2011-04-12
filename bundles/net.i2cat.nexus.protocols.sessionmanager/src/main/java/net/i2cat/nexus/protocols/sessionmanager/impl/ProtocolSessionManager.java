package net.i2cat.nexus.protocols.sessionmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolMessageFilter;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession.Status;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionListener;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolSessionManager implements IProtocolSessionManager,
		IProtocolSessionListener, IProtocolMessageFilter {

	private String								deviceID				= null;
	private Map<String, ProtocolPooled>			protocolSessions		= null;
	private Map<String, ProtocolSessionContext>	protocolSessionContexts	= null;
	private List<String>						lockedProtocolSessions	= null;
	private ProtocolManager						protocolManager			= null;

	Logger										log						= LoggerFactory
																				.getLogger(ProtocolSessionManager.class);

	public ProtocolSessionManager(String deviceID) {
		this.deviceID = deviceID;
		this.protocolSessions = new HashMap<String, ProtocolPooled>();
		this.protocolSessionContexts = new HashMap<String, ProtocolSessionContext>();
		this.lockedProtocolSessions = new ArrayList<String>();
	}

	public void setProtocolManager(ProtocolManager protocolManager) {
		this.protocolManager = protocolManager;
	}

	public synchronized String createProtocolSession(
			ProtocolSessionContext protocolSessionContext)
			throws ProtocolException {
		long now = System.currentTimeMillis();

		String protocol = (String) protocolSessionContext
				.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		IProtocolSessionFactory protocolFactory = protocolManager
				.getSessionFactory(protocol);
		String sessionID = UUID.randomUUID().toString();
		IProtocolSession protocolSession = protocolFactory
				.createProtocolSession(sessionID, protocolSessionContext);

		/* Active listener */
		protocolSession.registerProtocolSessionListener(this, this, sessionID);
		protocolSessions.put(sessionID,
				new ProtocolPooled(protocolSession, now));
		protocolSessionContexts.put(sessionID, protocolSessionContext);
		protocolSession.connect();

		return sessionID;
	}

	private synchronized String getProtocolSession(
			ProtocolSessionContext protocolSessionContext)
			throws ProtocolException {
		if (protocolSessionContext == null) {
			throw new ProtocolException(
					"The protocol session context provided is null");
		}

		Iterator<Entry<String, ProtocolSessionContext>> iterator = protocolSessionContexts
				.entrySet().iterator();
		Entry<String, ProtocolSessionContext> entry = null;

		while (iterator.hasNext()) {
			entry = iterator.next();
			if (entry.getValue().equals(protocolSessionContext)) {
				return entry.getKey();
			}
		}

		// FIXME EXIST A BETTER OPTION??
		return null;
	}

	public synchronized void destroyProtocolSession(String sessionID)
			throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!protocolSessions.containsKey(sessionID)) {
			throw new ProtocolException(
					"There is no existing session with this ID");
		}

		if (lockedProtocolSessions.contains(sessionID)) {
			// TODO what to do if the session is locked? It means is in use by
			// someone, should we throw an
			// exception or let the close proceed?
		}

		IProtocolSession protocolSession = protocolSessions.get(sessionID)
				.getProtocolSession();

		/* disconnect the session */
		if (protocolSession.getStatus().equals(Status.CONNECTED)) {
			protocolSession.disconnect();
		}

		protocolSessions.remove(sessionID);
		protocolSessionContexts.remove(sessionID);
		protocolSession.registerProtocolSessionListener(this, this, sessionID);

	}

	public Set<String> getAllProtocolSessions() {
		return protocolSessions.keySet();
	}

	public String getDeviceID() {
		return deviceID;
	}

	public synchronized IProtocolSession getProtocolSession(String sessionID,
			boolean lock) throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!protocolSessions.containsKey(sessionID)) {
			throw new ProtocolException(
					"There is no existing session with this ID");
		}

		if (lockedProtocolSessions.contains(sessionID)) {
			throw new ProtocolException(
					"The session identified by this sessionID is currently locked");
		}

		if (lock) {
			lockedProtocolSessions.add(sessionID);
		}

		return protocolSessions.get(sessionID).getProtocolSession();
	}

	public synchronized void returnProtocolSession(String sessionID)
			throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!protocolSessions.containsKey(sessionID)) {
			throw new ProtocolException(
					"There is no existing session with this ID");
		}

		if (!lockedProtocolSessions.contains(sessionID)) {
			throw new ProtocolException(
					"The session identified by this sessionID is not locked currently");
		}

		lockedProtocolSessions.remove(sessionID);
	}

	class ProtocolPooled {
		private long				timeToExpire;
		private IProtocolSession	protocolSession;

		public ProtocolPooled(IProtocolSession protocolSession,
				long timeToExpire) {
			this.setTimeToExpire(timeToExpire);
			this.setProtocolSession(protocolSession);
		}

		public void setProtocolSession(IProtocolSession protocolSession) {
			this.protocolSession = protocolSession;
		}

		public IProtocolSession getProtocolSession() {
			return protocolSession;
		}

		public void setTimeToExpire(long timeToExpire) {
			this.timeToExpire = timeToExpire;
		}

		public long getTimeToExpire() {
			return timeToExpire;
		}

	}

	private static long	expirationTime	= 3000 * 1000;	// 1000 (1 milisec)

	public synchronized String checkOut(
			ProtocolSessionContext protocolSessionContext)
			throws ProtocolException {
		long now = System.currentTimeMillis();
		if (protocolSessions.size() > 0) {
			Iterator<String> sessionIDs = protocolSessions.keySet().iterator();
			while (sessionIDs.hasNext()) {
				String sessionID = sessionIDs.next();
				ProtocolPooled protocolPooled = protocolSessions.get(sessionID);
				if ((now - protocolPooled.timeToExpire) > expirationTime) {
					destroyProtocolSession(sessionID);
				} else {
					if (validateProtocolSession(protocolPooled,
							protocolSessionContext, sessionID)) {
						protocolPooled.setTimeToExpire(now);
						return sessionID;
					}
				}
			}
		}

		// no objects available, create a new one
		return createProtocolSession(protocolSessionContext);

	}

	private boolean validateProtocolSession(ProtocolPooled protocolPooled,
			ProtocolSessionContext protocolSessionContext, String sessionID) {
		boolean sameConfig = protocolPooled.getProtocolSession()
				.getSessionContext().equals(protocolSessionContext);
		return (sameConfig && !lockedProtocolSessions.contains(sessionID));
	}

	public synchronized void checkIn(String sessionID) throws ProtocolException {
		returnProtocolSession(sessionID);
	}

	/**
	 * If you receive a listener message, it implements its action
	 */

	public void messageReceived(Object message) {
		if (message instanceof String) {
			String sessionID = (String) message;
			try {
				destroyProtocolSession(sessionID);
			} catch (ProtocolException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}

	}

	/**
	 * Specify the type of message which we want to use
	 */

	public boolean notify(Object message) {
		if (message instanceof Status) {
			Status status = (Status) message;
			return status.equals(Status.CONNECTION_LOST);
		}

		return false;
	}

}