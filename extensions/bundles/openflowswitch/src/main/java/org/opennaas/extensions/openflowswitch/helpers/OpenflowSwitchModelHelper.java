package org.opennaas.extensions.openflowswitch.helpers;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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
import java.util.Iterator;
import java.util.List;

import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public abstract class OpenflowSwitchModelHelper {

	public static List<FloodlightOFFlow> getSwitchForwardingRules(OpenflowSwitchModel model) {

		List<FloodlightOFFlow> forwardingRules = new ArrayList<FloodlightOFFlow>();

		Iterator<OFFlowTable> iterator = model.getOfTables().iterator();
		while (iterator.hasNext())
			forwardingRules.addAll(iterator.next().getOfForwardingRules());

		return forwardingRules;
	}

	public static OpenflowSwitchModel generateSampleModel() {

		OpenflowSwitchModel model = new OpenflowSwitchModel();
		List<FloodlightOFFlow> forwardingRules = generateSampleOFForwardingRules();

		OFFlowTable table = new OFFlowTable();
		table.setTableId("table1");
		table.setOfForwardingRules(forwardingRules);

		model.getOfTables().add(table);

		return model;
	}

	public static List<FloodlightOFFlow> generateSampleOFForwardingRules() {
		List<FloodlightOFFlow> rules = new ArrayList<FloodlightOFFlow>();

		FloodlightOFFlow rule1 = generateSampleOFForwardingRule("1", "1", "1");
		FloodlightOFFlow rule2 = generateSampleOFForwardingRule("2", "2", "2");
		rules.add(rule1);
		rules.add(rule2);

		return rules;

	}

	public static FloodlightOFFlow generateSampleOFForwardingRule(String flowId, String dstPort, String priority) {
		FloodlightOFFlow rule = new FloodlightOFFlow();

		FloodlightOFMatch match = new FloodlightOFMatch();
		match.setDstPort(dstPort);

		rule.setName(flowId);
		rule.setPriority(priority);
		rule.setMatch(match);

		return rule;
	}

}
