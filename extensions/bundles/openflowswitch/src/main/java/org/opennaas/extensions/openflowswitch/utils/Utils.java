package org.opennaas.extensions.openflowswitch.utils;

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
