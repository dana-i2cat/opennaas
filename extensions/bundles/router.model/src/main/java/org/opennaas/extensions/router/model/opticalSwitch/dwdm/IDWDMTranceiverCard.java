package org.opennaas.extensions.router.model.opticalSwitch.dwdm;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;

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
