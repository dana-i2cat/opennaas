package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards;

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;

/**
 * Represents a card that has all ports redirected to the commonPort, and can not be configured. A passive Add card allows adding to a dstPort ALL
 * channels within a srcPorts set. If same channel is used more than once per src set, bad things may happen :o
 * 
 * @author isart
 * 
 */
public class WonesysPassiveAddCard extends ProteusOpticalSwitchCard {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1494149756403636728L;
	/**
	 * Used in order to interlink several modules in a single rack. It is often linked to a drop card.
	 */
	private FCPort	expressPort;
	/**
	 * Is the output of the multiplexed signal going to the network. The only output port of this card.
	 */
	private FCPort	commonPort;

	public WonesysPassiveAddCard(int chasis, int slot, int type, int subtype) {
		super(chasis, slot, type, subtype);
	}

	public FCPort getExpressPort() {
		return expressPort;
	}

	public void setExpressPort(FCPort expressPort) {
		this.expressPort = expressPort;
	}

	public FCPort getCommonPort() {
		return commonPort;
	}

	public void setCommonPort(FCPort commonPort) {
		this.commonPort = commonPort;
	}

	public List<FiberChannel> getAvailableChannels(NetworkPort port) {
		// If any channel in use in commonPort is reused bad thing may happen
		// so they should not be reused
		// This kind of cards does not allow to select which port a channel comes from,
		// and as any channel is redirectd to commonPort,
		// Any channel in use in this card is in use in commonPort
		return getFreeChannels(commonPort);
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel ignoredDstChannel, NetworkPort ignoredDstPort) {
		NetworkPort dstPort = getInternalConnections(srcPort).get(0);
		// NetworkPort dstPort = (NetworkPort) srcPort.getOutgoingDeviceConnections().get(0);
		return super.addSwitchingRule(srcChannel, srcPort, srcChannel, dstPort);
	}

}
