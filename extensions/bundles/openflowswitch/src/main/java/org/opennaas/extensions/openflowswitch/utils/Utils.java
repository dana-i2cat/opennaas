package org.opennaas.extensions.openflowswitch.utils;

import java.util.List;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 *
 * @author josep
 */
public class Utils {

    /**
     * OpenDaylight flow to OFFlow
     * @param flow
     * @return 
     */
    public static OFFlow ODLFlowToOFFlow(OpenDaylightOFFlow flow) {
        OFFlow OFFlow = new OFFlow();
        FloodlightOFMatch match = flow.getMatch();
        List<FloodlightOFAction> actions = flow.getActions();
        OFFlow.setName(flow.getName());
        OFFlow.setMatch(match);
        OFFlow.setActions(actions);
        return OFFlow;
    }

    /**
     * OFFlow to OpenDaylight flow
     * @param OFFlow
     * @return
     */
    public static OpenDaylightOFFlow OFFlowToODL(OFFlow OFFlow) {
        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        FloodlightOFMatch match = OFFlow.getMatch();
        List<FloodlightOFAction> actions = OFFlow.getActions();
        for(int i=0; i<actions.size(); i++){
            if(actions.get(i).getType().equals("output")){
                actions.get(i).setType("OUTPUT");
            }
        }
        
        flow.setName(OFFlow.getName());
        flow.setMatch(match);
        flow.setActions(actions);
        return flow;
    }

    /**
     * OFFlow to Floodlight flow
     * @param OFFlow
     * @return
     */
    public static FloodlightOFFlow OFFlowToFLD(OFFlow OFFlow) {
        FloodlightOFFlow flow = new FloodlightOFFlow();
        FloodlightOFMatch match = OFFlow.getMatch();
        List<FloodlightOFAction> actions = OFFlow.getActions();
	flow.setName(OFFlow.getName());
        flow.setMatch(match);
        flow.setActions(actions);
        return flow;
    }
    }
