package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.wrappers.CountersMap;

/**
 * Floodlight counters API client
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/wm/core/counter")
public interface IFloodlightCountersClient {

	/**
	 * Get all counters for all switches
	 */
	@GET
	@Path("/all/json")
	@Produces(MediaType.APPLICATION_JSON)
	public CountersMap getAllCountersForAllSwitches() throws ProtocolException, Exception;

	/**
	 * Get all counters for one switch
	 */
	@GET
	@Path("/{switchId}/all/json")
	@Produces(MediaType.APPLICATION_JSON)
	public CountersMap getAllCounters(@PathParam("switchId") String switchId) throws ProtocolException, Exception;

	/**
	 * Get one counter for one switch
	 */
	@GET
	@Path("/{switchId}/{counterId}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public CountersMap getCounter(@PathParam("switchId") String switchId, @PathParam("counterId") String counterId) throws ProtocolException,
			Exception;

}
