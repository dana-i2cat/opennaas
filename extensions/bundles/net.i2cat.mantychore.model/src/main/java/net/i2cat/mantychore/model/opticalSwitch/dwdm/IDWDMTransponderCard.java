package net.i2cat.mantychore.model.opticalSwitch.dwdm;

import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.IOpticalSwitchCard;

/**
 * Allows conversion of channels
 * 
 * @author isart
 * 
 */
public interface IDWDMTransponderCard extends IOpticalSwitchCard {

	public void configureChannelConversion(FCPort srcPort, DWDMChannel srcChannel, FCPort dstPort, DWDMChannel dstChannel);

}
