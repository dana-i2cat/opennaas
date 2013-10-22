package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.wrappers.FloodlightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

@Path("/wm/staticflowentrypusher")
public interface IFloodlightStaticFlowPusherClient {

	/**
	 * Adds a static flow.
	 * 
	 * @param flow
	 *            The flow to push.
	 */
	@Path("/json")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void addFlow(FloodlightOFFlow flow) throws ProtocolException, Exception;

	/**
	 * Deletes a static flow
	 * 
	 * @param name
	 *            The name of the static flow to delete.
	 */
	@Path("/json")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteFlow(FloodlightOFFlow flow) throws ProtocolException, Exception;

	/**
	 * Deletes all static flows for a particular switch
	 * 
	 * @param dpid
	 *            The DPID of the switch to delete flows for.
	 */
	@Path("clear/{switchId}/json")
	public void deleteFlowsForSwitch(@PathParam("switchId") String dpid) throws ProtocolException, Exception;

	/**
	 * Deletes all flows.
	 */
	@Path("clear/all/json")
	public void deleteAllFlows() throws ProtocolException, Exception;

	/**
	 * Gets all list of all flows
	 */
	@Path("list/all/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, List<FloodlightOFFlow>> getFlows() throws ProtocolException, Exception;

	/**
	 * Gets a list of flows by switch
	 */
	@Path("list/{switchId}/json")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public FloodlightOFFlowsWrapper getFlows(@PathParam("switchId") String dpid) throws ProtocolException, Exception;

}
