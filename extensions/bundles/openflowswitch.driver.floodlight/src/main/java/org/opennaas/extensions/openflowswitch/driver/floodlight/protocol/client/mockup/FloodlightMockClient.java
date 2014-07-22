package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;

import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.wrappers.FloodlightOFFlowsWrapper;
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
	public void deleteFlow(FloodlightOFFlow flow) {

		for (String switchId : flows.keySet()) {

			List<FloodlightOFFlow> switchFlows = flows.get(switchId);
			List<FloodlightOFFlow> flowsToRemove = new ArrayList<FloodlightOFFlow>();

			for (FloodlightOFFlow existingFlow : switchFlows)
				if (existingFlow.getName().equals(flow.getName()))
					flowsToRemove.add(flow);

			switchFlows.removeAll(flowsToRemove);

		}

	}

	@Override
	public void deleteFlowsForSwitch(@PathParam("switchId") String dpid) {

		flows.get(dpid).clear();

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
	public FloodlightOFFlowsWrapper getFlows(@PathParam("switchId") String dpid) {
		FloodlightOFFlowsWrapper flowsWrapper = new FloodlightOFFlowsWrapper();
		if (flows.get(String.valueOf(dpid)) != null)
			flowsWrapper.addAll(flows.get(String.valueOf(dpid)));

		return flowsWrapper;
	}

}
