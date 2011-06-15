package net.i2cat.nexus.protocols.sessionmanager.tests;

import java.util.HashMap;

import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.tests.mock.MockProtocolSessionFactory;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

	// @Test FIXME This test throws a ConcurrentModificationException and its not nice.
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
	public void getProtocolSessionManagerWithContext() throws ProtocolException {
		String resourceIdTwo = "randomDevice2";
		netconfContext = new ProtocolSessionContext();
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		netconfContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@serve");

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceIdTwo, netconfContext);
		IProtocolSession protocolSession = protocolSessionManager.obtainSessionByProtocol("netconf", false);
		String protocol = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL);
		String protocol_uri = (String) protocolSession.getSessionContext().getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

		Assert.assertSame("netconf", protocol);
		Assert.assertSame("mock://user:pass@serve", protocol_uri);

	}

}
