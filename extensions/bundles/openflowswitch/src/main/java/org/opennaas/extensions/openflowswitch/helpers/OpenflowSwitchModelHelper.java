package org.opennaas.extensions.openflowswitch.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openflow.protocol.OFFlowMod;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OFForwardingRule;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public abstract class OpenflowSwitchModelHelper {

	public static List<OFForwardingRule> getSwitchForwardingRules(OpenflowSwitchModel model) {

		List<OFForwardingRule> forwardingRules = new ArrayList<OFForwardingRule>();

		Iterator<OFFlowTable> iterator = model.getOfTables().iterator();
		while (iterator.hasNext())
			forwardingRules.addAll(iterator.next().getOfForwardingRules());

		return forwardingRules;
	}

	public static OpenflowSwitchModel generateSampleModel() {

		OpenflowSwitchModel model = new OpenflowSwitchModel();
		List<OFForwardingRule> forwardingRules = generateSampleOFForwardingRules();

		OFFlowTable table = new OFFlowTable();
		table.setTableId("table1");
		table.setOfForwardingRules(forwardingRules);

		model.getOfTables().add(table);

		return model;
	}

	public static List<OFForwardingRule> generateSampleOFForwardingRules() {
		List<OFForwardingRule> rules = new ArrayList<OFForwardingRule>();

		OFForwardingRule rule1 = generateSampleOFForwardingRule("1", "1", "1");
		OFForwardingRule rule2 = generateSampleOFForwardingRule("2", "2", "2");
		rules.add(rule1);
		rules.add(rule2);

		return rules;

	}

	public static OFForwardingRule generateSampleOFForwardingRule(String flowId, String dstPort, String priority) {
		OFForwardingRule rule = new OFForwardingRule();
		OFFlowMod flowMod = new OFFlowMod();

		flowMod.setPriority(Short.valueOf(priority));
		flowMod.setOutPort(Short.valueOf(dstPort));

		rule.setFlowId(flowId);
		rule.setOfFlowMod(flowMod);

		return rule;
	}

}
