package org.opennaas.itests.roadm.connections;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.MakeConnectionAction;
import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RemoveConnectionAction;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchCardFactory;
import org.opennaas.extensions.router.model.utils.OpticalSwitchFactory;
import org.opennaas.itests.roadm.mock.MockProtocolSessionManager;

public class MakeConnectionTest {

	Log	log	= LogFactory.getLog(MakeConnectionTest.class);

	@Test
	public void makeConnectionWithMockTest() {
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

			IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

			makeConnectionAndCheck(proteus, protocolSessionManager);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	// @Test //uses real connection
	public void makeConnectionTest() {
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

			IProtocolSessionManager protocolSessionManager = getProtocolSessionManager();

			makeConnectionAndCheck(proteus, protocolSessionManager);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	public void makeConnectionAndCheck(ProteusOpticalSwitch proteus, IProtocolSessionManager protocolSessionManager) throws Exception {

		int connectionsNum = proteus.getFiberConnections().size();

		log.info("Number of connections: " + connectionsNum);

		log.info("Making connection...");
		MakeConnectionAction makeConnectionAction = new MakeConnectionAction();

		FiberConnection connection = newMakeConnectionParams(proteus);
		makeConnectionAction.setParams(connection);
		makeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);

		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));

		/* it is executed at least one action */
		Assert.assertTrue(actionResponse.getResponses().size() > 0);

		/* all responses are correct */
		for (Response response : actionResponse.getResponses()) {
			Assert.assertTrue(response.getErrors().size() == 0);
		}

		checkModelIsUpdated(connection, proteus);

		log.info("Number of connections: " + proteus.getFiberConnections().size());
		Assert.assertTrue(proteus.getFiberConnections().size() == connectionsNum + 1);

