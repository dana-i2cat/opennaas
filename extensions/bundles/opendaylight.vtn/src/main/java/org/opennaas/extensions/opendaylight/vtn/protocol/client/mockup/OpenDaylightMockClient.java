package org.opennaas.extensions.opendaylight.vtn.protocol.client.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;
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
    private List<OpenDaylightController> controllers;
    private List<OpenDaylightvBridge> vBridges;
    private List<vBridgeInterfaces> vInterfaces;

    public OpenDaylightMockClient() {
        this.flows = new HashMap<String, List<OpenDaylightOFFlow>>();
    }

    @Override
    public Response createVTN(VTN vtn) {
        vtns.add(vtn);
        return Response.ok().build();
    }

    @Override
    public Response createController(OpenDaylightController controller) {
        controllers.add(controller);
        return Response.ok().build();
    }

    @Override
    public Response createvBridge(String vtn, OpenDaylightvBridge vBridge) {
        vBridges.add(vBridge);
        return Response.ok().build();
    }

    @Override
    public Response deleteVTN(String name) {
        for (int i = 0; i<vtns.size(); i++){
            if(vtns.get(i).getVtn_name().equals(name))
                vtns.remove(i);
        }
        return Response.ok().build();
    }

    @Override
    public Response createInterfaces(String vtn_name, String vbr_name, vBridgeInterfaces if_name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response createBoundary(Boundary bound) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response createvLink(String vtn_name, vLink vlink) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response configPortMap(String vtn_name, String vbr_name, String if_name, PortMap portMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
