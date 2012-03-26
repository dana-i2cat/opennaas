package org.opennaas.extensions.roadm.wonesys.commandsets.test;

import java.util.List;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.GetInventoryCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.LockNodeCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.UnlockNodeCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannelInfo;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannelPlan;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannels;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.SetChannel;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSession;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolSessionFactory;
import org.opennaas.extensions.roadm.wonesys.transports.mock.ProteusMock;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class WonesysCommandsParseTest {

	Log				log				= LogFactory.getLog(WonesysCommandsParseTest.class);

	private String	resourceId		= "pedrosa";
	private String	hostIpAddress	= "10.10.80.11";
	private String	hostPort		= "27773";

	@Test
	public void testROADMCommandsCrafting() {
		try {

			log.info("testROADMCommandsCrafting ---------------------------------");

			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			String commandStr = (String) c.message();
			Assert.assertTrue(commandStr.contains("5910ffffffffff03ffffffff0000"));
			// TODO check XOR
			Assert.assertTrue(commandStr.endsWith("00"));

			// lockNode
			c = new UnlockNodeCommand();
			c.initialize();
			commandStr = (String) c.message();
			Assert.assertTrue(commandStr.contains("5910ffffffffff04ffffffff0000"));
			// TODO check XOR
			Assert.assertTrue(commandStr.endsWith("00"));

			// getInventory
			c = new GetInventoryCommand();
			c.initialize();
			commandStr = (String) c.message();
			Assert.assertTrue(commandStr.equals("5910ffffffffff01ffffffff0000b700"));

			String chassisStr = "00";
			String slotStr = "01";
			int chassis = 0;
			int slot = 1;

			// getChannelPlan
			c = new GetChannelPlan(chassis, slot);
			c.initialize();
			commandStr = (String) c.message();
			Assert.assertTrue(commandStr.contains("5910" + chassisStr + slotStr + "ffff0b03ffffffff0000"));
			// TODO check XOR
			Assert.assertTrue(commandStr.endsWith("00"));

			// getChannels
			c = new GetChannels(chassis, slot);
			c.initialize();
			commandStr = (String) c.message();
			Assert.assertTrue(commandStr.contains("5910" + chassisStr + slotStr + "ffff0b04ffffffff0000"));
			// TODO check XOR
			Assert.assertTrue(commandStr.endsWith("00"));

			// setChannel
			String channelStr = "0100";
			String portStr = "01";
			int channel = 1;
			int port = 1;
			c = new SetChannel(chassis, slot, channel, port);
			c.initialize();
			commandStr = (String) c.message();
			Assert.assertTrue(commandStr.contains("5910" + chassisStr + slotStr + "ffff0b02ffffffff0300" + channelStr + portStr));
			// TODO check XOR
			Assert.assertTrue(commandStr.endsWith("00"));
			log.info("-----------------------------------------------------------");

		} catch (CommandException e) {
			log.error("Error happened!!!", e);
			Assert.fail();
		}
	}

	@Test
	public void testROADMCommandsParsing() {

		try {

			ProteusMock proteus = new ProteusMock(null);

			ProteusOpticalSwitch opticalSwitch1 = new ProteusOpticalSwitch();
			opticalSwitch1.setName(resourceId);

			// lockNode
			WonesysCommand c = new LockNodeCommand();
			c.initialize();
			// Common workflow follows
			// String response = (String) session.sendReceive(c.message());
			// but this is a unit test
			String response = (String) proteus.execCommand((String) c.message());
			Response resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

			// getInventory
			c = new GetInventoryCommand();
			c.initialize();
			response = (String) proteus.execCommand((String) c.message());
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);
			Assert.assertTrue(opticalSwitch1.getLogicalDevices().size() > 0);

			// for each ROADM drop card
			ProteusOpticalSwitchCard dropCard = null;
			for (LogicalDevice dev : opticalSwitch1.getLogicalDevices()) {
				if (dev instanceof ProteusOpticalSwitchCard) {
					ProteusOpticalSwitchCard card = (ProteusOpticalSwitchCard) dev;
					if (card.getCardType().equals(CardType.ROADM_DROP)) {
						dropCard = card;

						int chassis = dropCard.getChasis();
						int slot = dropCard.getModuleNumber();

						Assert.assertTrue(WonesysCommand.toByteHexString(chassis, 1).length() == 2);
						Assert.assertTrue(WonesysCommand.toByteHexString(slot, 1).length() == 2);

						log.info("Testing GetChannelPlan....");
						// getChannelPlan
						c = new GetChannelPlan(chassis, slot);
						c.initialize();
						response = (String) proteus.execCommand((String) c.message());

						resp = c.checkResponse(response);
						c.parseResponse(resp, opticalSwitch1);

						Assert.assertTrue(dropCard.getChannelPlan().getFirstChannel() >= 0);
						Assert.assertTrue(dropCard.getChannelPlan().getLastChannel() > dropCard.getChannelPlan().getFirstChannel());
						Assert.assertTrue(dropCard.getChannelPlan().getChannelGap() > 0);
						Assert.assertFalse(dropCard.getChannelPlan().getAllChannels().isEmpty());

						for (NetworkPort port : dropCard.getModulePorts()) {
							Assert.assertTrue(port.getPortsOnDevice().size() == 0);
						}

						log.info("Testing GetChannels....");
						// getChannels
						c = new GetChannels(chassis, slot);
						c.initialize();
						response = (String) proteus.execCommand((String) c.message());
						resp = c.checkResponse(response);
						c.parseResponse(resp, opticalSwitch1);

						for (NetworkPort port : dropCard.getModulePorts()) {
							for (LogicalPort fcport : port.getPortsOnDevice()) {
								if (fcport instanceof FCPort) {
									Assert.assertTrue(((FCPort) fcport).getPortNumber() >= dropCard.getChannelPlan().getFirstChannel());
									Assert.assertTrue(((FCPort) fcport).getPortNumber() <= dropCard.getChannelPlan().getLastChannel());
									// TODO when topology is complete:
									// module ports should be connected between each others (other card module ports)
									// Assert.assertTrue(((FCPort) fcport).getOutgoingDeviceConnections().size() > 0);
								}
							}
						}

						log.info("Testing GetChannelInfo....");
						// getChannelInfo
						for (FiberChannel channel : dropCard.getChannelPlan().getAllChannels()) {
							log.debug("ChannelInfo for channel " + channel.getNumChannel());
							c = new GetChannelInfo(chassis, slot, channel.getNumChannel());
							c.initialize();
							response = (String) proteus.execCommand((String) c.message());
							resp = c.checkResponse(response);
							c.parseResponse(resp, opticalSwitch1);
						}

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

						// setChannel

						// check transformation to HexString is correct
						Assert.assertTrue(WonesysCommand.toByteHexString(dwdmChannel, 2).length() == 4);
						Assert.assertTrue(WonesysCommand.toByteHexString(setDstPort.getPortNumber(), 1).length() == 2);

						log.info("Testing SetChannel....");
						c = new SetChannel(chassis, slot, dwdmChannel, setDstPort.getPortNumber());
						c.initialize();
						response = (String) proteus.execCommand((String) c.message());
						resp = c.checkResponse(response);
						c.parseResponse(resp, opticalSwitch1);

						// CHECK MODEL HAS CHANGED CORRECTLY

						// check a subPort has been created on setSrcPort
						FCPort setSrcSubPort = null;
						for (LogicalDevice port : setSrcPort.getPortsOnDevice()) {
							if (port instanceof FCPort) {
								if (((FCPort) port).getPortNumber() == dwdmChannel) {
									setSrcSubPort = (FCPort) port;
									break;
								}
							}
						}
						Assert.assertNotNull(setSrcSubPort);

						// check a subPort has been created on setDstPort
						FCPort setDstSubPort = null;
						for (LogicalDevice port : setDstPort.getPortsOnDevice()) {
							if (port instanceof FCPort) {
								if (((FCPort) port).getPortNumber() == dwdmChannel) {
									setDstSubPort = (FCPort) port;
									break;
								}
							}
						}
						Assert.assertNotNull(setDstSubPort);

						// check both subports are connected
						Assert.assertTrue(setSrcSubPort.getOutgoingDeviceConnections().contains(setDstSubPort));

						// check passthrough has been created
						FCPort passthroughInParentPort = null;
						WDMFCPort passthroughInSubPort = null;
						for (LogicalDevice connectedPort : setDstPort.getOutgoingDeviceConnections()) {
							if (connectedPort instanceof FCPort) {
								passthroughInParentPort = (FCPort) connectedPort;
								for (LogicalDevice port : connectedPort.getPortsOnDevice()) {
									if (port instanceof WDMFCPort) {
										if (((WDMFCPort) port).getDWDMChannel().equals(selectedChannel)) {
											passthroughInSubPort = (WDMFCPort) port;
											break;
										}
									}
								}
								if (passthroughInSubPort != null)
									break;
							}
						}
						Assert.assertNotNull(passthroughInParentPort);
						Assert.assertNotNull(passthroughInSubPort);

						// check there's a connection linking different cards subports
						Assert.assertTrue(setDstSubPort.getOutgoingDeviceConnections().contains(passthroughInSubPort));
						Assert.assertTrue(dropCard.getFreeChannels(setSrcPort).size() == freeChannelsSize - 1);

						// check passthrough dstCard subports are connected
						WDMFCPort passthroughOutSubPort = null;
						for (LogicalDevice dev1 : passthroughInSubPort.getOutgoingDeviceConnections()) {
							if (dev1 instanceof WDMFCPort) {
								if (((WDMFCPort) dev1).getDWDMChannel().equals(passthroughInSubPort.getDWDMChannel())) {
									passthroughOutSubPort = (WDMFCPort) dev1;
									break;
								}
							}
						}
						Assert.assertNotNull(passthroughOutSubPort);

						FCPort passthroughOutParentPort = null;
						passthroughOutParentPort = (FCPort) passthroughOutSubPort.getDevices().get(0);
						Assert.assertNotNull(passthroughOutParentPort);

						log.info("Testing SetChannel in port 0....");
						// setChannel as before
						c = new SetChannel(chassis, slot, dwdmChannel, 0);
						c.initialize();
						response = (String) proteus.execCommand((String) c.message());
						resp = c.checkResponse(response);
						c.parseResponse(resp, opticalSwitch1);

						// check model has changed

						Assert.assertTrue(dropCard.getFreeChannels(setSrcPort).size() == freeChannelsSize);
						Assert.assertFalse(setSrcPort.getPortsOnDevice().contains(setSrcSubPort));
						Assert.assertFalse(setDstPort.getPortsOnDevice().contains(setDstSubPort));
						Assert.assertFalse(passthroughInParentPort.getPortsOnDevice().contains(passthroughInSubPort));
						Assert.assertFalse(passthroughOutParentPort.getPortsOnDevice().contains(passthroughOutSubPort));

					}
				}
			}

			// unlockNode
			c = new UnlockNodeCommand();
			c.initialize();
			response = (String) proteus.execCommand((String) c.message());
			resp = c.checkResponse(response);
			c.parseResponse(resp, opticalSwitch1);

		} catch (CommandException e) {
			log.error("Error happened!!!!", e);
			try {
				// executeUnlock();
			} catch (Exception e1) {
				log.error("Error executing unlock !!!!", e1);
			}
			Assert.fail();
		}

		log.info("---------------------------------------------------------------");
	}

	private void executeUnlock() throws CommandException, ProtocolException {

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

	private IProtocolSession getMockSession(String resourceId, String hostIpAddress, String hostPort) throws ProtocolException {

		ProtocolSessionContext sessionContext = createWonesysProtocolSessionContextMock(hostIpAddress, hostPort);

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
		return protocolSessionContext;
	}

	private ProtocolSessionContext createWonesysProtocolSessionContextMock(String ip,
			String port) {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"wonesys");
		protocolSessionContext.addParameter("protocol.mock",
				"true");
		return protocolSessionContext;
	}

	private IProtocolSession getProtocolSession(String resourceId, ProtocolSessionContext sessionContext) throws ProtocolException {
		WonesysProtocolSessionFactory factory = new WonesysProtocolSessionFactory();
		IProtocolSession protocolSession = factory.createProtocolSession(resourceId + "01", sessionContext);
		protocolSession.connect();
		return protocolSession;

	}

}
