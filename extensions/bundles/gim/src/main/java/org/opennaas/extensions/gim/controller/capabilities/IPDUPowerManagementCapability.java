package org.opennaas.extensions.gim.controller.capabilities;


public interface IPDUPowerManagementCapability extends IPDUInventaryCapability {

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	public boolean getPowerStatus(String portId) throws Exception;

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOn(String portId) throws Exception;

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOff(String portId) throws Exception;

}
