package org.opennaas.extensions.power.capabilities;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;

@Path("/")
public interface IPowerManagementCapability extends ICapability {

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	@Path("/powerstatus")
	@GET
	public boolean getPowerStatus() throws Exception;

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Path("/powerstatus/on")
	@POST
	public boolean powerOn() throws Exception;

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Path("/powerstatus/off")
	@POST
	public boolean powerOff() throws Exception;

}
