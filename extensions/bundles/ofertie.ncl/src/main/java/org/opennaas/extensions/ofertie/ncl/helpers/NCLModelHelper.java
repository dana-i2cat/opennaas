package org.opennaas.extensions.ofertie.ncl.helpers;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import java.util.List;

import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class NCLModelHelper {

	/**
	 * Checks if, at least, one of the given flows is allocated in the specific switch port.
	 * 
	 * @param switchName
	 * @param portId
	 * @param circuitFlows
	 * @return
	 */
	public static boolean flowsContainPort(String switchName, String portId, List<NetOFFlow> circuitFlows) {

		for (NetOFFlow flow : circuitFlows)
			if (flow.getResourceId().equals(switchName))
				if (isFlowInputPort(flow, portId) || isFlowOutputPort(flow, portId))
					return true;

		return false;
	}

	/**
	 * Checks if the given port is an input port of the switch of the flow.
	 * 
	 * @param flow
	 * @param portId
	 * @return
	 */
	public static boolean isFlowInputPort(NetOFFlow flow, String portId) {
		if (flow.getMatch().getIngressPort().equals(portId))
			return true;

		return false;
	}

	public static boolean isFlowOutputPort(NetOFFlow flow, String portId) {
		for (FloodlightOFAction action : flow.getActions()) {
			if (action.getType().equals("output") && action.getValue().equals(portId))
				return true;
		}

		return false;
	}
}
