package org.opennaas.extensions.router.model.opticalSwitch.dwdm;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;

/**
 * An add card allows adding DWDMChannels into a fiber. A configurable add card may filter channels to add into a fiber, and may be capable of
 * selecting witch port a channel should reach.
 * 
 * @author isart
 * 
 */
public interface IDWDMAddCard extends IOpticalSwitchCard {

	public void configureAddChannel(DWDMChannel channel, FCPort srcPort, FCPort dstPort);

	public void removeAddChannel(DWDMChannel channel, FCPort srcPort, FCPort dstPort);

}
