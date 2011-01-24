import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSession;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSessionFactory;
import net.i2cat.mantychore.protocols.sessionmanager.IProtocolSessionManager;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolException;
import net.i2cat.mantychore.protocols.sessionmanager.ProtocolSessionContext;
import net.i2cat.mantychore.protocols.sessionmanager.impl.ProtocolManager;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

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
			String idProtocolSessionManager;

			idProtocolSessionManager = protocolManager.createProtocolSessionManager(deviceID);
			protocolSessionManager = protocolManager.getProtocolSessionManager(idProtocolSessionManager);

			/*
			 * Register a protocol factory. These steps should be done for the
			 * blueprint configuration
			 */
			NetconfProtocolSessionFactory protocolNetconfFactory = new NetconfProtocolSessionFactory();

			Map serviceProperties = new HashMap<String, IProtocolSessionFactory>();
			serviceProperties.put(ProtocolSessionContext.PROTOCOL, "netconf");
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

			Reply response = (Reply) protocolSession.sendReceive(createKeepAlive());
			if (response.getErrors() != null && response.getErrors().size() > 0) {
				log.error("the response contains errors: " + response.getErrors().get(0).getMessage());
			}
			log.info("checking session....");
			protocolSessionManager.checkIn(sessionID);
			/* ------------------------------------------------ */

			/* use other protocol */
			log.info("Getting a new protocol session, we are creating one new");

			String newSessionID = protocolSessionManager.checkOut(newMockProtocolSessionContext2());
			if (newSessionID.equals(sessionID))
				log.error("the protocol restored is other");

			protocolSession = protocolSessionManager.getProtocolSession(newSessionID, true);

			printProtocolSessionInfo(protocolSession);
			log.info("Getting new protocolSession: " + protocolSession.getSessionID());

			response = (Reply) protocolSession.sendReceive(createKeepAlive());

			if (response.getErrors() != null && response.getErrors().size() > 0) {
				log.error("the response contains errors: " + response.getErrors().get(0).getMessage());
			}

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

			response = (Reply) protocolSession.sendReceive(createKeepAlive());

			if (response.getErrors() != null && response.getErrors().size() > 0) {
				log.error("the response contains errors: " + response.getErrors().get(0).getMessage());
			}

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
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		protocolSessionContext.addParameter("protocol.uri", "mock://foo:bar@foo:22/netconf");
		return protocolSessionContext;
	}

	private ProtocolSessionContext newMockProtocolSessionContext2() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		protocolSessionContext.addParameter("protocol.uri", "mock://foo2:bar2@foo:22/netconf");
		return protocolSessionContext;
	}

	private Query createKeepAlive() {
		Query query = QueryFactory.newKeepAlive();
		return query;
	}

	private void printProtocolSessionInfo(IProtocolSession protocolSession) {
		log.info("Protocol session info: ");
		log.info((String) protocolSession.getSessionContext().getSessionParameters().get("protocol"));
		log.info((String) protocolSession.getSessionContext().getSessionParameters().get("protocol.uri"));
		log.info("-------------------------");
	}
}
