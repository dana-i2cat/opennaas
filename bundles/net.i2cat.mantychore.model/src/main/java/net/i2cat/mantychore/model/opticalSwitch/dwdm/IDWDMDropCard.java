package net.i2cat.mantychore.model.opticalSwitch.dwdm;

import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.IOpticalSwitchCard;

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