package org.opennaas.extensions.roadm.wonesys.commandsets.test;

import java.util.HashMap;
import java.util.List;

import org.opennaas.extensions.roadm.wonesys.actionsets.actions.RefreshModelConnectionsAction;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.test.mock.MockProtocolSessionManager;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.protocols.sessionmanager.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class RefreshModelConnectionsActionTest {
	Log												log			= LogFactory.getLog(RefreshModelConnectionsActionTest.class);

	WonesysProtocolSession							session;
	// //
	private static RefreshModelConnectionsAction	action;
	private String									resourceId	= "pedrosa";

	// static ActionTestHelper helper;
	// static ProtocolSessionManager protocolsessionmanager;

	@Before
	public void init() {
		action = new RefreshModelConnectionsAction();
		ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
		opticalSwitch1.setName(resourceId);
		action.setModelToUpdate(opticalSwitch1);
		log.info("init test");
	}

	@Test
	public void testRefreshModelMockExecute() {
		log.info("Testing Action with mock connection");

		try {
			ActionResponse response = action.execute(getMockProtocolSessionManager());

			Assert.assertTrue(response.getStatus().equals(STATUS.OK));

			for (Response resp : response.getResponses()) {

				Assert.assertTrue(resp.getStatus().equals(Status.OK));
			}

			ProteusOpticalSwitch opticalSwitch = (ProteusOpticalSwitch) action.getModelToUpdate();
			checkModelIsRefreshed(opticalSwitch);
		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	// @Test // uses real connection
	public void testRefreshModelExecute() {
		log.info("Testing Action with real connection");

		try {
			ActionResponse response = action.execute(getProtocolSessionManager());

			Assert.assertTrue(response.getStatus().equals(STATUS.OK));

			for (Response resp : response.getResponses()) {

				Assert.assertTrue(resp.getStatus().equals(Status.OK));
			}

			ProteusOpticalSwitch opticalSwitch = (ProteusOpticalSwitch) action.getModelToUpdate();
			checkModelIsRefreshed(opticalSwitch);

		} catch (ActionException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	public void checkModelIsRefreshed(ProteusOpticalSwitch opticalSwitch) {
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
