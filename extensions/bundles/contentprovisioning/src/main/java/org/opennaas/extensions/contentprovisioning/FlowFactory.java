package org.opennaas.extensions.contentprovisioning;

/*
 * #%L
 * OpenNaaS :: Content Provisioning
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
import java.util.List;

import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class FlowFactory {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	/**
	 * Creates a FloodlightOFFlow data structure with given input and output ports.
	 * 
	 * The FloodlightOFFlow will match all traffic arriving at inputPort and, and will redirect it to outputPort.
	 * 
	 * @param inputPort
	 * @param outputPort
	 * @return FloodlightOFFlow as described
	 */
	public static FloodlightOFFlow newFlow(String inputPort, String outputPort) {

		// create match filtering traffic arriving at inputPort
		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setIngressPort(inputPort);

		// create action redirecting matching traffic to port outputPort
		FloodlightOFAction action = new FloodlightOFAction();
		action.setType(FloodlightOFAction.TYPE_OUTPUT);
		action.setValue(outputPort);

		List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>(1);
		actions.add(action);

		FloodlightOFFlow flow = new FloodlightOFFlow();
		flow.setName(inputPort + "->-" + outputPort);
		flow.setActive(true);
		flow.setPriority(DEFAULT_FLOW_PRIORITY);
		// switchId will be set by the driver :)
		// flow.setSwitchId();
		flow.setMatch(match);
		flow.setActions(actions);

		return flow;
	}

}
