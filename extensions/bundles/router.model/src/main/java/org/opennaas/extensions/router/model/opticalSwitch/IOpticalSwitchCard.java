package org.opennaas.extensions.router.model.opticalSwitch;

import java.util.List;

import org.opennaas.extensions.router.model.NetworkPort;

public interface IOpticalSwitchCard {

	/**
	 * Connects srcChannel in srcPort with dstChannel in dstPort.
	 * 
	 * @param srcChannel
	 * @param srcPort
	 * @param dstChannel
	 * @param dstPort
	 * @return
	 */
	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort);

	public boolean removeSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort);

	public List getSwitchingRules();

	public List<NetworkPort> getPorts();

	/**
	 * Returns channels ready to be configured on given port. Notice it is not the same as free channels on given port, as this card may introduce
	 * further restrictions. Assumes given port is in this card
	 * 
	 * @param port
	 * @return
	 */
	public List<FiberChannel> getAvailableChannels(NetworkPort port);

	public boolean isPassive();

}
