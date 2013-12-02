package org.opennaas.extensions.router.model.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysPassiveAddCard;

public class OpticalSwitchCardFactory {

	Log								logger	= LogFactory.getLog(OpticalSwitchCardFactory.class);

	OpticalSwitchCardProfileLoader	cardProfiles;
	OpticalSwitchTopologyLoader		topologyLoader;

	public OpticalSwitchCardFactory() throws IOException {
		cardProfiles = new OpticalSwitchCardProfileLoader();
		topologyLoader = new OpticalSwitchTopologyLoader();
	}

	public OpticalSwitchCardFactory(String cardProfilesFilePath, String topologyFilePath) throws IOException {
		cardProfiles = new OpticalSwitchCardProfileLoader(cardProfilesFilePath);
		topologyLoader = new OpticalSwitchTopologyLoader(topologyFilePath);
	}

	public ProteusOpticalSwitchCard createCard(int chasis, int slot, int type, int subtype) {

		String cardTypeStr = cardProfiles.getValue(type, subtype, "cardType");

		CardType cardType = null;
		try {
			cardType = CardType.valueOf(cardTypeStr);
		} catch (IllegalArgumentException e) {
			// Unrecognized card type
			// FIXME throw dedicated exception
			throw e;
		}

		ProteusOpticalSwitchCard card;
		// TODO find a way to add other types automatically
		if (cardType.equals(CardType.ROADM_DROP)) {
			card = new WonesysDropCard(chasis, slot, type, subtype);
		} else if (cardType.equals(CardType.ROADM_ADD)) {
			card = new WonesysPassiveAddCard(chasis, slot, type, subtype);
		} else {
			// FIXME add all current possibilities
			// card = new WonesysTransceiverCard(chasis, slot, type, subtype);
			// card = new WonesysTransponderCard(chasis, slot, type, subtype);
			card = new ProteusOpticalSwitchCard(chasis, slot, type, subtype);
		}

		// card.setName(cardProfiles.getValue(type, subtype, "name"));
		card.setCardType(cardType);
		card.setAllowsProtection(Boolean.parseBoolean(cardProfiles.getValue(type, subtype, "allowsProtection")));

		// all ProteusCards managing channels use WDM channels by default
		WDMChannelPlan channelPlan = new WDMChannelPlan();
		// channelPlan.setChannelType(FiberChannelPlan.ChannelType.valueOf(cardProfiles.getValue(type, subtype, "channelType",
		// ChannelType.WDM.toString())));
		card.setChannelPlan(channelPlan);

		NetworkPort cardPort;
		String[] cardPortNums = cardProfiles.getPortsParameter(type, subtype, "portNumber");
		String[] cardPortTypes = cardProfiles.getPortsParameter(type, subtype, "portType");
		String[] cardPortSpecial = cardProfiles.getPortsParameter(type, subtype, "special");
		List<List<String>> connections = new ArrayList<List<String>>();
		for (int i = 0; i < cardPortNums.length; i++) {
			if (cardPortTypes[i].equals("optical")) {
				cardPort = new FCPort();
			} else {
				cardPort = new NetworkPort();
			}
			connections.add(cardProfiles.getPortInternalConnections(type, subtype, cardPortNums[i]));
			cardPort.setPortNumber(Integer.parseInt(cardPortNums[i]));
			card.addModulePort(cardPort);

			// set special ports
			if (card instanceof WonesysDropCard) {
				if (cardPortSpecial[i].equals("common")) {
					((WonesysDropCard) card).setCommonPort((FCPort) cardPort);
				} else if (cardPortSpecial[i].equals("express")) {
					((WonesysDropCard) card).setExpressPort((FCPort) cardPort);
				}
			} else if (card instanceof WonesysPassiveAddCard) {
				if (cardPortSpecial[i].equals("common")) {
					((WonesysPassiveAddCard) card).setCommonPort((FCPort) cardPort);
				} else if (cardPortSpecial[i].equals("express")) {
					((WonesysPassiveAddCard) card).setExpressPort((FCPort) cardPort);
				}
			}

		}

		// add internal connections of each port
		List<NetworkPort> portConnections;
		for (NetworkPort port : card.getModulePorts()) {
			for (int i = 0; i < cardPortNums.length; i++) {
				if (cardPortNums[i].equals(Integer.toString(port.getPortNumber()))) {
					portConnections = new ArrayList<NetworkPort>();
					for (String portNum : connections.get(i)) {
						portConnections.add(card.getPort(Integer.parseInt(portNum)));
					}
					card.addInternalConnections(port, portConnections);
					break;
				}
			}
		}

		// TODO oposnl thing???

		return card;
	}

	/**
	 * Adds connections between ports of given opticalSwitch, loading them from a config file.
	 * 
	 * @param opticalSwitch
	 */
	public void addHardcodedCardConnections(org.opennaas.extensions.router.model.System opticalSwitch) {

		String srcPortID = null;
		String dstPortID = null;
		List<String[]> connections = topologyLoader.getConnections(opticalSwitch.getName());

		NetworkPort srcPort;
		NetworkPort dstPort;

		for (String[] connection : connections) {

			srcPort = null;
			dstPort = null;

			srcPortID = connection[0];
			dstPortID = connection[1];

			String nodeName = topologyLoader.getPortNodeName(srcPortID);
			int chasis = topologyLoader.getPortChassis(srcPortID);
			int slot = topologyLoader.getPortSlot(srcPortID);
			int portNumber = topologyLoader.getPortNumber(srcPortID);

			srcPort = getPort((ProteusOpticalSwitch) opticalSwitch, nodeName, chasis, slot, portNumber);

			if (srcPort != null) {
				nodeName = topologyLoader.getPortNodeName(dstPortID);
				chasis = topologyLoader.getPortChassis(dstPortID);
				slot = topologyLoader.getPortSlot(dstPortID);
				portNumber = topologyLoader.getPortNumber(dstPortID);

				dstPort = getPort((ProteusOpticalSwitch) opticalSwitch, nodeName, chasis, slot, portNumber);

				if (dstPort != null) {
					srcPort.addDeviceConnection(dstPort);
				}
			}
		}
	}

	private NetworkPort getPort(ProteusOpticalSwitch opticalSwitch, String nodeName, int chasis, int slot, int portNumber) {

		NetworkPort result = null;

		if (nodeName.equals(opticalSwitch.getName())) {
			// get card
			ProteusOpticalSwitchCard card = null;
			for (LogicalDevice dev : opticalSwitch.getLogicalDevices()) {
				if (dev instanceof ProteusOpticalSwitchCard) {
					if (((ProteusOpticalSwitchCard) dev).getChasis() == chasis &&
							((ProteusOpticalSwitchCard) dev).getModuleNumber() == slot) {
						card = (ProteusOpticalSwitchCard) dev;
						break;
					}
				}
			}
			if (card != null) {
				result = card.getPort(portNumber);
			}
		}

		if (result == null) {
			logger.warn("Unable to find desired port in model. Check topology file is correct. \n" +
					"Port details: node=" + nodeName + " chasis=" + chasis + " slot=" + slot + " portNumber=" + portNumber);
		}

		return result;
	}
}
