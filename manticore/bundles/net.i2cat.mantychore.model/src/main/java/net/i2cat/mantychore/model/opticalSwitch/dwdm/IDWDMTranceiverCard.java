package net.i2cat.mantychore.model.opticalSwitch.dwdm;

import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.IOpticalSwitchCard;

/**
 * Allows transformation from electrical signal to optical, and viceversa.
 *
 * @author isart
 *
 */
public interface IDWDMTranceiverCard extends IOpticalSwitchCard {

	public void configureElectricToOptic(NetworkPort srcPort, FCPort dstPort, DWDMChannel dstChannel);

	public void configureOpticToElectric(FCPort srcPort, DWDMChannel srcChannel, NetworkPort dstPort);

}
