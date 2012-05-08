package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards;

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.IDWDMTransponderCard;

public class WonesysTransponderCard extends ProteusOpticalSwitchCard implements IDWDMTransponderCard {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6851684948308799291L;

	public WonesysTransponderCard(int chasis, int slot, int type, int subtype) {
		super(chasis, slot, type, subtype);
		// TODO Auto-generated constructor stub
	}

	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort) {
		// TODO Auto-generated method stub
		return false;
	}

	public List getSwitchingRules() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<NetworkPort> getPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	public void configureChannelConversion(FCPort srcPort, DWDMChannel srcChannel, FCPort dstPort, DWDMChannel dstChannel) {
		// TODO Auto-generated method stub

	}

	public List<FiberChannel> getAvailableChannels(NetworkPort port) {
		// TODO Auto-generated method stub
		return null;
	}

}
