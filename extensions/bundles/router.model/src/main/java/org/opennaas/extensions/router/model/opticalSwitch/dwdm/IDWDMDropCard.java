package org.opennaas.extensions.router.model.opticalSwitch.dwdm;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;

public interface IDWDMDropCard extends IOpticalSwitchCard {

	/**
	 * Drops given channel from srcPort and redirects it to dstPort
	 * 
	 * @param channel
	 * @param srcPort
	 * @param dstPort
	 */
	public boolean configureDropChannel(DWDMChannel channel, FCPort srcPort, FCPort dstPort);

	/**
	 * Removes a previously configured drop of a channel.
	 * 
	 * @param channel
	 * @param srcPort
	 * @param dstPort
	 */
	public boolean removeDropChannel(DWDMChannel channel, FCPort srcPort, FCPort dstPort);

}