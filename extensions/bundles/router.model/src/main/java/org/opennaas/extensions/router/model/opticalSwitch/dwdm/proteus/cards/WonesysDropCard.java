package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards;

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;

public class WonesysDropCard extends ProteusOpticalSwitchCard {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8837660093155598972L;
	/**
	 * Used in order to interlink several modules in a single rack. It is often used to link with a passive add card.
	 */
	private FCPort	expressPort;
	/**
	 * Is the input of the multiplexed signal coming from the network. The only input port of this card.
	 */
	private FCPort	commonPort;

	public WonesysDropCard(int chasis, int slot, int type, int subtype) {
		super(chasis, slot, type, subtype);
	}

	public void setExpressPort(FCPort expressPort) {
		this.expressPort = expressPort;
	}

	public FCPort getExpressPort() {
		return expressPort;
	}

	public FCPort getCommonPort() {
		return commonPort;
	}

	public void setCommonPort(FCPort commonPort) {
		this.commonPort = commonPort;
	}

	@Override
	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort ignoredsrcPort, FiberChannel ignoreddstChannel, NetworkPort dstPort) {

		// NOTE: only one redirection per channel is allowed in WonesysDropCards
		// remove previous redirection of given channel (if any)
		FCPort srcSubPort = getSubPort(commonPort, (DWDMChannel) srcChannel);
		if (srcSubPort != null) {
			NetworkPort currentDstPort = (NetworkPort) ((FCPort) srcSubPort.getOutgoingDeviceConnections().get(0)).getDevices().get(0);
			removeSwitchingRule(srcChannel, commonPort, srcChannel, currentDstPort);
		}

		return super.addSwitchingRule(srcChannel, commonPort, srcChannel, dstPort);
	}

	@Override
	public boolean removeSwitchingRule(FiberChannel srcChannel, NetworkPort ignoredsrcPort, FiberChannel ignoreddstChannel, NetworkPort dstPort) {
		return super.removeSwitchingRule(srcChannel, commonPort, srcChannel, dstPort);
	}

	@Override
	public List<FiberChannel> getAvailableChannels(NetworkPort port) {
		// Despite free channels on given port, in a WonesysDropCard only channels that are not yet configured on commonPort are available.
		return getFreeChannels(commonPort);
	}
}
