package org.opennaas.extensions.openflowswitch.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlowTable;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

public abstract class OpenflowSwitchModelHelper {

    static Log log = LogFactory.getLog(OpenflowSwitchModelHelper.class);

    public static List<OFFlow> getSwitchForwardingRules(OpenflowSwitchModel model) {
        List<OFFlow> forwardingRules = new ArrayList<OFFlow>();

        Iterator<OFFlowTable> iterator = model.getOfTables().iterator();
        while (iterator.hasNext()) {
            OFFlowTable it = iterator.next();
            forwardingRules.addAll(it.getOfForwardingRules());
        }

        return forwardingRules;
    }

    public static OpenflowSwitchModel generateSampleModel() {

        OpenflowSwitchModel model = new OpenflowSwitchModel();
        List<OFFlow> forwardingRules = generateSampleOFForwardingRules();

        OFFlowTable table = new OFFlowTable();
        table.setTableId("table1");
        table.setOfForwardingRules(forwardingRules);

        model.getOfTables().add(table);

        return model;
    }

    public static List<OFFlow> generateSampleOFForwardingRules() {
        List<OFFlow> rules = new ArrayList<OFFlow>();

        OFFlow rule1 = generateSampleOFForwardingRule("1", "1", "1");
        OFFlow rule2 = generateSampleOFForwardingRule("2", "2", "2");
        rules.add(rule1);
        rules.add(rule2);

        return rules;

    }

    public static OFFlow generateSampleOFForwardingRule(String flowId, String dstPort, String priority) {
        OFFlow rule = new OFFlow();

        FloodlightOFMatch match = new FloodlightOFMatch();
        match.setDstPort(dstPort);

        rule.setName(flowId);
        rule.setPriority(priority);
        rule.setMatch(match);

        return rule;
    }

}
