package org.opennaas.extensions.opendaylight.vtn.protocol.client;

import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;

/**
 * OpenDaylight special client mixing CXF and Java clients allowing sending HTTP
 * DELETE with body
 *
 * @author Josep Batallé
 *
 */
public class OpenDaylightvtnAPIClient implements IOpenDaylightvtnAPIClient {

    private ProtocolSessionContext sessionContext;
    private IOpenDaylightvtnAPIClient cxfClient;
    Log log = LogFactory.getLog(OpenDaylightvtnAPIClient.class);

    public OpenDaylightvtnAPIClient(IOpenDaylightvtnAPIClient cxfClient, ProtocolSessionContext sessionContext) {
        this.cxfClient = cxfClient;
        this.sessionContext = sessionContext;
    }

    @Override
    public Response createVTN(VTN vtn) {
        return cxfClient.createVTN(vtn);
    }
    
    @Override
    public Response deleteVTN(String name) {
        return cxfClient.deleteVTN(name);
    }

    @Override
    public Response createController(OpenDaylightController controller) {
        return cxfClient.createController(controller);
    }
    
    @Override
    public Response createvBridge(String vtn, OpenDaylightvBridge vBridge) {
        return cxfClient.createvBridge(vtn, vBridge);
    }

    @Override
    public Response createInterfaces(String vtn_name, String vbr_name, vBridgeInterfaces if_name) {
        return cxfClient.createInterfaces(vtn_name, vbr_name, if_name);
    }

    @Override
    public Response createBoundary(Boundary bound) {
        return cxfClient.createBoundary(bound);
    }

    @Override
    public Response createvLink(String vtn_name, vLink vlink) {
        return cxfClient.createvLink(vtn_name, vlink);
    }

    @Override
    public Response configPortMap(String vtn_name, String vbr_name, String if_name, PortMap portMap) {
        return cxfClient.configPortMap(vtn_name, vbr_name, if_name, portMap);
    }
    
}
