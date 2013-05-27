package org.opennaas.core.protocols.sessionmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
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
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.alarms.SessionAlarm;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSession.Status;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ProtocolSessionManager implements IProtocolSessionManager, IProtocolSessionListener, IProtocolMessageFilter, EventHandler {

	private String								resourceID									= null;
	/* key: sessionId, value: pooled session */
	private Map<String, ProtocolPooled>			liveSessions								= null;
	/* key: sessionId, value: context */
	private Map<String, ProtocolSessionContext>	liveSessionContexts							= null;
	/* key: protocol, value: context */
	private Map<String, ProtocolSessionContext>	registeredContexts							= null;
	private List<String>						lockedProtocolSessions						= null;
	private ProtocolManager						protocolManager								= null;
	private IEventManager						eventManager;

	Log											log											= LogFactory.getLog(ProtocolSessionManager.class);

	/**
	 * key: sessionId value: registration number
	 */
	private Map<String, Integer>				sessionEventsListenerRegistrationNumbers	= new HashMap<String, Integer>();

	private ServiceRegistration					registration;

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
	public ListResponse getAllProtocolSessionIdsWS() {
		ListResponse resp = new ListResponse();
		resp.setList(new ArrayList<String>(getAllProtocolSessionIds()));
		return resp;
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
			protocolSession.unregisterProtocolSessionListener(this, sessionID);
			throw new ProtocolException("Failed to register as session alarms listener for session " + sessionID, e);
		}

		liveSessions.put(sessionID, new ProtocolPooled(protocolSession, now));
		liveSessionContexts.put(sessionID, protocolSessionContext);
		protocolSession.connect();

		return protocolSession;
	}

	@Override
	public synchronized void destroyProtocolSession(String sessionID) throws ProtocolException {

		IProtocolSession protocolSession = getSessionById(sessionID);
		if (protocolSession == null) {
			throw new ProtocolException("There is no existing session with this ID: " + sessionID);
		}

		lockProtocolSession(sessionID);

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
	public synchronized IProtocolSession obtainSessionByProtocol(String protocol, boolean lock) throws ProtocolException {

		if (protocol == null)
			throw new ProtocolException("Requested protocol is null.");

		ProtocolSessionContext context = registeredContexts.get(protocol);
		if (context == null)
			throw new ProtocolException("No such registered context for protocol: " + protocol);

		return obtainSession(context, lock);
	}

	@Override
	public synchronized IProtocolSession obtainSession(ProtocolSessionContext context, boolean lock) throws ProtocolException {

		IProtocolSession session = getSessionByContext(context);

		if (session == null) {
			session = createProtocolSession(context);
		}

		if (lock)
			lockProtocolSession(session.getSessionId());

		return session;
	}

	@Override
	public synchronized IProtocolSession getSessionById(String sessionId, boolean lock) throws ProtocolException {

		IProtocolSession session = getSessionById(sessionId);
		if (session == null)
			throw new ProtocolException("There is no existing session with ID: " + sessionId);

		if (lock)
			lockProtocolSession(sessionId);

		return session;
	}

	/**
	 * 
	 * @param sessionId
	 * @return IProtocolSession with given sessionId, if there is one, or null otherwise
	 */
	private synchronized IProtocolSession getSessionById(String sessionId) {
		if (sessionId == null)
			return null;

		ProtocolPooled pooled = liveSessions.get(sessionId);
		if (pooled == null)
			return null;

		return pooled.getProtocolSession();
	}

	/**
	 * 
	 * @param context
	 * @return IProtocolSession with given context, if there is one, or null otherwise
	 */
	private IProtocolSession getSessionByContext(ProtocolSessionContext context) {
		for (ProtocolPooled pooledSession : liveSessions.values()) {
			if (pooledSession.getProtocolSession().getSessionContext().equals(context)) {
				return pooledSession.getProtocolSession();
			}
		}
		return null;
	}

	@Override
	public void registerContext(ProtocolSessionContext context) throws ProtocolException {

		String protocol = (String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);

		checkIsSupportedProtocol(protocol);

		// unregister the old context (if any)
		unregisterContext(protocol);

		registeredContexts.put(protocol, context);
	}

	@Override
	public void unregisterContext(String protocol) throws ProtocolException {
		ProtocolSessionContext removedContext = registeredContexts.remove(protocol);
		if (removedContext != null) {
			IProtocolSession toDestroy = getSessionByContext(removedContext);
			if (toDestroy != null) {
				destroyProtocolSession(toDestroy.getSessionId());
			}
		}
	}

	@Override
	public void unregisterContext(ProtocolSessionContext context) throws ProtocolException {
		String protocol = (String) context.getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		unregisterContext(protocol);
	}

	private void checkIsSupportedProtocol(String protocol) throws ProtocolException {
		if (!protocolManager.isSupportedProtocol(protocol)) {
			throw new ProtocolException("Unsupported protocol " + protocol);
		}
	}

	private synchronized void lockProtocolSession(String sessionID) throws ProtocolException {
		if (sessionID == null) {
			throw new ProtocolException("The session ID provided is null");
		}

		if (!liveSessions.containsKey(sessionID)) {
			throw new ProtocolException("There is no existing session with ID: " + sessionID);
		}

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
	public synchronized void releaseSession(String sessionId) throws ProtocolException {
		touchSession(sessionId);
		unlockProtocolSession(sessionId);
	}

	@Override
	public synchronized void releaseSession(IProtocolSession session) throws ProtocolException {
		releaseSession(session.getSessionId());
	}

	private void touchSession(String sessionId) {
		if (sessionId != null) {
			ProtocolPooled pooled = liveSessions.get(sessionId);
			if (pooled != null) {
				pooled.lastUsed = System.currentTimeMillis();
			}
		}
	}

	/* EVENTS METHODS */

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

	public void registerAsOSGiService() throws ProtocolException {
		registration = null;
		if (Activator.getBundleContext() != null) {
			Dictionary<String, String> props = new Hashtable<String, String>();
			props.put("resourceId", resourceID);
			props = addWSRegistrationProperties(props);

			registration = Activator.getBundleContext().registerService(IProtocolSessionManager.class.getName(), this, props);
		}
	}

	public void unregisterAsOSGiService() {
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
	}

	private Dictionary<String, String> addWSRegistrationProperties(Dictionary<String, String> props) throws ProtocolException {
		IResource resource;
		try {
			resource = getResource(resourceID);
			String resourceType = resource.getResourceDescriptor().getInformation().getType();
			String resourceName = resource.getResourceDescriptor().getInformation().getName();

			ConfigurationAdminUtil configurationAdmin = new ConfigurationAdminUtil(Activator.getBundleContext());
			String url = configurationAdmin.getProperty("org.opennaas", "ws.rest.url");

			if (props != null) {
				props.put("service.exported.interfaces", "*");
				props.put("service.exported.configs", "org.apache.cxf.rs");
				props.put("org.apache.cxf.ws.address", url + "/" + resourceType + "/" + resourceName + "/protocolSessionManager" + "/");
			}
		} catch (ResourceException e) {
			throw new ProtocolException(e);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
		return props;
	}

	private IResource getResource(String resourceId) throws ResourceException {
		try {
			IResource resource = Activator.getResourceManagerService().getResourceById(resourceID);
			if (resource == null) {
				throw new ResourceException("Given resource does not exist in ResourceManager");
			}
			return resource;
		} catch (ResourceException e) {
			throw new ResourceException("Given resource does not exist in ResourceManager", e);
		} catch (ActivatorException e) {
			throw new ResourceException("Fail to check existence of given resource", e);
		}
	}

}