package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;

public class ProteusOpticalSwitch extends org.opennaas.extensions.router.model.System {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6737244447225380204L;
	private List<FiberConnection>	fiberConnections	= new ArrayList<FiberConnection>();

	public List<FiberConnection> getFiberConnections() {
		return fiberConnections;
	}

	public void setFiberConnections(List<FiberConnection> fiberConnections) {
		this.fiberConnections = fiberConnections;
	}

	/**
	 * Returns ProteusOpticalSwitchCard identifyied with given chasis and slot, or null if there is no such card.
	 * 
	 * @param chasis
	 * @param slot
	 * @return
	 */
	public ProteusOpticalSwitchCard getCard(int chasis, int slot) {

		for (LogicalDevice dev : getLogicalDevices()) {
			if (dev instanceof ProteusOpticalSwitchCard) {
				if (((ProteusOpticalSwitchCard) dev).getChasis() == chasis &&
						((ProteusOpticalSwitchCard) dev).getSlot() == slot) {
					return (ProteusOpticalSwitchCard) dev;
				}
			}
		}
		return null;
	}

}
