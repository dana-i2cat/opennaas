package net.i2cat.luminis.actions.tests;

import java.util.HashMap;
import java.util.List;
import java.io.File;

import net.i2cat.luminis.actionsets.wonesys.actions.MakeConnectionAction;
import net.i2cat.luminis.actionsets.wonesys.actions.RefreshModelConnectionsAction;
import net.i2cat.luminis.actionsets.wonesys.actions.RemoveConnectionAction;
import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSessionFactory;

import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberConnection;
import net.i2cat.mantychore.model.opticalSwitch.WDMChannelPlan;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.WDMFCPort;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.opennaas.core.protocols.sessionmanager.impl.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class ConnectionActionsIntegrationTest {

	Log log	= LogFactory.getLog(ConnectionActionsIntegrationTest.class);

	private String	resourceId	= "pedrosa";

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
					   noConsole(),
					   keepRuntimeFolder());
	}

	@Test
	public void testRefreshMakeAndRemoveConnectionsWithMock() {

		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);

		log.info("Testing actions with mock connection");
		IProtocolSessionManager sessionManager = getMockProtocolSessionManager();

		try {
			execActionsAndChecks(opticalSwitch1, sessionManager);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	// @Test //uses real connection
	public void testRefreshMakeAndRemoveConnectionsReal() {

		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);

		log.info("Testing actions with real connection");
		IProtocolSessionManager sessionManager = null;
		try {
			sessionManager = getProtocolSessionManager();

			execActionsAndChecks(opticalSwitch1, sessionManager);

		} catch (ProtocolException e1) {
			e1.printStackTrace();
			Assert.fail();
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void execActionsAndChecks(ProteusOpticalSwitch opticalSwitch1, IProtocolSessionManager sessionManager) throws ActionException {

		log.info("Refresh...");

		// refresh
		IAction action = new RefreshModelConnectionsAction();
		action.setModelToUpdate(opticalSwitch1);

		ActionResponse response = action.execute(sessionManager);
		Assert.assertTrue(response.getStatus().equals(STATUS.OK));

		checkModelIsRefreshed((ProteusOpticalSwitch) action.getModelToUpdate());

		int initialConnectionsNum = opticalSwitch1.getFiberConnections().size();

		FiberConnection connection = newConnectionRequest(opticalSwitch1);

		log.info("MakeConnection...");

		// makeConnection
		action = new MakeConnectionAction();
		action.setModelToUpdate(opticalSwitch1);

		action.setParams(connection);

		response = action.execute(sessionManager);
		if (!response.getStatus().equals(STATUS.OK)) {
			Assert.fail(response.getInformation());
		}
		Assert.assertTrue(response.getStatus().equals(STATUS.OK));

		checkConnectionAddedToModel(connection, opticalSwitch1);

		Assert.assertTrue(opticalSwitch1.getFiberConnections().size() == initialConnectionsNum + 1);

		log.info("RemoveConnection...");

		// removeConnection
		action = new RemoveConnectionAction();
		action.setModelToUpdate(opticalSwitch1);
		action.setParams(connection);

		response = action.execute(sessionManager);
		if (!response.getStatus().equals(STATUS.OK)) {
			Assert.fail(response.getInformation());
		}
		Assert.assertTrue(response.getStatus().equals(STATUS.OK));

		checkConnectionRemovedFromModel(connection, opticalSwitch1);

		Assert.assertTrue(opticalSwitch1.getFiberConnections().size() == initialConnectionsNum);

	}

	private void checkConnectionRemovedFromModel(FiberConnection connection, ProteusOpticalSwitch opticalSwitch1) {

		Assert.assertFalse(RemoveConnectionAction.connectionExists(connection, opticalSwitch1));

		// TODO check subports have been removed
	}

	private void checkModelIsRefreshed(ProteusOpticalSwitch opticalSwitch) {
		Assert.assertTrue(opticalSwitch.getLogicalDevices().size() > 0);

		ProteusOpticalSwitchCard dropCard = null;
		for (LogicalDevice dev : opticalSwitch.getLogicalDevices()) {
			if (dev instanceof ProteusOpticalSwitchCard) {
				ProteusOpticalSwitchCard card = (ProteusOpticalSwitchCard) dev;

				Assert.assertNotNull(card.getChannelPlan());
				Assert.assertTrue(card.getChannelPlan() instanceof WDMChannelPlan);

				if (card.getCardType().equals(CardType.ROADM_DROP)) {
					dropCard = card;

					int chassis = dropCard.getChasis();
					int slot = dropCard.getModuleNumber();

					Assert.assertTrue(WonesysCommand.toByteHexString(chassis, 1).length() == 2);
					Assert.assertTrue(WonesysCommand.toByteHexString(slot, 1).length() == 2);

					Assert.assertTrue(dropCard.getChannelPlan().getFirstChannel() >= 0);
					Assert.assertTrue(dropCard.getChannelPlan().getLastChannel() > dropCard.getChannelPlan().getFirstChannel());
					Assert.assertTrue(dropCard.getChannelPlan().getChannelGap() > 0);
					Assert.assertFalse(dropCard.getChannelPlan().getAllChannels().isEmpty());

					boolean internalConnectionsDetected = false;
					for (NetworkPort port : dropCard.getModulePorts()) {

						for (LogicalPort fcport : port.getPortsOnDevice()) {
							if (fcport instanceof FCPort) {
								Assert.assertTrue(((FCPort) fcport).getPortNumber() >= dropCard.getChannelPlan().getFirstChannel());
								Assert.assertTrue(((FCPort) fcport).getPortNumber() <= dropCard.getChannelPlan().getLastChannel());
							}
						}
						// when topology is complete:
						// module ports should be connected between each others (same card ports)
						internalConnectionsDetected = internalConnectionsDetected || !dropCard.getInternalConnections((FCPort) port)
								.isEmpty();
					}
					Assert.assertTrue(internalConnectionsDetected);

					// select SetChannel srcPort
					FCPort setSrcPort = ((WonesysDropCard) dropCard).getCommonPort();

					// select SetChannel dwdmChannel
					List<FiberChannel> freeChannels = dropCard.getFreeChannels(setSrcPort);
					int freeChannelsSize = freeChannels.size();
					DWDMChannel selectedChannel = (DWDMChannel) freeChannels.get(0);
					int dwdmChannel = selectedChannel.getChannelNumber();

					// select setChannel dstPort
					NetworkPort setDstPort = null;
					List<NetworkPort> reachablePorts = dropCard.getInternalConnections(setSrcPort);
					// selecting express port will create a passthrough connection
					if (reachablePorts.contains(((WonesysDropCard) dropCard).getExpressPort()))
						setDstPort = ((WonesysDropCard) dropCard).getExpressPort();
					Assert.assertNotNull(setDstPort);

				}
			}
		}

		for (FiberConnection connection : opticalSwitch.getFiberConnections()) {
			ProteusOpticalSwitchCard modelSrcCard = opticalSwitch.getCard(connection.getSrcCard().getChasis(), connection.getSrcCard().getSlot());
			ProteusOpticalSwitchCard modelDstCard = opticalSwitch.getCard(connection.getDstCard().getChasis(), connection.getDstCard().getSlot());
			Assert.assertTrue(connection.getSrcCard().equals(modelSrcCard));
			Assert.assertTrue(connection.getDstCard().equals(modelDstCard));

			Assert.assertTrue(connection.getSrcPort().equals(modelSrcCard.getPort(connection.getSrcPort().getPortNumber())));
			Assert.assertTrue(connection.getDstPort().equals(modelDstCard.getPort(connection.getDstPort().getPortNumber())));

			Assert.assertTrue(connection.getSrcFiberChannel().equals(
					((WDMChannelPlan) modelSrcCard.getChannelPlan()).getChannel(connection.getSrcFiberChannel().getNumChannel())));
			Assert.assertTrue(connection.getDstFiberChannel().equals(
					((WDMChannelPlan) modelDstCard.getChannelPlan()).getChannel(connection.getDstFiberChannel().getNumChannel())));

			// subPorts have been created
			WDMFCPort srcSubPort = null;
			for (LogicalPort subPort : connection.getSrcPort().getPortsOnDevice()) {
				if (subPort instanceof WDMFCPort) {
					if (((WDMFCPort) subPort).getDWDMChannel().getLambda() == connection.getSrcFiberChannel().getLambda()) {
						srcSubPort = (WDMFCPort) subPort;
						break;
					}
				}
			}
			Assert.assertNotNull(srcSubPort);
			Assert.assertFalse(srcSubPort.getOutgoingDeviceConnections().isEmpty());

			WDMFCPort dstSubPort = null;
			for (LogicalPort subPort : connection.getDstPort().getPortsOnDevice()) {
				if (subPort instanceof WDMFCPort) {
					if (((WDMFCPort) subPort).getDWDMChannel().getLambda() == connection.getDstFiberChannel().getLambda()) {
						dstSubPort = (WDMFCPort) subPort;
						break;
					}
				}
			}
			Assert.assertNotNull(dstSubPort);
			Assert.assertFalse(dstSubPort.getIncomingDeviceConnections().isEmpty());

			// there is a route from srcSubPort to dstSubPort
			WDMFCPort subPort = srcSubPort;
			int hopCounter = 0;
			while (!subPort.equals(dstSubPort) && hopCounter < 10) {
				subPort = (WDMFCPort) subPort.getOutgoingDeviceConnections().get(0);
				hopCounter++;
			}
			Assert.assertTrue(subPort.equals(dstSubPort));

		}

	}

	private void checkConnectionAddedToModel(FiberConnection request, ProteusOpticalSwitch proteus) {

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

	private FiberConnection newConnectionRequest(ProteusOpticalSwitch proteus) {

		FiberConnection fiberConnection = new FiberConnection();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = (FCPort) dropCard.getPort(0);

		DWDMChannel srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) dropCard.getChannelPlan()).getChannel(
				dropCard.getChannelPlan().getFirstChannel());

		double srcLambda = srcFiberChannel.getLambda();

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
				((WDMChannelPlan) addCard.getChannelPlan()).getChannelNumberFromLambda(srcLambda));

		// DWDMChannel dstFiberChannel = new DWDMChannel();
		// dstFiberChannel.setLambda(1528.58);

		fiberConnection.setDstCard(addCard);
		fiberConnection.setDstPort(dstPort);
		fiberConnection.setDstFiberChannel(dstFiberChannel);

		return fiberConnection;
	}

	public IProtocolSessionManager getMockProtocolSessionManager() {
		IProtocolSessionManager protocolSessionManager = new MockProtocolSessionManager();
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
