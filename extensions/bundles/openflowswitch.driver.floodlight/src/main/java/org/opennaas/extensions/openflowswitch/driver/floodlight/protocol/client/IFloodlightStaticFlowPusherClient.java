package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client;

import java.util.List;
import java.util.Map;

import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

public interface IFloodlightStaticFlowPusherClient {

	/**
	 * Adds a static flow.
	 * 
	 * @param flow
	 *            The flow to push.
	 */
	public void addFlow(FloodlightOFFlow flow);

	/**
	 * Deletes a static flow
	 * 
	 * @param name
	 *            The name of the static flow to delete.
	 */
	public void deleteFlow(String name);

	/**
	 * Deletes all static flows for a practicular switch
	 * 
	 * @param dpid
	 *            The DPID of the switch to delete flows for.
	 */
	public void deleteFlowsForSwitch(long dpid);

	/**
	 * Deletes all flows.
	 */
	public void deleteAllFlows();

	/**
	 * Gets all list of all flows
	 */
	public Map<String, List<FloodlightOFFlow>> getFlows();

	/**
	 * Gets a list of flows by switch
	 */
	public List<FloodlightOFFlow> getFlows(String dpid);

}
