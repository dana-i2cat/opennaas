package org.opennaas.core.protocols.sessionmanager.tests;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.mock.MockEventManager;
import org.opennaas.core.resources.mock.MockProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class ProtocolManagerTest {

	String					resourceId	= "RandomDevice";

	ProtocolManager			protocolManager;
	ProtocolSessionManager	protocolSessionManager;

	ProtocolSessionContext	netconfContext;
	ProtocolSessionContext	mockContext;

	@SuppressWarnings("serial")
	@Before
	public void prepare() throws ProtocolException {

		protocolManager = new ProtocolManager();
		protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
		// trick to avoid registration as alarm listener errors
		protocolSessionManager.setEventManager(new MockEventManager());

		netconfContext = new ProtocolSessionContext();
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@server");

		mockContext = new ProtocolSessionContext();
		mockContext.addParameter(ProtocolSessionContext.PROTOCOL, "mock");
		mockContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "freeFormString");

		protocolManager.sessionFactoryAdded(new MockProtocolSessionFactory(), new HashMap<String, String>() {
			{
				put(ProtocolSessionContext.PROTOCOL, "netconf");
			}
		});
		protocolManager.sessionFactoryAdded(new MockProtocolSessionFactory(), new HashMap<String, String>() {
			{
				put(ProtocolSessionContext.PROTOCOL, "mock");
			}
		});

		protocolSessionManager.registerContext(netconfContext);
		protocolSessionManager.registerContext(mockContext);
	}

	@After
	public void clean() throws ProtocolException {
		protocolSessionManager.unregisterContext((String) netconfContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));
		protocolSessionManager.unregisterContext((String) mockContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));

		protocolManager.destroyProtocolSessionManager(resourceId);
	}

	@Test
	public void registerAContextWithoutFactory() {
		ProtocolSessionContext adhocContext = new ProtocolSessionContext();
		adhocContext.addParameter(ProtocolSessionContext.PROTOCOL, "nonExistingProtocol");
		adhocContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "adhocContext");

		try {
			protocolSessionManager.registerContext(adhocContext);
		} catch (ProtocolException e) {
			return; // all is nice
		}
		Assert.fail("Registering a context for a non-existing protocol worked. Should have failed.");
	}

	@Test
	public void obtainSessionByProtocolWithLock() throws ProtocolException {

		IProtocolSession session = protocolSessionManager.obtainSessionByProtocol("netconf", true);

		Assert.assertNotNull("The returned session is null", session);

		protocolSessionManager.releaseSession(session);
	}

	@Test
	public void obtainSessionByProtocolWithoutLock() throws ProtocolException {

		IProtocolSession session = protocolSessionManager.obtainSessionByProtocol("netconf", false);

		Assert.assertNotNull("The returned session is null", session);
	}

	@Test
	public void obtainSessionWithNewContext() throws ProtocolException {
		ProtocolSessionContext adhocContext = new ProtocolSessionContext();
		adhocContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		adhocContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@mockserver");

		IProtocolSession session = protocolSessionManager.obtainSession(adhocContext, true);

		Assert.assertNotNull("The returned session is null", session);
		Assert.assertEquals(session.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL),
				adhocContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL));

		protocolSessionManager.releaseSession(session);
	}

	@Test
	public void obtainSessionWithProtocol() throws ProtocolException {
		IProtocolSession session1 = protocolSessionManager.obtainSessionByProtocol("netconf", true);
		IProtocolSession session2 = protocolSessionManager.obtainSessionByProtocol("mock", true);
		protocolSessionManager.releaseSession(session1);
		protocolSessionManager.releaseSession(session2.getSessionId());
	}

	@Test
	public void obtainAndDestroySessionWithProtocol() throws ProtocolException {
		IProtocolSession session1 = protocolSessionManager.obtainSessionByProtocol("netconf", true);
		IProtocolSession session2 = protocolSessionManager.obtainSessionByProtocol("mock", true);
		protocolSessionManager.releaseSession(session1);
		protocolSessionManager.releaseSession(session2.getSessionId());

		protocolSessionManager.destroyProtocolSession((session1.getSessionId()));
		protocolSessionManager.destroyProtocolSession((session2.getSessionId()));
	}

	@Test
	public void obtainAndPurgeSessionWithProtocol() throws ProtocolException {
		IProtocolSession session1 = protocolSessionManager.obtainSessionByProtocol("netconf", true);
		IProtocolSession session2 = protocolSessionManager.obtainSessionByProtocol("mock", true);

		protocolSessionManager.releaseSession(session1);
		protocolSessionManager.releaseSession(session2.getSessionId());

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Assert.fail();
		}

		protocolSessionManager.purgeOldSessions(100);

		session1 = protocolSessionManager.obtainSessionByProtocol("netconf", true);
		session2 = protocolSessionManager.obtainSessionByProtocol("mock", true);

		protocolSessionManager.releaseSession(session1);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			Assert.fail();
		}

		protocolSessionManager.releaseSession(session2.getSessionId());

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			Assert.fail();
		}

		protocolSessionManager.purgeOldSessions(300);
		protocolSessionManager.purgeOldSessions(0);
	}

	@Test
	public void getProtocolSessionManagerWithContext() throws ProtocolException {
		String resourceIdTwo = "randomDevice2";
		netconfContext = new ProtocolSessionContext();
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@serve");

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceIdTwo, netconfContext);
		// trick to avoid registration as alarm listener errors
		((ProtocolSessionManager) protocolSessionManager).setEventManager(new MockEventManager());
		IProtocolSession protocolSession = protocolSessionManager.obtainSessionByProtocol("netconf", false);
		String protocol = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		String protocol_uri = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

		Assert.assertSame("netconf", protocol);
		Assert.assertSame("mock://user:pass@serve", protocol_uri);

	}

	@Test
	public void obtainSessionsByProtocol() throws ProtocolException {

		IProtocolSession session1 = protocolSessionManager.obtainSessionByProtocol("netconf", true);
		Assert.assertEquals(1, protocolSessionManager.getAllProtocolSessionIds().size());

		// adding another session with the same protocol (and context) reuses the session
		IProtocolSession session2 = protocolSessionManager.obtainSessionByProtocol("netconf", true);
		Assert.assertEquals(1, protocolSessionManager.getAllProtocolSessionIds().size());
	}

	@Test
	public void getProtocolSessionsWithSameContext() throws ProtocolException {

		String resourceIdTwo = "randomDevice2";
		netconfContext = new ProtocolSessionContext();
		netconfContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@serve");

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceIdTwo, netconfContext);
		// trick to avoid registration as alarm listener errors
		((ProtocolSessionManager) protocolSessionManager).setEventManager(new MockEventManager());

		IProtocolSession protocolSession1 = protocolSessionManager.obtainSessionByProtocol("netconf", false);
		IProtocolSession protocolSession2 = protocolSessionManager.obtainSessionByProtocol("netconf", false);
		IProtocolSession protocolSession3 = protocolSessionManager.obtainSession(netconfContext, false);
		IProtocolSession protocolSession4 = protocolSessionManager.obtainSession(netconfContext, false);

		// only 1 session per context is created.
		Assert.assertEquals(1, protocolSessionManager.getAllProtocolSessionIds().size());
		// The session is reused
		Assert.assertEquals(protocolSession1, protocolSession2);
		Assert.assertEquals(protocolSession1, protocolSession3);

	}

	@Test
	public void getProtocolSessionsWithDifferentContexts() throws ProtocolException {
		String resourceIdThree = "randomDevice3";

		// With two different contexts with the same protocol: netconfContext1 and netconfContext2
		// only the second one should be registered
		ProtocolSessionContext netconfContext1 = new ProtocolSessionContext();
		netconfContext1.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");
		netconfContext1.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		netconfContext1.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@serve");

		ProtocolSessionContext netconfContext2 = new ProtocolSessionContext();
		netconfContext2.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");
		netconfContext2.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		netconfContext2.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@serve");
		netconfContext2.addParameter(ProtocolSessionContext.KEY_USERNAME, "pocahontas");

		// netconfContext1
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceIdThree, netconfContext1);
		// trick to avoid registration as alarm listener errors
		((ProtocolSessionManager) protocolSessionManager).setEventManager(new MockEventManager());

		IProtocolSession protocolSession1 = protocolSessionManager.obtainSession(netconfContext1, false);
		Assert.assertEquals(1, protocolSessionManager.getAllProtocolSessionIds().size());

		// netconfContext2: register with a new context using the same protocol. Only the second context should be registered
		protocolSessionManager.registerContext(netconfContext2);
		Assert.assertEquals("Only 1 context with the same protocol allowed ", 1, protocolSessionManager.getRegisteredContexts().size());
		Assert.assertTrue("Only 2nd context is registered ", protocolSessionManager.getRegisteredContexts().contains(netconfContext2));

		// and only one session should be created
		IProtocolSession protocolSession2 = protocolSessionManager.obtainSession(netconfContext, false);
		Assert.assertEquals(1, protocolSessionManager.getAllProtocolSessionIds().size());

		// Registering with New context with different protocol should be allowed
		mockContext = new ProtocolSessionContext();
		mockContext.addParameter(ProtocolSessionContext.PROTOCOL, "mock");
		mockContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "freeFormString");

		// Both contexts should be registered
		protocolSessionManager.registerContext(mockContext);
		Assert.assertEquals("2 contexts with different protocols allowed", 2, protocolSessionManager.getRegisteredContexts().size());

		Assert.assertTrue(protocolSessionManager.getRegisteredContexts().contains(mockContext));
		Assert.assertTrue(protocolSessionManager.getRegisteredContexts().contains(netconfContext2));

		// and two sessions should be created
		IProtocolSession protocolSession3 = protocolSessionManager.obtainSession(mockContext, false);
		Assert.assertEquals(2, protocolSessionManager.getAllProtocolSessionIds().size());

	}
}
