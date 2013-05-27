package org.opennaas.extensions.roadm.wonesys.commandsets.test;

import java.util.HashMap;

import org.opennaas.extensions.roadm.wonesys.actionsets.actions.queue.ConfirmAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.queue.PrepareAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.queue.RestoreAction;
import org.opennaas.extensions.roadm.wonesys.commandsets.test.mock.MockProtocolSessionManager;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LockAndUnlockActionTest {
	Log								log			= LogFactory.getLog(LockAndUnlockActionTest.class);

	WonesysProtocolSession			session;
	// //
	private static PrepareAction	prepareAction;
	private static ConfirmAction	confirmAction;
	private static RestoreAction	restoreAction;
	private String					resourceId	= "pedrosa";

	// static ActionTestHelper helper;
	// static ProtocolSessionManager protocolsessionmanager;

	@Before
	public void init() {
		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);

		/* prepare action */
		prepareAction = new PrepareAction();
		prepareAction.setModelToUpdate(opticalSwitch1);

		/* confirm action */
		confirmAction = new ConfirmAction();
		confirmAction.setModelToUpdate(opticalSwitch1);

		/* restore action */
		restoreAction = new RestoreAction();
		restoreAction.setModelToUpdate(opticalSwitch1);

		log.info("init test");
	}

	@Test
	public void testPrepareActionMockExecute() {
		log.info("Testing Action with mock connection");

		try {
			ActionResponse response = prepareAction.execute(getMockProtocolSessionManager());

			Assert.assertTrue(response.getStatus().equals(STATUS.OK));

			for (Response resp : response.getResponses()) {

				Assert.assertTrue(resp.getStatus().equals(Status.OK));
			}

			// ProteusOpticalSwitch opticalSwitch = (ProteusOpticalSwitch) action.getModelToUpdate();
			// checkModelIsRefreshed(opticalSwitch);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testConfirmActionMockExecute() {
		log.info("Testing Action with mock connection");

		try {

			ProtocolSessionManager protocolSessionManager = getMockProtocolSessionManager();
			/**
			 * BUG to do an unlock, before it is necessary to do an lock
			 */
			prepareAction.execute(protocolSessionManager);

			ActionResponse response = confirmAction.execute(protocolSessionManager);

			Assert.assertTrue(response.getStatus().equals(STATUS.OK));

			for (Response resp : response.getResponses()) {

				Assert.assertTrue(resp.getStatus().equals(Status.OK));
			}

			// ProteusOpticalSwitch opticalSwitch = (ProteusOpticalSwitch) action.getModelToUpdate();
			// checkModelIsRefreshed(opticalSwitch);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testRestoreActionMockExecute() {
		log.info("Testing Action with mock connection");
		ProtocolSessionManager protocolSessionManager = getMockProtocolSessionManager();

		try {
			/**
			 * BUG to do an unlock, before it is necessary to do an lock
			 */
			prepareAction.execute(protocolSessionManager);
			ActionResponse response = restoreAction.execute(protocolSessionManager);

			Assert.assertTrue(response.getStatus().equals(STATUS.ERROR));

			for (Response resp : response.getResponses()) {

				Assert.assertTrue(resp.getStatus().equals(Status.OK));
			}

			// ProteusOpticalSwitch opticalSwitch = (ProteusOpticalSwitch) action.getModelToUpdate();
			// checkModelIsRefreshed(opticalSwitch);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	public ProtocolSessionManager getMockProtocolSessionManager() {

		ProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager(resourceId);
		return protocolSessionManager;
	}

	public ProtocolSessionManager getProtocolSessionManager() throws ProtocolException {

		ProtocolManager protocolManager = new ProtocolManager();
		ProtocolSessionManager protocolSessionManager = null;

		protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceId);
		ProtocolSessionContext wonesysContext = newSessionContextWonesys();
		protocolManager.sessionFactoryAdded(new WonesysProtocolSessionFactory(), new HashMap<String, String>() {
			{
				put(ProtocolSessionContext.PROTOCOL, "wonesys");
			}
		});
		protocolSessionManager.registerContext(wonesysContext);

		return protocolSessionManager;
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextWonesys() {
		String hostIpAddress = "10.10.80.11";
		String hostPort = "27773";
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "wonesys://" + hostIpAddress + ":" + hostPort);
		return protocolSessionContext;

	}

}
