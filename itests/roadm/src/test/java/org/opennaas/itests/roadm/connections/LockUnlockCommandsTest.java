package org.opennaas.itests.roadm.connections;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.LockNodeCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.UnlockNodeCommand;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public class LockUnlockCommandsTest {

	Log				log				= LogFactory.getLog(LockUnlockCommandsTest.class);

	private String	resourceId		= "pedrosa";
	private String	hostIpAddress	= "10.10.80.11";
	private String	hostPort		= "27773";
	private int		sessionCounter	= 0;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Test all we want loading container only once (to speed up tests)
	 */
	@Test
	public void testLockUnlockFunctionality() {
		// initBundles();
		testLockUnlock();
		testLockMultipleTimes();
		testUnlockWithoutLock();
		testLockUnlockInDifferentSessions();
	}

	private void testLockUnlock() {

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

			session.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail(e.getLocalizedMessage());
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail(e.getLocalizedMessage());
		}

	}

	private void testLockMultipleTimes() {
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

			session.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	private void testUnlockWithoutLock() {
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

			session.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
	}

	private void testLockUnlockInDifferentSessions() {
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

			session1.disconnect();
			session2.disconnect();
			log.info("Done!");

		} catch (ProtocolException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			Assert.fail();
		}
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
