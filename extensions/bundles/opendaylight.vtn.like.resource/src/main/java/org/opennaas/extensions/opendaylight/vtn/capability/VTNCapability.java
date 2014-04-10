package org.opennaas.extensions.opendaylight.vtn.capability;

import java.io.InputStream;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;

//import org.opennaas.extensions.openflowswitch.utils.Utils;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class VTNCapability implements IVTNCapability {

    Log log = LogFactory.getLog(VTNCapability.class);

    public VTNCapability() {
    }

    @Override
    public Response test(String name) {
        VTN vtn = new VTN();
        vtn.setVtn_name(name);
        return Response.ok().build();
    }
    
    @Override
    public Response createVTN(String name) {
        VTN vtn = new VTN();
        vtn.setVtn_name(name);
        return Response.ok().build();
    }

    @Override
    public Response removeVTN(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getRoute(String ipSource, String ipDest, String switchDPID, int inputPort, boolean proactive) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort, int lifeTime) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response removeRoute(int id, int version) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response removeRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response removeRoutes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getRoutes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getRoutes(int version) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getRoutes(int version, String ipSrc, String ipDst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response insertRouteFile(String fileName, InputStream viDescription) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response insertRoute(String route) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String insertodl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String removeodl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getodl(String DPID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
