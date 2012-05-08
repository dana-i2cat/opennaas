package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards;

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.IDWDMTranceiverCard;

public class WonesysTransceiverCard extends ProteusOpticalSwitchCard implements IDWDMTranceiverCard {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3406989632621913235L;

	public WonesysTransceiverCard(int chasis, int slot, int type, int subtype) {
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

	public void configureElectricToOptic(NetworkPort srcPort, FCPort dstPort, DWDMChannel dstChannel) {
		// TODO Auto-generated method stub

	}

	public void configureOpticToElectric(FCPort srcPort, DWDMChannel srcChannel, NetworkPort dstPort) {
		// TODO Auto-generated method stub

	}

	public List<FiberChannel> getAvailableChannels(NetworkPort port) {
		// TODO Auto-generated method stub
		return null;
	}

}
