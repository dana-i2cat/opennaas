package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;

import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class FloodlightMockClient implements IFloodlightStaticFlowPusherClient {

	private Map<String, List<FloodlightOFFlow>>	flows;

	public FloodlightMockClient() {
		this.flows = new HashMap<String, List<FloodlightOFFlow>>();
	}

	@Override
	public void addFlow(FloodlightOFFlow flow) {

		String switchId = flow.getSwitchId();

		if (switchId != null) {
			List<FloodlightOFFlow> switchFlows = flows.get(switchId);

			if (switchFlows == null) {
				switchFlows = new ArrayList<FloodlightOFFlow>();
				switchFlows.add(flow);
				flows.put(switchId, switchFlows);
			}
			else
				switchFlows.add(flow);

		}
	}

	@Override
	public void deleteFlow(String name) {

		for (String switchId : flows.keySet()) {

			List<FloodlightOFFlow> switchFlows = flows.get(switchId);
			List<FloodlightOFFlow> flowsToRemove = new ArrayList<FloodlightOFFlow>();

			for (FloodlightOFFlow flow : switchFlows)
				if (flow.getName().equals(name))
					flowsToRemove.add(flow);

			switchFlows.removeAll(flowsToRemove);

		}

	}

	@Override
	public void deleteFlowsForSwitch(@PathParam("switchId") long dpid) {

		flows.get(String.valueOf(dpid)).clear();

	}

	@Override
	public void deleteAllFlows() {

		for (String switchId : flows.keySet()) {

			List<FloodlightOFFlow> switchFlows = flows.get(switchId);
			switchFlows.clear();

		}

	}

	@Override
	public Map<String, List<FloodlightOFFlow>> getFlows() {

		return flows;

	}

	@Override
	public List<FloodlightOFFlow> getFlows(@PathParam("switchId") String dpid) {

		return flows.get(String.valueOf(dpid));
	}

}
