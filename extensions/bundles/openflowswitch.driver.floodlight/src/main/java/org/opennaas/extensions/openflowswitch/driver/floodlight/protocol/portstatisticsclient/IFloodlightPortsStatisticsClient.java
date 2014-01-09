package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers.SwitchStatisticsMap;

/**
 * Floodlight ports statistics API client
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/wm/core")
public interface IFloodlightPortsStatisticsClient {

	/**
	 * Get ports statistics for all switches
	 */
	@GET
	@Path("/switch/all/port/json")
	@Produces(MediaType.APPLICATION_JSON)
	public SwitchStatisticsMap getPortsStatisticsForAllSwitches() throws ProtocolException, Exception;

	/**
	 * Get ports statistics for one switch
	 */
	@GET
	@Path("/switch/{switchId}/port/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SwitchStatisticsMap getPortsStatistics(@PathParam("switchId") String switchId) throws ProtocolException, Exception;

}
