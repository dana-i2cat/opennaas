package org.opennaas.extensions.opendaylight.vtn.capability;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.opendaylight.vtn.model.Boundary;
import org.opennaas.extensions.opendaylight.vtn.model.BoundaryMap;
import org.opennaas.extensions.opendaylight.vtn.model.Link;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightvBridge;
import org.opennaas.extensions.opendaylight.vtn.model.PortMap;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.model.vBridgeInterfaces;
import org.opennaas.extensions.opendaylight.vtn.model.vLink;
import org.opennaas.extensions.opendaylight.vtn.protocol.OpenDaylightProtocolSession;
import org.opennaas.extensions.opendaylight.vtn.protocol.OpenDaylightProtocolSessionFactory;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.IOpenDaylightvtnAPIClient;

//import org.opennaas.extensions.openflowswitch.utils.Utils;
/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class VTNCapability implements IVTNCapability {

    Log log = LogFactory.getLog(VTNCapability.class);
    ProtocolSessionContext context;
    OpenDaylightProtocolSession session;
    IOpenDaylightvtnAPIClient client;
    private static final String SESSION_ID = "0001";
    private String PROTOCOL_URI = "http://192.168.254.72:8083/";

    public VTNCapability() {
        context = generateContext();
        try {
            session = (OpenDaylightProtocolSession) new OpenDaylightProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
            session.connect();
            client = session.getOpenDaylightClientForUse();
        } catch (ProtocolException ex) {
            Logger.getLogger(VTNCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Response createVTN(String name) {
        log.error("Create VTN " + name);

        VTN vtn = new VTN();
        vtn.setVtn_name(name);
        Response response = client.createVTN(vtn);
        return response;
    }

    private ProtocolSessionContext generateContext() {
        context = new ProtocolSessionContext();
        context.addParameter(ProtocolSessionContext.PROTOCOL, OpenDaylightProtocolSession.OPENDAYLIGHT_PROTOCOL_TYPE);
        context.addParameter(ProtocolSessionContext.PROTOCOL_URI, PROTOCOL_URI);
        context.addParameter(ProtocolSessionContext.AUTH_TYPE, "noauth");
        return context;
    }

    @Override
    public Response removeVTN(String name) {
        return client.deleteVTN(name);
    }

    @Override
    public Response coordinatorAddress(String address, String port) {
        PROTOCOL_URI = "http://" + address + ":" + port;
        generateContext();
        return Response.ok().build();
    }

    @Override
    public Response createController(String name, String ipaddr, String type) {
        log.error("Creating controller...");
        OpenDaylightController ctrl = new OpenDaylightController();
        ctrl.setController_id(name);
        ctrl.setIpaddr(ipaddr);
        ctrl.setType(type);
        ctrl.setVersion("1.0");
        ctrl.setAuditstatus("enable");
        Response response = client.createController(ctrl);
        return response;
    }

    @Override
    public Response createvBridge(String vtn_name, String name, String ctrl, String domain) {
        OpenDaylightvBridge vBridge = new OpenDaylightvBridge();
        vBridge.setVbr_name(name);
        vBridge.setController_id(ctrl);
        vBridge.setDomain_id(domain);
        vBridge.setVbr_name(name);
        Response response = client.createvBridge(vtn_name, vBridge);
        return response;
    }

    @Override
    public Response createInterfaces(String vtn_name, String vBridge, String iface) {
        vBridgeInterfaces inf = new vBridgeInterfaces();
        inf.setIf_name(iface);
        return client.createInterfaces(vtn_name, vBridge, inf);
    }

    @Override
    public Response createBoundary(String id, String ctrl1, String domain1, String port1, String ctrl2, String domain2, String port2) {
        Boundary bound = new Boundary();
        bound.setBoundary_id(id);
        Link link = new Link();
        link.setController1_id(ctrl1);
        link.setDomain1_id(domain1);
        link.setLogical_port1_id(port1);
        link.setController2_id(ctrl2);
        link.setDomain2_id(domain2);
        link.setLogical_port2_id(port2);
        bound.setLink(link);
        return client.createBoundary(bound);
    }

    @Override
    public Response test() {
        log.error("Start test");
        VTN vtn = new VTN();
        vtn.setVtn_name("vtnTest");
        Response response = client.createVTN(vtn);
log.error("VTN "+response.getStatus());
        OpenDaylightController ctrl1 = new OpenDaylightController();
        ctrl1.setController_id("ctrl1");
        ctrl1.setIpaddr("192.168.254.156");
        ctrl1.setType("odc");
        ctrl1.setVersion("1.0");
        ctrl1.setAuditstatus("enable");
        response = client.createController(ctrl1);
log.error("Controller - "+response.getStatus());
        OpenDaylightController ctrl2 = new OpenDaylightController();
        ctrl2.setController_id("ctrl2");
        ctrl2.setIpaddr("192.168.254.221");
        ctrl2.setType("odc");
        ctrl2.setVersion("1.0");
        ctrl2.setAuditstatus("enable");
        response = client.createController(ctrl2);
log.error("Controller2 - "+response.getStatus());

        OpenDaylightvBridge vBridge = new OpenDaylightvBridge();
        vBridge.setVbr_name("vbr1");
        vBridge.setController_id(ctrl1.getController_id());
        vBridge.setDomain_id("DEFAULT");
        response = client.createvBridge(vtn.getVtn_name(), vBridge);
log.error("Bridge1 "+response.getStatus());
        
        OpenDaylightvBridge vBridge2 = new OpenDaylightvBridge();
        vBridge2.setVbr_name("vbr2");
        vBridge2.setController_id(ctrl2.getController_id());
        vBridge2.setDomain_id("DEFAULT");
        response = client.createvBridge(vtn.getVtn_name(), vBridge2);
log.error("Bridge2 "+response.getStatus());
        vBridgeInterfaces inf = new vBridgeInterfaces();
        inf.setIf_name("if1");
        response = client.createInterfaces(vtn.getVtn_name(), vBridge.getVbr_name(), inf);
log.error("Interf "+response.getStatus());
        vBridgeInterfaces inf2 = new vBridgeInterfaces();
        inf2.setIf_name("if2");
        response =  client.createInterfaces(vtn.getVtn_name(), vBridge.getVbr_name(), inf2);
log.error("Interf2 "+response.getStatus());
        inf = new vBridgeInterfaces();
        inf.setIf_name("if1");
        response =  client.createInterfaces(vtn.getVtn_name(), vBridge2.getVbr_name(), inf);
log.error("Interf3 "+response.getStatus());        
         inf2 = new vBridgeInterfaces();
        inf2.setIf_name("if2");
        response =  client.createInterfaces(vtn.getVtn_name(), vBridge2.getVbr_name(), inf2);
log.error("Interf4 "+response.getStatus());
 
        Boundary bound = new Boundary();
        bound.setBoundary_id("b1");
        Link link = new Link();
        link.setController1_id(ctrl1.getController_id());
        link.setDomain1_id(vBridge.getDomain_id());
        link.setLogical_port1_id("PP-OF:00:00:00:00:00:00:00:01-s1-eth3");
        link.setController2_id(ctrl2.getController_id());
        link.setDomain2_id(vBridge2.getDomain_id());
        link.setLogical_port2_id("PP-OF:00:00:00:00:00:00:00:04-s4-eth3");
        bound.setLink(link);
        response = client.createBoundary(bound);
log.error("Boundary "+response.getStatus());
        

        vLink vlink = new vLink();
        vlink.setVlk_name("vlink1");
        vlink.setVnode1_name(vBridge.getVbr_name());
        vlink.setIf1_name(inf2.getIf_name());
        vlink.setVnode2_name(vBridge2.getVbr_name());
        vlink.setIf2_name(inf2.getIf_name());
        BoundaryMap boundMap = new BoundaryMap();
        boundMap.setBoundary_id(bound.getBoundary_id());
        boundMap.setVlan_id("50");
        vlink.setBoundaryMap(boundMap);
        response = client.createvLink(vtn.getVtn_name(), vlink);
log.error("vLink "+response.getStatus());

        PortMap portMap = new PortMap();
        portMap.setLogical_port_id("PP-OF:00:00:00:00:00:00:00:02-s2-eth2");
        response = client.configPortMap(vtn.getVtn_name(), vBridge.getVbr_name(), inf.getIf_name(), portMap);
log.error("portMap "+response.getStatus());
        portMap = new PortMap();
        portMap.setLogical_port_id("PP-OF:00:00:00:00:00:00:00:05-s5-eth2");
        response = client.configPortMap(vtn.getVtn_name(), vBridge2.getVbr_name(), inf.getIf_name(), portMap);
log.error("portMap2 "+response.getStatus());

        return response;
    }

    @Override
    public Response mapPort(String vtnName, String vBridge, String iface, String port) {
        PortMap portMap = new PortMap();
        portMap.setLogical_port_id(port);
        return client.configPortMap(vtnName, vBridge, iface, portMap);
    }

}
