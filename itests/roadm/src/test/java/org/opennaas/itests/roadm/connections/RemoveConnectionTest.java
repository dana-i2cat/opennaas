package org.opennaas.itests.roadm.connections;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.MakeConnectionAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RemoveConnectionAction;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchCardFactory;
import org.opennaas.extensions.router.model.utils.OpticalSwitchFactory;
import org.opennaas.itests.roadm.mock.MockProtocolSessionManager;

public class RemoveConnectionTest {
	OpticalSwitchFactory	switchFactory;

	Log						log	= LogFactory.getLog(RemoveConnectionTest.class);

	public RemoveConnectionTest() throws Exception {
		switchFactory = new OpticalSwitchFactory();

	}

	@Test
	public void connectionExistsTest() throws Exception {

		OpticalSwitchCardFactory factory;
		try {
			factory = new OpticalSwitchCardFactory();
		} catch (IOException e) {
			throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
		}

		OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
		ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

		int connectionsNum = proteus.getFiberConnections().size();

		IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

		FiberConnection connection = newRemoveConnectionParams(proteus);

		// make connection
		MakeConnectionAction makeConnectionAction = new MakeConnectionAction();
		makeConnectionAction.setParams(connection);
		makeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);

		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

		Assert.assertTrue(proteus.getFiberConnections().size() == connectionsNum + 1);

		// check connectionExisits works
		Assert.assertTrue(RemoveConnectionAction.connectionExists(connection, proteus));
	}

	@Test
	public void removeConnectionTest() {
		// WonesysProtocolSession = new WonesysProtocolSession();
		try {

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
			ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

			int initialConnectionsNum = proteus.getFiberConnections().size();
			log.info("Number of connections: " + initialConnectionsNum);

			IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

			FiberConnection connection = newRemoveConnectionParams(proteus);

			log.info("Making connection to remove...");

			// make connection
			MakeConnectionAction makeConnectionAction = new MakeConnectionAction();
			makeConnectionAction.setParams(connection);
			makeConnectionAction.setModelToUpdate(proteus);

			ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);

			Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

			int connectionsNum = proteus.getFiberConnections().size();
			log.info("Number of connections: " + connectionsNum);

			log.info("Removing previous connection ...");
			// remove previously made connection
			RemoveConnectionAction removeConnectionAction = new RemoveConnectionAction();
			removeConnectionAction.setParams(connection);
			removeConnectionAction.setModelToUpdate(proteus);

			actionResponse = removeConnectionAction.execute(protocolSessionManager);

			Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

			/* it is executed at least one action */
			Assert.assertTrue(actionResponse.getResponses().size() > 0);

			/* all responses are correct */
			for (Response response : actionResponse.getResponses()) {
				Assert.assertTrue(response.getErrors().size() == 0);
			}

			log.info("Number of connections: " + proteus.getFiberConnections().size());
			Assert.assertTrue(proteus.getFiberConnections().size() == connectionsNum - 1);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	private FiberConnection newRemoveConnectionParams(ProteusOpticalSwitch proteus) throws Exception {

		FiberConnection fiberConnection = new FiberConnection();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = (FCPort) dropCard.getPort(0);

		DWDMChannel srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) dropCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		// DWDMChannel srcFiberChannel = new DWDMChannel();
		// srcFiberChannel.setLambda(1528.58);

		fiberConnection.setSrcCard(dropCard);
		fiberConnection.setSrcPort(srcPort);
		fiberConnection.setSrcFiberChannel(srcFiberChannel);

		// PSROADM ADD card
		int addChasis = 0;
		int addSlot = 17;
		ProteusOpticalSwitchCard addCard = proteus.getCard(addChasis, addSlot);
		FCPort dstPort = ((WonesysPassiveAddCard) addCard).getCommonPort();

		DWDMChannel dstFiberChannel = (DWDMChannel) ((WDMChannelPlan) addCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		// DWDMChannel dstFiberChannel = new DWDMChannel();
		// dstFiberChannel.setLambda(1528.58);

		fiberConnection.setDstCard(addCard);
		fiberConnection.setDstPort(dstPort);
		fiberConnection.setDstFiberChannel(dstFiberChannel);

		return fiberConnection;
	}

}
