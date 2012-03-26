package org.opennaas.extensions.router.model.opticalSwitch.dwdm;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;

/**
 * Allows conversion of channels
 * 
 * @author isart
 * 
 */
public interface IDWDMTransponderCard extends IOpticalSwitchCard {

	public void configureChannelConversion(FCPort srcPort, DWDMChannel srcChannel, FCPort dstPort, DWDMChannel dstChannel);

}
