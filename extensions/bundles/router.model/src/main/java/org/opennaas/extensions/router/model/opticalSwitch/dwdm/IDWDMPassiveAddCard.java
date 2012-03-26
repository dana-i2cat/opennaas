package org.opennaas.extensions.router.model.opticalSwitch.dwdm;

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;

/**
 * A passive Add card allows adding to a dstPort ALL channels within a srcPorts set. If same channel is used more than once per src set, bad things
 * may happen :o
 * 
 * @author isart
 * 
 */
public interface IDWDMPassiveAddCard extends IOpticalSwitchCard {

	public boolean configureAddPassthrough(FCPort srcPort, FCPort dstPort);

	public boolean removeAddPassthrough(FCPort srcPort, FCPort dstPort);

	public List getPassthroughConnections();

}
