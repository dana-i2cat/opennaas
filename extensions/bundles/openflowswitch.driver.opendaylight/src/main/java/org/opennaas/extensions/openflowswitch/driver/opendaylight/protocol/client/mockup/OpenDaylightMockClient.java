package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.PathParam;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.IOpenDaylightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.wrappers.OpenDaylightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 *
 * @author Adrian Rosello (i2CAT)
 *
 */
public class OpenDaylightMockClient implements IOpenDaylightStaticFlowPusherClient {

    private Map<String, List<OpenDaylightOFFlow>> flows;

    public OpenDaylightMockClient() {
        this.flows = new HashMap<String, List<OpenDaylightOFFlow>>();
    }

    @Override
    public void addFlow(OpenDaylightOFFlow flow, String DPID, String name) {
        String switchId = flow.getSwitchId();
        if (switchId != null) {
            List<OpenDaylightOFFlow> switchFlows = flows.get(switchId);
            if (switchFlows == null) {
                switchFlows = new ArrayList<OpenDaylightOFFlow>();
                switchFlows.add(flow);
                flows.put(switchId, switchFlows);
            } else {
                switchFlows.add(flow);
            }
        }
    }

    @Override
    public void deleteFlow(String DPID, String name) {
        for (String switchId : flows.keySet()) {
            List<OpenDaylightOFFlow> switchFlows = flows.get(switchId);
            List<OpenDaylightOFFlow> flowsToRemove = new ArrayList<OpenDaylightOFFlow>();
            for (OpenDaylightOFFlow existingFlow : switchFlows) {
                if (existingFlow.getName().equals(name)) {
                    flowsToRemove.add(existingFlow);
                }
            }
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
            List<OpenDaylightOFFlow> switchFlows = flows.get(switchId);
            switchFlows.clear();
        }
    }

    @Override
    public Map<String, List<OpenDaylightOFFlow>> getFlows() {
        return flows;
    }

    @Override
    public OpenDaylightOFFlowsWrapper getFlows(@PathParam("switchId") String dpid) {
        OpenDaylightOFFlowsWrapper flowsWrapper = new OpenDaylightOFFlowsWrapper();
        if (flows.get(String.valueOf(dpid)) != null) {
            flowsWrapper.addAll(flows.get(String.valueOf(dpid)));
        }
        return flowsWrapper;
    }

    @Override
    public OpenDaylightOFFlow getFlow(@PathParam("switchId") String dpid, @PathParam("name") String name) {
        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        for (String switchId : flows.keySet()) {
            List<OpenDaylightOFFlow> switchFlows = flows.get(switchId);
            for (OpenDaylightOFFlow existingFlow : switchFlows) {
                if (existingFlow.getName().equals(name)) {
                    return existingFlow;
                }
            }
        }
        return flow;
    }
}
