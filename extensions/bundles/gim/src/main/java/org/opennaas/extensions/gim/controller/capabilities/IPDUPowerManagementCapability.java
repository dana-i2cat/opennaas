package org.opennaas.extensions.gim.controller.capabilities;

import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;

public interface IPDUPowerManagementCapability extends IPDUInventaryCapability {

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	public boolean getPowerStatus(PDUPort port) throws Exception;

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOn(PDUPort port) throws Exception;

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOff(PDUPort port) throws Exception;

}
