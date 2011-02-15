package net.i2cat.nexus.protocols.sessionmanager.tests;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.nexus.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.nexus.protocols.sessionmanager.tests.mock.MockProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.tests.mock.MockProtocolSessionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManagerTest {
	String					deviceID	= "m10";
	Logger					log			= LoggerFactory.getLogger(SessionManagerTest.class);
	IProtocolSessionManager	protocolSessionManager;

	@Before
	public void prepareFactory() {
		try {
			ProtocolManager protocolManager = new ProtocolManager();
			String idProtocolSessionManager = protocolManager.createProtocolSessionManager(deviceID);
			protocolSessionManager = protocolManager.getProtocolSessionManager(idProtocolSessionManager);

			/*
			 * Register a protocol factory. These steps should be done for the
			 * blueprint configuration
			 */
			MockProtocolSessionFactory protocolNetconfFactory = new MockProtocolSessionFactory();

			Map serviceProperties = new HashMap<String, IProtocolSessionFactory>();
			serviceProperties.put(ProtocolSessionContext.PROTOCOL, "mock");
			protocolManager.sessionFactoryAdded(protocolNetconfFactory, serviceProperties);
		} catch (ProtocolException e) {
			log.error(e.getMessage());
		}
	}

	@Test
	public void poolTest() {
		try {

			/* use a protocol */
			log.info("Getting our new protocol session, we are creating one new");

			String sessionID = protocolSessionManager.checkOut(newMockProtocolSessionContext());
			IProtocolSession protocolSession = protocolSessionManager.getProtocolSession(sessionID, true);

			printProtocolSessionInfo(protocolSession);
			log.info("Getting protocolSession: " + protocolSession.getSessionID());

			String response = (String) protocolSession.sendReceive("fake message");
			Assert.assertNotNull(response);
			log.info("checking session....");
			protocolSessionManager.checkIn(sessionID);
			/* ------------------------------------------------ */

			/* use other protocol */
			log.info("Getting a new protocol session, we are creating one new");

			String newSessionID =
					protocolSessionManager.checkOut(newMockProtocolSessionContext2());
			if (newSessionID.equals(sessionID)) {
				log.error("the protocol restored is other");
				Assert.fail("the protocol restored is other");
			}

			protocolSession =
					protocolSessionManager.getProtocolSession(newSessionID, true);

			// FIX. if you use a mock protocol, to have to change to session
			// context which it knows or this test won't be able to difference
			// protocol session
			ProtocolSessionContext protocolSessionContext = newMockProtocolSessionContext2();
			((MockProtocolSession) protocolSession).setSessionContext(protocolSessionContext);
			// FIX

			printProtocolSessionInfo(protocolSession);
			log.info("Getting new protocolSession: " +
					protocolSession.getSessionID());

			response = (String) protocolSession.sendReceive("fake message");
			Assert.assertNotNull(response);

			log.info("checking session....");
			protocolSessionManager.checkIn(newSessionID);

			/* ------------------------------------------------ */

			/* reuse a protocol */
			String recoverSessionID = protocolSessionManager.checkOut(newMockProtocolSessionContext());
			log.info("Restoring a new protocol session, we are creating one new");
			log.info("Reusing protocolSession: " + recoverSessionID);

			if (!sessionID.equals(recoverSessionID))
				Assert.fail("it is not the same session, the pooling doesn't work");

			protocolSession = protocolSessionManager.getProtocolSession(recoverSessionID, true);

			printProtocolSessionInfo(protocolSession);
			log.info("Reusing new protocolSession: " + protocolSession.getSessionID());

			response = (String) protocolSession.sendReceive("fake message");

			log.info("checking session....");
			protocolSessionManager.checkIn(recoverSessionID);

			/* ------------------------------------------------ */

		} catch (ProtocolException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}

	@Test
	public void testLockSessionsTest() {

		try {
			/* use a protocol */
			log.info("Getting our new protocol session, we are creating one new");
			String sessionID = protocolSessionManager.checkOut(newMockProtocolSessionContext());
			IProtocolSession protocolSession = protocolSessionManager.getProtocolSession(sessionID, true);
			protocolSession = protocolSessionManager.getProtocolSession(sessionID, true);

			/* ------------------------------------------------ */
		} catch (ProtocolException e) {
			log.info("It was impossible to create other netconf protocol session");
			log.info(e.getMessage());
		}
	}

	private ProtocolSessionContext newMockProtocolSessionContext() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "mock");
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:bar@foo:22/mock");
		return protocolSessionContext;
	}

	private ProtocolSessionContext newMockProtocolSessionContext2() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "mock");
		protocolSessionContext.addParameter("protocol.uri", "mock://FOO2:OTHER2@foo:22/mock");
		return protocolSessionContext;
	}

	private void printProtocolSessionInfo(IProtocolSession protocolSession) {
		log.info("Protocol session info: ");
		log.info((String) protocolSession.getSessionContext().getSessionParameters().get("protocol"));
		log.info((String) protocolSession.getSessionContext().getSessionParameters().get("protocol.uri"));
		log.info("-------------------------");
	}
}
