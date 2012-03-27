package org.opennaas.extensions.router.model.tests;

import java.io.IOException;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchCardFactory;
import org.opennaas.extensions.router.model.utils.OpticalSwitchFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class OpticalSwitchCardLoadingTest {

	Log	logger	= LogFactory.getLog(OpticalSwitchCardLoadingTest.class);

	@Test
	public void loadCardsTest() {

		try {

			logger.debug("LoadCardsTest...");

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			ProteusOpticalSwitch proteus = new ProteusOpticalSwitch();

			// PSROADM DROP card
			int chasis = 0;
			int slot = 1;
			int type = 11;
			int subtype = 1;

			ProteusOpticalSwitchCard card = factory.createCard(chasis, slot, type, subtype);
			proteus.addLogicalDevice(card);

			Assert.assertTrue(card.getChasis() == chasis);
			Assert.assertTrue(card.getSlot() == slot);
			Assert.assertTrue(card.getSlot() == card.getModuleNumber());
			Assert.assertTrue(card.getType() == type);
			Assert.assertTrue(card.getSubtype() == subtype);
			Assert.assertTrue(card.getCardType().equals(CardType.ROADM_DROP));
			Assert.assertTrue(card.getChannelPlan() != null);
			Assert.assertTrue(card.getChannelPlan().getChannelType().equals(FiberChannelPlan.ChannelType.WDM));

			WonesysDropCard dropCard = (WonesysDropCard) card;
			Assert.assertTrue(dropCard.getCommonPort() != null);
			Assert.assertTrue(dropCard.getExpressPort() != null);
			Assert.assertTrue(dropCard.getPorts().contains(dropCard.getCommonPort()));
			Assert.assertTrue(dropCard.getPorts().contains(dropCard.getExpressPort()));

			Assert.assertTrue(dropCard.getPorts().size() > 0);
			// there is at least one port that is internally connected to at least one port
			// a switch that cannot link any of its ports is no longer a switch
			boolean connectionFound = false;
			for (NetworkPort port : dropCard.getPorts()) {
				if (dropCard.getInternalConnections(port).size() > 0) {
					connectionFound = true;
					break;
				}
			}
			Assert.assertTrue(connectionFound);

			logger.debug("Tested");

		} catch (Exception e) {
			logger.error("Error ocurred!!!", e);
			Assert.fail();
		}

	}

	@Test
	public void loadTopologyTest() {

		try {

			logger.debug("LoadTopologyTest...");

			OpticalSwitchCardFactory factory;
			try {
				factory = new OpticalSwitchCardFactory();
			} catch (IOException e) {
				throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
			}

			ProteusOpticalSwitch proteus = new ProteusOpticalSwitch();
			proteus.setName("pedrosa");

			// PSROADM DROP card
			int chasis = 0;
			int slot = 1;
			int type = 11;
			int subtype = 1;

			ProteusOpticalSwitchCard card = factory.createCard(chasis, slot, type, subtype);
			proteus.addLogicalDevice(card);

			// no connections
			for (NetworkPort port : card.getPorts()) {
				Assert.assertTrue(port.getOutgoingDeviceConnections().isEmpty());
			}

			// PSROADM ADD card
			chasis = 0;
			slot = 17;
			type = 11;
			subtype = 32;

			card = factory.createCard(chasis, slot, type, subtype);
			proteus.addLogicalDevice(card);

			// no connections
			for (NetworkPort port : card.getPorts()) {
				Assert.assertTrue(port.getOutgoingDeviceConnections().isEmpty());
			}

			// add connections
			factory.addHardcodedCardConnections(proteus);

			// assure there is at least one connection
			boolean connectionFound = false;
			for (LogicalDevice dev : proteus.getLogicalDevices()) {
				if (dev instanceof ProteusOpticalSwitchCard) {
					for (NetworkPort port : ((ProteusOpticalSwitchCard) dev).getPorts()) {
						if (!port.getOutgoingDeviceConnections().isEmpty()) {
							// assure its not connected to itself
							for (LogicalDevice dst : port.getOutgoingDeviceConnections()) {
								Assert.assertTrue(port != dst);
							}
							connectionFound = true;
							break;
						}
					}
				}
			}
			Assert.assertTrue(connectionFound);

			logger.debug("Tested");

		} catch (Exception e) {
			logger.error("Error ocurred!!!", e);
			Assert.fail();
		}

	}

	@Test
	public void PassConnectionsTest() throws Exception {
		logger.debug("PassThroughTest...");

		OpticalSwitchCardFactory factory;
		try {
			factory = new OpticalSwitchCardFactory();
		} catch (IOException e) {
			throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
		}
		OpticalSwitchFactory switchFactory = new OpticalSwitchFactory();

		ProteusOpticalSwitch proteus = switchFactory.newPedrosaProteusOpticalSwitch();

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		ProteusOpticalSwitchCard dropCard = proteus.getCard(dropChasis, dropSlot);
		FCPort srcPort = ((WonesysDropCard) dropCard).getCommonPort();
		/* check common port */
		Assert.assertTrue(srcPort.getPortNumber() == 0);

		/* check internal connections */

		for (NetworkPort internalConnectedPort : dropCard.getInternalConnections(srcPort)) {
			;
			boolean isFound = false;
			int listInternalConnections[] = { 1, 2, 3, 128 };
			for (int numToConnect : listInternalConnections) {
				if (internalConnectedPort.getPortNumber() == numToConnect)
					isFound = true;
			}
			Assert.assertTrue(isFound);
		}

		/* check external connections, in this case we use only 1 */
		FCPort connectedPort = (FCPort) dropCard.getPort(128);
		for (LogicalDevice logicalDevice : connectedPort.getOutgoingDeviceConnections()) {
			FCPort toConnectedPort = (FCPort) logicalDevice;
			int outDevice = 4;
			if (toConnectedPort.getPortNumber() != outDevice)
				Assert.fail("It doesn't contain an external connection");
		}

		// PSROADM ADD card
		int addChasis = 0;
		int addSlot = 17;
		ProteusOpticalSwitchCard addCard = proteus.getCard(addChasis, addSlot);
		FCPort dstPort = ((WonesysPassiveAddCard) addCard).getCommonPort();

		/* check common port */
		Assert.assertTrue(dstPort.getPortNumber() == 129);

		/* check internal connections */
		for (NetworkPort port : addCard.getPorts()) {
			if (port.getPortNumber() == 129)
				continue; /* it is the common port, we don't want to test it */
			for (LogicalDevice logicalDevice : addCard.getInternalConnections(port)) {
				FCPort toConnectedPort = (FCPort) logicalDevice;
				if (toConnectedPort.getPortNumber() != 129)
					Assert.fail("It doesn't contain an internal connection");
			}

		}

		/* check external connections, in this case we use only 1 */
		connectedPort = (FCPort) addCard.getPort(4);
		for (LogicalDevice logicalDevice : connectedPort.getIncomingDeviceConnections()) {
			FCPort toConnectedPort = (FCPort) logicalDevice;
			int outDevice = 128;
			if (toConnectedPort.getPortNumber() != outDevice)
				Assert.fail("It doesn't contain an external connection");

		}

	}

}
