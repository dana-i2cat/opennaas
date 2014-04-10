package org.opennaas.extensions.opendaylight.vtn.protocol.client.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.IOpenDaylightvtnAPIClient;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.OpenDaylightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 *
 * @author Adrian Rosello (i2CAT)
 *
 */
public class OpenDaylightMockClient implements IOpenDaylightvtnAPIClient {

    private Map<String, List<OpenDaylightOFFlow>> flows;
    private List<VTN> vtns;

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

    @Override
    public Response createVTN(VTN vtn) {
        vtns.add(vtn);
        return Response.ok().build();
    }

    @Override
    public Response createController(OpenDaylightController controller) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
