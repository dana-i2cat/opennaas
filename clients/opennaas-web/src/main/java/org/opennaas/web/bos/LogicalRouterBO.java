/**
 * 
 */
package org.opennaas.web.bos;

import org.opennaas.extensions.router.model.ComputerSystem;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Jordi
 */
public class LogicalRouterBO extends GenericBO {

	public void createLogicalRouter() {
		ClientResponse response = opennaasRest
				.executePost("router", "lolaM20", "chassis", "createLogicalRouter", getComputerSystem());
	}

	/**
	 * @return
	 */
	private static ComputerSystem getComputerSystem() {
		ComputerSystem computerSystem = new ComputerSystem();
		computerSystem.setName("Name");
		return computerSystem;
	}

}
