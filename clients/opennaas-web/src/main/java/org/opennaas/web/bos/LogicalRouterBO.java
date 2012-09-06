/**
 * 
 */
package org.opennaas.web.bos;

import org.opennaas.extensions.router.model.ComputerSystem;

/**
 * @author Jordi
 */
public class LogicalRouterBO extends GenericBO {

	/**
	 * Push an action in the queue to create a Logical Router
	 */
	public void createLogicalRouter() {
		String path = "router/lolaM20/chassis/createLogicalRouter";
		opennaasRest.post(getURL(path), getComputerSystem());
	}

	/**
	 * Get the ComputerSystem
	 * 
	 * @return a ComputerSystem
	 */
	private static ComputerSystem getComputerSystem() {
		ComputerSystem computerSystem = new ComputerSystem();
		computerSystem.setName("Name");
		return computerSystem;
	}

}
