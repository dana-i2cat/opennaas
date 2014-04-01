/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opennaas.extensions.openflowswitch.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    static Log log = LogFactory.getLog(Utils.class);

    public static OFFlow ODLFlowToOFFlow(OpenDaylightOFFlow flow) {
        log.error("OFFLOW TO ODL -----------------------------------");
        OFFlow OFFlow = new OFFlow();
        FloodlightOFMatch match;
        List<FloodlightOFAction> actions;
        FloodlightOFAction action = new FloodlightOFAction();
        match = flow.getMatch();
        actions = flow.getActions();
        
        log.error("NAME: " + flow.getName());
        OFFlow.setName(flow.getName());
        OFFlow.setMatch(match);
        OFFlow.setActions(actions);
        return OFFlow;
    }

    /**
     *
     * @param OFFlow
     * @return
     */
    public static OpenDaylightOFFlow OFFlowToODL(OFFlow OFFlow) {
        log.error("OFFLOW TO ODL -----------------------------------");
        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        FloodlightOFMatch match;
        List<FloodlightOFAction> actions;
        FloodlightOFAction action = new FloodlightOFAction();
        match = OFFlow.getMatch();
        actions = OFFlow.getActions();
        for(int i=0; i<actions.size(); i++){
            if(actions.get(i).getType().equals("output")){
                actions.get(i).setType("OUTPUT");
            }
        }
        /*        match.setSrcIp(OFFlow.getMatch().getSrcIp());
         match.setDstIp(OFFlow.getMatch().getDstIp());
         match.setSrcMac(OFFlow.getMatch().getSrcMac());
         match.setDstMac(OFFlow.getMatch().getSrcMac());
         match.setSrcPort(OFFlow.getMatch().getSrcPort());
         match.setProtocol(OFFlow.getMatch().getProtocol());
         match.setEtherType(OFFlow.getMatch().getEtherType());
         match.setIngressPort(OFFlow.getMatch().getIngressPort());
         for(FloodlightOFAction act : OFFlow.getActions()){
         action.setType(OFFlow.getActions().get(0).getType());
         action.setValue(OFFlow.getActions().get(0).getValue());
         actions.add(act);
         }
         */
log.error("NAME: " + OFFlow.getName());
        flow.setName(OFFlow.getName());
        flow.setMatch(match);
log.error("Ethertype: " + OFFlow.getMatch().getEtherType());
        flow.setActions(actions);
        return flow;
    }

    /**
     *
     * @param OFFlow
     * @return
     */
    public static FloodlightOFFlow OFFlowToFLD(OFFlow OFFlow) {
        FloodlightOFFlow flow = new FloodlightOFFlow();
        FloodlightOFMatch match;
        List<FloodlightOFAction> actions;
        FloodlightOFAction action = new FloodlightOFAction();
        match = OFFlow.getMatch();
        actions = OFFlow.getActions();
        /*        match.setSrcIp(OFFlow.getMatch().getSrcIp());
         match.setDstIp(OFFlow.getMatch().getDstIp());
         match.setSrcMac(OFFlow.getMatch().getSrcMac());
         match.setDstMac(OFFlow.getMatch().getSrcMac());
         match.setSrcPort(OFFlow.getMatch().getSrcPort());
         match.setProtocol(OFFlow.getMatch().getProtocol());
         match.setEtherType(OFFlow.getMatch().getEtherType());
         match.setIngressPort(OFFlow.getMatch().getIngressPort());
         for(FloodlightOFAction act : OFFlow.getActions()){
         action.setType(OFFlow.getActions().get(0).getType());
         action.setValue(OFFlow.getActions().get(0).getValue());
         actions.add(act);
         }
         */
        flow.setMatch(match);
        flow.setActions(actions);
        return flow;
    }
    
    public static String createFlowName(String id, String ethType, String source, String target, String dpid) {
        log.error("SETNAME FLOW: 0-" + ethType + "-" + source + "-" + target + "-" + dpid.substring(dpid.length() - 2));
//        return id+"-"+ethType+"-"+source+"-"+target+"-" + dpid.substring(dpid.length() - 2);
        return "0-" + ethType + "-" + source + "-" + target + "-" + dpid.substring(dpid.length() - 2);
    }
}
