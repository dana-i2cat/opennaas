package org.opennaas.extensions.power.capabilities;

import org.opennaas.core.resources.capability.ICapability;

public interface IPowerManagementCapability extends ICapability {
	
	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	public boolean getPowerStatus() throws Exception;

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOn() throws Exception;

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOff() throws Exception;

}
