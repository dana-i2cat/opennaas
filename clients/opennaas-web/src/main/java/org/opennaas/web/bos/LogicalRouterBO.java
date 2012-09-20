/**
 * 
 */
package org.opennaas.web.bos;

import org.opennaas.extensions.router.model.ComputerSystem;

/**
 * @author Jordi
 */
public class LogicalRouterBO extends GenericBO {

	private static final String	CREATE_LOGICAL_ROUTER	= "router/lolaM20/chassis/createLogicalRouter";

	/**
	 * Push an action in the queue to create a Logical Router
	 */
	public void createLogicalRouter() {
		opennaasRest.post(getURL(CREATE_LOGICAL_ROUTER), getComputerSystem());
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
