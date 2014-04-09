package org.opennaas.extensions.opendaylight.vtn.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;
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
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.BoundaryWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.LogicalPortsOFFlowsWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgeInterfacesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vBridgesWrapper;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.wrappers.vLinksWrapper;

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
    private VTN vtn;
    private List<OpenDaylightController> controllers = new ArrayList<OpenDaylightController>();
    private List<Boundary> boundaries = new ArrayList<Boundary>();
    private Map<vBridgeInterfaces, String> mapPorts;

    public VTNCapability() {
        context = generateContext();

        try {
            session = (OpenDaylightProtocolSession) new OpenDaylightProtocolSessionFactory().createProtocolSession(SESSION_ID, context);
            session.connect();
            client = session.getOpenDaylightClientForUse();
        } catch (ProtocolException ex) {
            Logger.getLogger(VTNCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        initConfig();
    }

    private ProtocolSessionContext generateContext() {
        context = new ProtocolSessionContext();
        context.addParameter(ProtocolSessionContext.PROTOCOL, OpenDaylightProtocolSession.OPENDAYLIGHT_PROTOCOL_TYPE);
        context.addParameter(ProtocolSessionContext.PROTOCOL_URI, PROTOCOL_URI);
        context.addParameter(ProtocolSessionContext.AUTH_TYPE, "noauth");
        return context;
    }
    
    private void initConfig() {
        vtn = new VTN();
        vtn.setVtn_name("vtn1");
        createVTN(vtn.getVtn_name());

        createController("ctrl1", "192.168.254.156", "odc");
        createController("ctrl2", "192.168.254.221", "odc");

        log.error("Register Bridges");
        vBridgesWrapper vbrs = getvBridges(vtn.getVtn_name());//get list
        if (vbrs.size() > 1) {
            for (OpenDaylightvBridge vbr : vbrs) {
                vtn.getvBridges().add(getvBridge(vtn.getVtn_name(), vbr.getVbr_name()));
            }
        } else {
            createvBridge(vtn.getVtn_name(), "vbr1", controllers.get(0).getController_id(), "DEFAULT");
            createvBridge(vtn.getVtn_name(), "vbr2", controllers.get(1).getController_id(), "DEFAULT");
        }
        log.error("Register interfaces...");
        vBridgeInterfacesWrapper ifaces;
        for (OpenDaylightvBridge vbr : vtn.getvBridges()) {
            ifaces = getInterfaces(vtn.getVtn_name(), vbr.getVbr_name());
            if (ifaces.size() > 1) {
                for (vBridgeInterfaces iface : ifaces) {
                    vbr.getIface().add(iface);
                }
            } else {
//                List<vBridgeInterfaces> ints = getPossibleInts(vbr.getVbr_name(), vbr.getController_id());
                int numInt = getNumInts(vbr.getController_id());
                for (int i = 1; i < numInt + 1; i++) {
                    String ifName = "if" + i;
                    createInterfaces(vtn.getVtn_name(), vbr.getVbr_name(), ifName);
                }
            }
        }

        log.error("Creating boundary");
        BoundaryWrapper bds = getBoundaries();//get list
        if (bds.size() > 0) {
            for (Boundary bound : bds) {
                boundaries.add(getBoundary(bound.getBoundary_id()));
            }
        } else {
            String borderPort1 = "PP-OF:00:00:00:00:00:00:00:01-s1-eth3";
            String borderPort2 = "PP-OF:00:00:00:00:00:00:00:04-s4-eth3";
            createBoundary("b1", controllers.get(0).getController_id(), "DEFAULT", borderPort1, controllers.get(1).getController_id(), "DEFAULT", borderPort2);
        }

        log.error("Creating vLinks");
        vLinksWrapper vLinks = getvLinks(vtn.getVtn_name());//get list
        if (vLinks.size() > 0) {
            for (vLink vlk : vLinks) {
                vtn.getVlink().add(getvLink(vtn.getVtn_name(), vlk.getVlk_name()));
            }
        } else {
            createvLink(vtn.getVtn_name(), "vlink1", "vbr1", "if1", "vbr2", "if1", "b1", "50");
        }

        log.error("------------- VTN summary -------------");
        log.error("List of vbr (" + vtn.getvBridges().size() + ")");
        for (OpenDaylightvBridge vbr : vtn.getvBridges()) {
            log.error("In vBridge " + vbr.getVbr_name() + ". List of ifaces (" + vbr.getIface().size() + ")");
            for (vBridgeInterfaces iface : vbr.getIface()) {
//                log.error("Iface: "+iface.getIf_name());
            }
        }
        log.error("Number of boundaries: " + boundaries.size());
        if (boundaries.size() > 0) {
            log.error("Bound: " + boundaries.get(0).getBoundary_id());
            log.error("Bound: " + boundaries.get(0).getLink().getController1_id());
            log.error("Bound: " + boundaries.get(0).getLink().getController2_id());
        }
        log.error("Number of vLinks: " + vtn.getVlink().size());
        if (vtn.getVlink().size() > 0) {
            log.error("vLink: " + vtn.getVlink().get(0).getVlk_name());
            log.error("vLink: " + vtn.getVlink().get(0).getVnode1_name());
            log.error("vLink: " + vtn.getVlink().get(0).getVnode2_name());
            log.error("vLink: " + vtn.getVlink().get(0).getBoundaryMap().getBoundary_id());
        }
        log.error("Assign each iface to a physical port...");
       checkPortMap();
    }

    @Override
    public Response createVTN(String name) {
        log.error("Create VTN " + name);

        vtn = new VTN();
        vtn.setVtn_name(name);
        Response response = client.createVTN(vtn);
        return response;
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
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            controllers.add(ctrl);
        }
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
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            if (!vtn.getvBridges().contains(vBridge)) {
                vtn.getvBridges().add(vBridge);
            }
        }
        return response;
    }

    @Override
    public Response createInterfaces(String vtn_name, String vBridge, String iface) {
        vBridgeInterfaces inf = new vBridgeInterfaces();
        inf.setIf_name(iface);
        Response response = client.createInterfaces(vtn_name, vBridge, inf);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            for (OpenDaylightvBridge vbr : vtn.getvBridges()) {
                if (vbr.getVbr_name().equals(vBridge)) {
                    if (!vbr.getIface().contains(inf)) {
                        vbr.getIface().add(inf);
                    }
                }
            }
        }
        return response;
    }

    @Override
    public Response createBoundary(String id, String ctrl1, String domain1, String port1, String ctrl2, String domain2, String port2) {
        Response response;
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
        response = client.createBoundary(bound);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            boundaries.add(bound);
        }
        return response;
    }

    @Override
    public Response createvLink(String vtnName, String vlinkName, String vnode1, String if1, String vnode2, String if2, String boundId, String vlanId) {
        vLink vlink = new vLink();
        vlink.setVlk_name(vlinkName);
        vlink.setVnode1_name(vnode1);
        vlink.setIf1_name(if1);
        vlink.setVnode2_name(vnode2);
        vlink.setIf2_name(if2);
        BoundaryMap boundMap = new BoundaryMap();
        boundMap.setBoundary_id(boundId);
        boundMap.setVlan_id(vlanId);
        vlink.setBoundaryMap(boundMap);
        Response response = client.createvLink(vtnName, vlink);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            if (!vtn.getVlink().contains(vlink)) {
                vtn.getVlink().add(vlink);
            }
        }
        return response;
    }

    @Override
    public Response mapPort(String vtnName, String vBridge, String iface, String port) {
        PortMap portMap = new PortMap();
        portMap.setLogical_port_id(port);
        return client.configPortMap(vtnName, vBridge, iface, portMap);
    }

    @Override
    public Response test() {
        log.error("Start test");
        String vBridge1 = "vbr1";
        String vBridge2 = "vbr2";
        String iface1 = "if1";
        String iface2 = "if2";
        VTN vtn = new VTN();
        vtn.setVtn_name("vtnTest");
        Response response = client.createVTN(vtn);
        log.error("VTN " + response.getStatus());

        response = createController("ctrl1", "192.168.254.156", "odc");
        log.error("Controller - " + response.getStatus());
        response = createController("ctrl2", "192.168.254.221", "odc");
        log.error("Controller2 - " + response.getStatus());

        response = createvBridge(vtn.getVtn_name(), vBridge1, "ctrl1", "DEFAULT");
        log.error("Bridge1 " + response.getStatus());
        response = createvBridge(vtn.getVtn_name(), vBridge2, "ctrl2", "DEFAULT");
        log.error("Bridg2 " + response.getStatus());

        response = createInterfaces(vtn.getVtn_name(), vBridge1, "if1");
        log.error("Interf " + response.getStatus());
        response = createInterfaces(vtn.getVtn_name(), vBridge1, "if2");
        log.error("Interf2 " + response.getStatus());
        response = createInterfaces(vtn.getVtn_name(), vBridge2, "if1");
        log.error("Interf3 " + response.getStatus());
        response = createInterfaces(vtn.getVtn_name(), vBridge2, "if2");
        log.error("Interf4 " + response.getStatus());

        String port1 = "PP-OF:00:00:00:00:00:00:00:01-s1-eth3";
        String port2 = "PP-OF:00:00:00:00:00:00:00:04-s4-eth3";
        response = createBoundary("b1", "ctrl1", "DEFAULT", port1, "ctrl2", "DEFAULT", port2);
        log.error("Boundary " + response.getStatus());

        response = createvLink(vtn.getVtn_name(), "vlink1", "vbr1", "if2", "vbr2", "if2", "b1", "50");
        log.error("vLink " + response.getStatus());

        String port = "PP-OF:00:00:00:00:00:00:00:02-s2-eth2";
        response = mapPort(vtn.getVtn_name(), vBridge1, iface1, port);
        log.error("portMap " + response.getStatus());
        port = "PP-OF:00:00:00:00:00:00:00:05-s5-eth2";
        response = mapPort(vtn.getVtn_name(), vBridge2, iface1, port);
        log.error("portMap2 " + response.getStatus());

        return response;
    }

    @Override
    public Response ipreq(String srcDPID, String inPort, String dstDPID, String dstPort) {
        log.error("IP REQUEST FROM VRF");
        log.error(srcDPID + " " + inPort + " " + dstDPID + " " + dstPort);
        Response response;

        if (vtn == null) {
            return Response.status(500).entity("VTN no exists. Please create a VTN").build();
        }
        if (dstPort == null) {
            return Response.accepted("Error. Destination port is null").build();
        }

        //configure map-ports
        String Sw_num = "s" + srcDPID.substring(srcDPID.length() - 1);
        String port = "PP-OF:" + srcDPID + "-s" + Sw_num + "-eth" + inPort;
//        response = mapPort(vtn.getVtn_name(), "vbr1", "if1", port);
//        log.error("portMap " + response.getStatus());
        String dstSw_num = "s" + dstDPID.substring(dstDPID.length() - 1);
        port = "PP-OF:" + dstDPID + "-" + dstSw_num + "-eth" + dstPort;
        log.error("Port: " + port);
//        port = "PP-OF:00:00:00:00:00:00:00:05-s5-eth2";
        String iface = "if2";
        for (vBridgeInterfaces inf : vtn.getvBridges().get(1).getIface()) {
            for (PortMap pm : inf.getPortMaps()) {
                if (pm.getLogical_port_id() != null) {
                    if (pm.getLogical_port_id().equals(port)) {
                        iface = inf.getIf_name();
                        log.error("Set Iface: " + iface);
                        break;
                    }
                }
            }
        }
        log.error("iface: " + iface + " Port: " + port);
        response = mapPort(vtn.getVtn_name(), "vbr2", iface, port);
        log.error("Req to ODL VTN - portMap: " + response.getStatus());

        return response;
    }

    @Override
    public PortMap mapPort(String vtnName, String vBridge, String iface) {
        return client.configPortMap(vtnName, vBridge, iface);
    }

    @Override
    public LogicalPortsOFFlowsWrapper getLogicalPorts(String ctrl, String domain) {
        LogicalPortsOFFlowsWrapper lp = client.getLogicalPorts(ctrl, "(" + domain + ")");
        if (lp.size() > 0) {
            log.error(lp.get(0).getLogical_port_id());
            log.error(lp.get(0).getSwitch_id());
            log.error("Number of logical ports: " + lp.size());
        }
        return lp;
    }

    /**
     * Analyze the vbr of OpenNaaS and assign the mapping(port-interface) of
     * each interface that contains each vbridge
     */
    @Override
    public void checkPortMap() {
        log.error("Assing Port-Map Interface of each vBridge");
        //for each vBridge and each interface check the mapping port
        PortMap pMap;
        for (OpenDaylightvBridge vbr : vtn.getvBridges()) {
            for (vBridgeInterfaces inf : vbr.getIface()) {
                pMap = client.configPortMap(vtn.getVtn_name(), vbr.getVbr_name(), inf.getIf_name());
                inf.getPortMaps().add(pMap);
                log.error("Iface " + inf.getIf_name() + " from " + vbr.getVbr_name() + ". AddingMap " + pMap.getLogical_port_id());
            }
        }
    }

    @Override
    public OpenDaylightvBridge getvBridge(String vtnName, String vBridge) {
        OpenDaylightvBridge vbr = client.getvBridge(vtnName, vBridge);
        log.error("VRB: " + vbr.getVbr_name() + ",  " + vbr.getController_id());
        return vbr;
    }

    @Override
    public vBridgesWrapper getvBridges(String vtnName) {
        vBridgesWrapper vbrs = client.getvBridges("vtn1");
        return vbrs;
    }

    @Override
    public vBridgeInterfacesWrapper getInterfaces(String vtnName, String vBridge) {
        vBridgeInterfacesWrapper ifaces = client.getInterfaces(vtnName, vBridge);
        return ifaces;
    }

    @Override
    public Boundary getBoundary(String bound) {
        return client.getBoundary(bound);
    }

    @Override
    public BoundaryWrapper getBoundaries() {
        return client.getBoundaries();
    }

    @Override
    public vLink getvLink(String vtnName, String bound) {
        return client.getvLink(vtnName, bound);
    }

    @Override
    public vLinksWrapper getvLinks(String vtnName) {
        return client.getvLinks(vtnName);
    }
    /*
     private List<vBridgeInterfaces> getPossibleInts(String vbr_name, String controller_id) {
     LogicalPortsOFFlowsWrapper resp = getLogicalPorts(controller_id, "DEFAULT");
     for (OpenDaylightvBridge vbr : vtn.getvBridges()) {
     if (vbr_name.equals(vbr.getVbr_name())) {
     for (LogicalPort lp : resp) {
     lp.
     }
     }
     }

     }
     */

    private int getNumInts(String controller_id) {
        LogicalPortsOFFlowsWrapper resp = getLogicalPorts(controller_id, "DEFAULT");
        return resp.size();
    }

    @Override
    public Response callStatic() {
        log.error("Call static");
        String url = "http://localhost:8888/opennaas/vrf/routing/routeMode";
        Response response;
        String username = "admin";
        String password = "123456";
        String base64encodedUsernameAndPassword = base64Encode(username + ":" + password);

        WebClient client = WebClient.create(url);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);
        client.accept(MediaType.TEXT_PLAIN);
        response = client.get();
        log.error(response.getStatus());
        log.error(response.getMetadata());

        return response;
    }

    private static String base64Encode(String stringToEncode) {
        return DatatypeConverter.printBase64Binary(stringToEncode.getBytes());
    }

}
