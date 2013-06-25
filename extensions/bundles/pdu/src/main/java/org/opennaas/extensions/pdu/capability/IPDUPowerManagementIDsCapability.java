package org.opennaas.extensions.pdu.capability;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.opennaas.core.resources.capability.ICapability;

@Path("/")
public interface IPDUPowerManagementIDsCapability extends ICapability {

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	@Path("/powerstatus/{portId}")
	@GET
	public boolean getPowerStatus(@PathParam("portId") String portId) throws Exception;

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Path("/powerstatus/{portId}/on")
	@POST
	public boolean powerOn(@PathParam("portId") String portId) throws Exception;

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Path("/powerstatus/{portId}/off")
	@POST
	public boolean powerOff(@PathParam("portId") String portId) throws Exception;

}
