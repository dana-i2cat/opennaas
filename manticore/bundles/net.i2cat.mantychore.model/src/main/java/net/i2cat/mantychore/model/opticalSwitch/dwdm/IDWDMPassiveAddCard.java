package net.i2cat.mantychore.model.opticalSwitch.dwdm;

import java.util.List;

import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.opticalSwitch.IOpticalSwitchCard;

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
