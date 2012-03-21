package net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards;

import java.util.List;

import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberChannel;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.IDWDMTranceiverCard;

public class WonesysTransceiverCard extends ProteusOpticalSwitchCard implements IDWDMTranceiverCard {

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
