package org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.mockup;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.model.RyuOFFlow;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.wrappers.RyuOFFlowListWrapper;

/**
 * Mock {@link IRyuStatsClient}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class RyuMockClient implements IRyuStatsClient {

	private Map<String, List<RyuOFFlow>>	flows;

	public RyuMockClient() {
		this.flows = new HashMap<String, List<RyuOFFlow>>();
	}

	@Override
	public void addFlowEntry(RyuOFFlow flow) throws ProtocolException, Exception {
		String dpid = flow.getDpid();

		if (dpid != null) {
			List<RyuOFFlow> switchFlows = flows.get(dpid);

			if (switchFlows == null) {
				switchFlows = new ArrayList<RyuOFFlow>();
				switchFlows.add(flow);
				flows.put(dpid, switchFlows);
			}
			else {
				switchFlows.add(flow);
			}
		}
	}

	@Override
	public void deleteFlowEntryStrictly(RyuOFFlow flow) throws ProtocolException, Exception {
		List<RyuOFFlow> switchFlows = flows.get(flow.getDpid());
		List<RyuOFFlow> flowsToRemove = new ArrayList<RyuOFFlow>();

		for (RyuOFFlow existingFlow : switchFlows)
			if (existingFlow.getName().equals(flow.getName()))
				flowsToRemove.add(flow);

		switchFlows.removeAll(flowsToRemove);
	}

	@Override
	public void deleteAllFlowsEntriesForDPID(String dpid) throws ProtocolException, Exception {
		flows.get(dpid).clear();
	}

	@Override
	public RyuOFFlowListWrapper getFlows(String dpid) throws ProtocolException, Exception {
		RyuOFFlowListWrapper listWrapper = new RyuOFFlowListWrapper();
		if (flows.get(String.valueOf(dpid)) != null) {
			listWrapper.addAll(flows.get(String.valueOf(dpid)));
		}

		return listWrapper;
	}
}
