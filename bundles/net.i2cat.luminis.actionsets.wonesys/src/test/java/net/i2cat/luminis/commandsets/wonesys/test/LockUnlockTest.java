package net.i2cat.luminis.commandsets.wonesys.test;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.LockNodeCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.UnlockNodeCommand;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSessionFactory;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.nexus.resources.command.CommandException;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.command.Response.Status;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class LockUnlockTest {

	Log				log				= LogFactory.getLog(LockUnlockTest.class);

	private String	resourceId		= "pedrosa";
	private String	hostIpAddress	= "10.10.80.11";
	private String	hostPort		= "27773";
	private int		sessionCounter	= 0;

	@Test
	public void testLockUnlock() {

		try {
			WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Adquiring lock...");

			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) session.sendReceive(c.message());

			log.info("Response: " + response);

			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Adquired!");

			log.info("Unlocking...");
			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			// try {
			// executeUnlockInNewSession();
			// } catch (Exception e1) {
			// log.error("Error executing unlock !!!!", e1);
			// }
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			// try {
			// executeUnlockInNewSession();
			// } catch (Exception e1) {
			// log.error("Error executing unlock !!!!", e1);
			// }
			Assert.fail();
		}
	}

	@Test
	public void testLockMultipleTimes() {
		try {

			WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Adquiring lock...");

			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Adquired!");

			log.info("Adquiring lock AGAIN...");

			// lockNode
			c = new LockNodeCommand();
			c.initialize();
			response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Adquired!");

			log.info("Unlocking...");
			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) session.sendReceive(c.message());
			log.info("Response: " + response);
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	@Test
	public void testUnlockWithoutLock() {
		try {

			WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Unlocking...");

			// unlockNode
			WonesysCommand c = new UnlockNodeCommand();
			c.initialize();
			String response = (String) session.sendReceive(c.message());
			Response resp = c.checkResponse(response);

			Assert.assertTrue(resp.getStatus().equals(Status.ERROR));
			Assert.assertTrue(resp.getErrors().contains("Node is not locked"));

			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	@Test
	public void testLockUnlockInDifferentSessions() {
		try {

			log.info("Testing LockUnlockInDifferentSessions ...");

			WonesysProtocolSession session1 = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);
			WonesysProtocolSession session2 = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

			if ((session1.getSessionContext().getSessionParameters().containsKey("protocol.mock") &&
					session1.getSessionContext().getSessionParameters().get("protocol.mock").equals("true")) ||
					(session2.getSessionContext().getSessionParameters().containsKey("protocol.mock") &&
					session2.getSessionContext().getSessionParameters().get("protocol.mock").equals("true"))) {
				// This test fails using mock device, as Proteus mock transport is not session aware
				log.info("Skipping test: not supported using mock protocol.");
				return;
			}

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			log.info("Adquiring lock...");
			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String response = (String) session1.sendReceive(c.message());
			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			log.info("Lock adquired!");

			log.info("Unlocking...");

			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) session2.sendReceive(c.message());
			resp = c.checkResponse(response);

			log.info("Checking unlock failed with reason Node is locked...");
			Assert.assertTrue(resp.getStatus().equals(Status.ERROR));
			log.info("Error reason: " + resp.getErrors().get(0));
			Assert.assertTrue(resp.getErrors().contains("Node is locked"));
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	private void executeUnlockInNewSession() throws CommandException, ProtocolException {

		WonesysProtocolSession session = (WonesysProtocolSession) getSession(resourceId, hostIpAddress, hostPort);

		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);

		// unlockNode
		WonesysCommand c = new UnlockNodeCommand();
		c.initialize();
		String response = (String) session.sendReceive(c.message());
		Response resp = c.checkResponse(response);
		c.parseResponse(resp, opticalSwitch1);

	}

	private IProtocolSession getSession(String resourceId, String hostIpAddress, String hostPort) throws ProtocolException {

		ProtocolSessionContext sessionContext = createWonesysProtocolSessionContext(hostIpAddress, hostPort);

		// get WonesysProtocolSession using ProtocolSessionManager
		IProtocolSession protocolSession = getProtocolSession(resourceId, sessionContext);
		if (protocolSession == null)
			throw new ProtocolException("Could not get a valid ProtocolSession");

		return protocolSession;
	}

	private ProtocolSessionContext createWonesysProtocolSessionContext(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + ip + ":" + port);
		protocolSessionContext.addParameter("protocol.mock", "true");
		return protocolSessionContext;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {
		WonesysProtocolSessionFactory factory = new WonesysProtocolSessionFactory();
		IProtocolSession protocolSession = factory.createProtocolSession(resourceId + sessionCounter, sessionContext);
		sessionCounter++;
		protocolSession.connect();
		return protocolSession;
	}

}
