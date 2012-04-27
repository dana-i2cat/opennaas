package org.opennaas.extensions.ws.services;

import org.opennaas.extensions.router.model.ComputerSystem;

/**
 * @author Jordi Puig
 */

public interface IChassisCapabilityService {

	/*
	 * Logical Routers
	 */
	/**
	 * Creates a logical router.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param logicalRouter
	 *            to be created
	 * @param resourceId
	 */
	public void createLogicalRouter(String resourceId, ComputerSystem logicalRouter);

	/**
	 * Deletes given logical router.
	 * 
	 * @param logicalRouter
	 *            existing logical router to delete.
	 * @param resourceId
	 */
	public void deleteLogicalRouter(String resourceId, ComputerSystem logicalRouter);

}