		// justo to keep the state
		removeConnection(proteus, protocolSessionManager, connection);

	}

	@Test
	public void makeRepeatedConnectionWithMockTest() {

		try {

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
			ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

			IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();

			makeRepeatedConnection(proteus, protocolSessionManager);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	// @Test //uses real connection
	public void makeRepeatedConnectionTest() {

		try {

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();
			ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

			IProtocolSessionManager protocolSessionManager = getProtocolSessionManager();

			makeRepeatedConnection(proteus, protocolSessionManager);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	public void makeRepeatedConnection(ProteusOpticalSwitch proteus, IProtocolSessionManager protocolSessionManager) throws Exception {

		int connectionsNum = proteus.getFiberConnections().size();
		log.info("Number of connections: " + connectionsNum);

		MakeConnectionAction makeConnectionAction = new MakeConnectionAction();

		FiberConnection connection = newMakeConnectionParams(proteus);
		makeConnectionAction.setParams(connection);
		makeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = makeConnectionAction.execute(protocolSessionManager);
		/* it is executed only one action */
		Assert.assertTrue(actionResponse.getResponses().size() > 0);

		/* all responses are correct */
		for (Response response : actionResponse.getResponses()) {
			Assert.assertTrue(response.getErrors().size() == 0);
		}

		int connectionsNum1 = proteus.getFiberConnections().size();
		log.info("Number of connections: " + connectionsNum1);

		Assert.assertTrue(connectionsNum1 == connectionsNum + 1);

		// try to make the same connection again

		MakeConnectionAction makeRepeatedConnectionAction = new MakeConnectionAction();
		makeRepeatedConnectionAction.setParams(connection);
		makeRepeatedConnectionAction.setModelToUpdate(proteus);

		actionResponse = makeRepeatedConnectionAction.execute(protocolSessionManager);
		Assert.assertNotNull(actionResponse);
		// expect error
		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.ERROR));

		int connectionsNum2 = proteus.getFiberConnections().size();
		log.info("Number of connections: " + connectionsNum2);

		Assert.assertTrue(connectionsNum2 == connectionsNum1);

		// just to keep state
		removeConnection(proteus, protocolSessionManager, connection);

	}

	private void checkModelIsUpdated(FiberConnection request, ProteusOpticalSwitch proteus) {

		ProteusOpticalSwitchCard srcCard = proteus.getCard(request.getSrcCard().getChasis(), request.getSrcCard().getSlot());
		FCPort srcPort = (FCPort) srcCard.getPort(request.getSrcPort().getPortNumber());
		DWDMChannel srcChannel = (DWDMChannel) ((WDMChannelPlan) srcCard.getChannelPlan()).getChannel(((WDMChannelPlan) srcCard.getChannelPlan())
				.getChannelNumberFromLambda(request.getSrcFiberChannel().getLambda()));

		// check there is a subport of srcPort using desired channel
		WDMFCPort srcSubPort = (WDMFCPort) srcCard.getSubPort(srcPort, srcChannel);
		Assert.assertNotNull(srcSubPort);
		Assert.assertTrue(srcSubPort.getOutgoingDeviceConnections().size() > 0);

		ProteusOpticalSwitchCard dstCard = proteus.getCard(request.getDstCard().getChasis(), request.getDstCard().getSlot());
		FCPort dstPort = (FCPort) dstCard.getPort(request.getDstPort().getPortNumber());
		DWDMChannel dstChannel = (DWDMChannel) ((WDMChannelPlan) dstCard.getChannelPlan()).getChannel(((WDMChannelPlan) dstCard.getChannelPlan())
				.getChannelNumberFromLambda(request.getDstFiberChannel().getLambda()));

		// check there is a subport of dstPort using desired channel
		WDMFCPort dstSubPort = (WDMFCPort) dstCard.getSubPort(dstPort, dstChannel);
		Assert.assertNotNull(dstSubPort);
		Assert.assertTrue(dstSubPort.getIncomingDeviceConnections().size() > 0);

		// check there is a deviceConnection chain between srcSupPort and dstSubPort
		WDMFCPort subPort1 = srcSubPort;
		int maxPortsInvolved = 4;
		int portHops = 0;
		// TODO CHECK WHILE CONDITION, its not checking ports are the same!!!
		while (((FCPort) subPort1.getDevices().get(0)).getPortNumber() != dstPort.getPortNumber()
				&& portHops < maxPortsInvolved) {

			// there is a port connected to subPort1 using same lambda
			WDMFCPort tmpSubPort = null;
			for (LogicalDevice port : subPort1.getOutgoingDeviceConnections()) {
				if (port instanceof WDMFCPort) {
					if (((WDMFCPort) port).getDWDMChannel().getLambda() == subPort1.getDWDMChannel().getLambda()) {
						tmpSubPort = (WDMFCPort) port;
						break;
					}
				}
			}
			Assert.assertNotNull(tmpSubPort);
			subPort1 = tmpSubPort;
			portHops++;
		}
		Assert.assertTrue(((FCPort) subPort1.getDevices().get(0)).getPortNumber() == dstPort.getPortNumber());

		// check there is a FiberConnection matching request
		boolean found = false;
		for (FiberConnection existentConnection : proteus.getFiberConnections()) {

			// match src
			if (existentConnection.getSrcCard().getChasis() == request.getSrcCard().getChasis() &&
					existentConnection.getSrcCard().getSlot() == request.getSrcCard().getSlot()) {
				if (existentConnection.getSrcFiberChannel().getLambda() == request.getSrcFiberChannel().getLambda()) {

					// match dst
					if (existentConnection.getDstCard().getChasis() == request.getDstCard().getChasis() &&
							existentConnection.getDstCard().getSlot() == request.getDstCard().getSlot()) {
						if (existentConnection.getDstFiberChannel().getLambda() == request.getDstFiberChannel().getLambda()) {
							found = true;
							break;
						}
					}

				}
			}
		}
		Assert.assertTrue(found);

	}

	private FiberConnection newMakeConnectionParams(ProteusOpticalSwitch proteus) throws Exception {

		FiberConnection fiberConnection = new FiberConnection();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = (FCPort) dropCard.getPort(0);

		DWDMChannel srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) dropCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		double srcLambda = srcFiberChannel.getLambda();

		fiberConnection.setSrcCard(dropCard);
		fiberConnection.setSrcPort(srcPort);
		fiberConnection.setSrcFiberChannel(srcFiberChannel);

		// PSROADM ADD card
		int addChasis = 0;
		int addSlot = 17;
		ProteusOpticalSwitchCard addCard = proteus.getCard(addChasis, addSlot);
		FCPort dstPort = ((WonesysPassiveAddCard) addCard).getCommonPort();

		DWDMChannel dstFiberChannel = (DWDMChannel) ((WDMChannelPlan) addCard.getChannelPlan()).getChannel(
				((WDMChannelPlan) addCard.getChannelPlan()).getChannelNumberFromLambda(srcLambda));

		fiberConnection.setDstCard(addCard);
		fiberConnection.setDstPort(dstPort);
		fiberConnection.setDstFiberChannel(dstFiberChannel);

		return fiberConnection;
	}

	private void removeConnection(ProteusOpticalSwitch proteus, IProtocolSessionManager protocolSessionManager, FiberConnection connection)
			throws ActionException {

		log.info("Removing previous connection ...");
		// remove previously made connection
		RemoveConnectionAction removeConnectionAction = new RemoveConnectionAction();
		removeConnectionAction.setParams(connection);
		removeConnectionAction.setModelToUpdate(proteus);

		ActionResponse actionResponse = removeConnectionAction.execute(protocolSessionManager);

		Assert.assertTrue(actionResponse.getStatus().equals(STATUS.OK));
	}

	private ProtocolSessionManager getProtocolSessionManager() throws ProtocolException {

		ProtocolManager protocolManager = new ProtocolManager();
		ProtocolSessionManager protocolSessionManager = null;

		protocolSessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager("pedrosa");
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
