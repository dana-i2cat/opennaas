package org.opennaas.extensions.vrf.capability.routing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.vrf.model.VRFModel;
import org.opennaas.extensions.vrf.model.Route;
import org.opennaas.extensions.vrf.model.RoutingTable;
import org.opennaas.extensions.vrf.model.Switch;
import org.opennaas.extensions.vrf.utils.Utils;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class RoutingCapability extends AbstractCapability implements IRoutingCapability {

    public static String CAPABILITY_TYPE = "routing";
    Log log = LogFactory.getLog(RoutingCapability.class);
    private String resourceId = "";

    public RoutingCapability(CapabilityDescriptor descriptor, String resourceId) {

        super(descriptor);
        this.resourceId = resourceId;
        log.debug("Built new Routing Capability");
    }

    @Override
    public void activate() throws CapabilityException {
        registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IRoutingCapability.class.getName());
        super.activate();
    }

    @Override
    public void deactivate() throws CapabilityException {
        unregisterService();
        super.deactivate();
    }

    @Override
    public String getCapabilityName() {
        return CAPABILITY_TYPE;
    }

    @Override
    public IActionSet getActionSet() throws CapabilityException {
        String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
        String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

        try {
            return Activator.getRoutingActionSetService(name, version);
        } catch (ActivatorException e) {
            throw new CapabilityException(e);
        }
    }

    @Override
    public void queueAction(IAction action) throws CapabilityException {
        getQueueManager(resourceId).queueAction(action);
    }

    /**
     *
     * @return QueuemanagerService this capability is associated to.
     * @throws CapabilityException if desired queueManagerService could not be
     * retrieved.
     */
    private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
        try {
            return Activator.getQueueManagerService(resourceId);
        } catch (ActivatorException e) {
            throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
        }
    }

    @Override
    public Response getRoute(String ipSource, String ipDest, String switchDPID, int inputPort, boolean proactive) throws CapabilityException {
        int outPortSrcSw;
        int version;//IP version
        if (Utils.isIPv4Address(ipSource) && Utils.isIPv4Address(ipDest)) {
            ipSource = Utils.fromIPv4Address(Integer.parseInt(ipSource));
            ipDest = Utils.fromIPv4Address(Integer.parseInt(ipDest));
            version = 4;
        } else if(Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            ipSource = Utils.tryToCompressIPv6(ipSource);
            ipDest = Utils.tryToCompressIPv6(ipDest);
            version = 6;
        } else{
            return Response.serverError().entity("Ip not recognized").build();
        }

        VRFModel model = (VRFModel) resource.getModel();
        if (model.getTable(version) == null) {
            return Response.status(404).type("text/plain").entity("Table does not exist.").build();
        }

        log.info("Requested route: " + ipSource + " > " + ipDest + " " + switchDPID + ", inPort: " + inputPort);
        Switch switchInfo = new Switch(inputPort, switchDPID);

        Route route = new Route(ipSource, ipDest, switchInfo);
        int routeId = model.getTable(version).RouteExists(route);
        String subIpSrc;
        String subIpDest;
        if (routeId != 0) {
            outPortSrcSw = model.getTable(version).getOutputPort(routeId);
            subIpSrc = model.getTable(version).getRouteId(routeId).getSourceAddress();
            subIpDest = model.getTable(version).getRouteId(routeId).getDestinationAddress();
        } else {
            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }

        if (proactive) {
            proactiveRouting(ipSource, subIpSrc, ipDest, subIpDest, switchInfo, route, version);
        }
        return Response.ok(Integer.toString(outPortSrcSw) + ":" + ipDest).build();
    }

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) throws CapabilityException {
        log.info("Insert route. Src: " + ipSource + " Dst: " + ipDest + " In: " + inputPort + " Out: " + outputPort);
        VRFModel model = (VRFModel) resource.getModel();

        int version = 0;
        if (Utils.isIpAddress(ipSource) == 4 && Utils.isIpAddress(ipDest) == 4) {
            version = 4;
        } else if (Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            version = 6;
        } else {
            log.error("IP version error. The detected version is: " + version);
            return Response.status(403).type("text/plain").entity("The IP version is not detected. Analyze the IP.").build();
        }
        if (model.getTable(version) == null) {
            return Response.status(403).type("text/plain").entity("IPv" + version + " table does not exist.").build();
        }
        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchDPID.isEmpty() && inputPort != 0 && outputPort != 0) {
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchDPID);
            Route route = new Route(ipSource, ipDest, switchInfo);

            String response = model.getTable(version).addRoute(route);
            return Response.status(201).entity(response).build();
        }
        return Response.status(403).type("text/plain").entity("Some value is empty").build();
    }

    @Override
    public Response removeRoute(int id, int version) throws CapabilityException {
        log.info("Removing route "+id+" from table IPv"+version);
        VRFModel model = (VRFModel) resource.getModel();
        Route route = model.getTable(version).getRouteId(id);

        int prefix = Utils.isSubnetAddress(route.getDestinationAddress());
        StringBuilder strIp = new StringBuilder();
        StringBuilder strArp = new StringBuilder();
        if(prefix == 0){
            strIp.append("ip4to-mod-");
            strArp.append("arpto-mod-");
        }else{
            strIp.append("ip4in-mod-");
            strArp.append("arpin-mod-");
        }
        strIp.append(route.getDestinationAddress());
        strArp.append(route.getDestinationAddress());

        model.getTable(version).removeRoute(id);
        log.debug("Remove flow " + id + " " + strIp);
        String flow = "{\"name\":\"" + strIp + "\"}";
        String flow2 = "{\"name\":\"" + strArp + "\"}";

        log.debug("Flows: " + flow + "flow2: " + flow2);
        String uri;
        try{
            String controllerInfo = model.getSwitchController().get(route.getSwitchInfo().getMacAddress());
            uri = "http://" + controllerInfo;
        }catch (NullPointerException e){
            return Response.serverError().entity("Url of the controller not found.").build();
        }
        String response = Utils.removeHttpRequest(uri, flow, strIp.toString());
        log.debug(response);
        response = Utils.removeHttpRequest(uri, flow2, strArp.toString());
        log.debug(response);
//error in the response?
        return Response.ok("Removed").build();
    }

    @Override
    public Response removeRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) throws CapabilityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Response removeRoutes() throws CapabilityException {
        VRFModel model = (VRFModel) resource.getModel();
        model.getIpv4().removeRoutes();
        model.getIpv6().removeRoutes();

        Map<String, String> listSwitches = model.getSwitchController();
        for (Entry<String, String> e: listSwitches.entrySet()) {
            log.debug("["+e.getKey() + "=" + e.getValue()+"]");
            String uri = "http://" + e.getValue();
            String response = Utils.removeAllHttpRequest(uri, e.getKey());
            log.debug(response);
        }
        return Response.ok("Removed").build();
    }

    @Override
    public Response getRoutes() throws CapabilityException {
        log.info("Get entire Model");
        VRFModel model = (VRFModel) resource.getModel();

        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model);
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    @Override
    public Response getRoutes(int version) throws CapabilityException {
        log.info("Get entire Route Table of version IPv" + version);
        VRFModel model = (VRFModel) resource.getModel();
        if (model.getTable(version) == null) {
            model.setTable(new RoutingTable(version), version);
        }
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getTable(version));
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    @Override
    public Response insertRouteFile(String filename, InputStream file) throws CapabilityException {
        log.info("Insert Routes from File");
        VRFModel model = (VRFModel) resource.getModel();
        String response = "Inserted";
        try {
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createJsonParser(new File(filename));
            JsonToken current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                log.error("Error: root should be object: quiting.");
                return Response.status(404).entity("Error: root should be object: quiting.").build();
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                current = jp.nextToken();// move from field name to field value
                if (fieldName.equals("routeIPv4")) {
                    if (model.getTable(4) == null) {
                        model.setTable(new RoutingTable(4), 4);
                    }
                    if (current == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            // read the record into a tree model,
                            // this moves the parsing position to the end of it
                            JsonNode node = jp.readValueAsTree();
                            String field = jp.getCurrentName();
                            // And now we have random access to everything in the object
                            Route newRoute = new Route();
                            Switch newSwitch = new Switch();
                            newRoute.setSourceAddress(node.get("srcAddr").getValueAsText());
                            newRoute.setDestinationAddress(node.get("dstAddr").getValueAsText());
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            model.getTable(4).addRoute(newRoute);
                        }
                    } else {
                        response = "Error: records should be an array: skipping.";
                        log.error(response);
                        jp.skipChildren();
                    }
                } else if (fieldName.equals("routeIPv6")) {
                    if (current == JsonToken.START_ARRAY) {
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            JsonNode node = jp.readValueAsTree();
                            String field = jp.getCurrentName();
                            Route newRoute = new Route();
                            Switch newSwitch = new Switch();
                            newRoute.setSourceAddress(node.get("srcAddr").getValueAsText());
                            newRoute.setDestinationAddress(node.get("dstAddr").getValueAsText());
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            model.getTable(6).addRoute(newRoute);
                        }
                    } else {
                        response = "Error: records should be an array: skipping.";
                        log.error(response);
                        jp.skipChildren();
                    }
                } else {
                    response = "Unprocessed property: " + fieldName;
                    log.error(response);                    
                    jp.skipChildren();
                }
            }
            return Response.ok(response).build();

        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(404).entity("Some error. Check the file. Possible error: "+response).build();
    }

    @Override
    public Response getControllerStatus(String ip) throws CapabilityException {
        ip = ip.replace("-", ".").replace("%3A", ":");
        log.info("Request Controller status witht he following IP: " + ip);
        String status = "Online";
        try {
            String Url = "http://" + ip + "/wm/core/controller/switches/json";
            URLConnection connection = new URL(Url).openConnection();
            //connection.setRequestProperty("Accept-Charset", charset);
            connection.getInputStream();
            log.info("Controller "+ip+ " Status: "+status);
            return Response.ok(status).build();
        } catch (IOException ex) {
            status = "Offline";
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            log.info("Controller "+ip+ " Status: "+status);
            return Response.ok(status).build();
        }
    }

    @Override
    public Response putSwitchController(String ipController, String portController, String switchDPID) throws CapabilityException {
        log.info("Put Switch-Controller info into HashMap");
        VRFModel model = (VRFModel) resource.getModel();
        try{
            model.getSwitchController().put(switchDPID, ipController + ":" + portController);
        }catch(Exception e){
            return Response.serverError().entity("Error, expection: "+e.getMessage()).build();
        }
        return Response.ok("Inserted correctly.").build();
    }

    @Override
    public Response getControllersInfo() throws CapabilityException {
        VRFModel model = (VRFModel) resource.getModel();
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getSwitchController());
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    public class MyThread extends Thread {
        private int i;
        private String Url;
        private String[] json;

        public MyThread(int i, String Url, String[] json) {
            this.i = i;
            this.Url = Url;
            this.json = json;
        }
        @Override
        public void run() {
            long initialTime = System.currentTimeMillis();
            log.debug("try to send " + i);
            Utils.putFlowHttpRequest(Url, json[i]);
            long midTime = System.currentTimeMillis() - initialTime;
            log.debug("GetResponse" + i + "... " + midTime + "Initial: " + initialTime);
        }
    }

    private Response proactiveRouting(String ipSource, String subIpSrc, String ipDest, String subIpDest, Switch srcSwInfo, Route route, int version) throws CapabilityException {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        VRFModel model = (VRFModel) resource.getModel();
        String controllerInfo;
        Switch destSwInfo = null;
        try {
            destSwInfo = model.getTable(version).getDestinationSwitch(ipSource, ipDest, srcSwInfo);
            controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
        } catch (NullPointerException e) {
            log.error("Null Switch Info. No information about the controller. Stop find route... "+e.getMessage());
            return Response.serverError().entity("No information about the controller. Stop find route...").build();
        }
        Route route1 = new Route(subIpSrc, ipDest, destSwInfo);
        int routeId = model.getTable(version).RouteExists(route1);

        int outPortDstSw;
        if (routeId != 0) {
            outPortDstSw = model.getTable(version).getOutputPort(routeId);
        } else {
            outPortDstSw = 0;
            log.error("No output port in the last Switch.");
//            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }
        log.debug("Output port of the last Switch: " + outPortDstSw);

        final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
        String json[] = new String[4];
        String ethertype = "";
        String ethertype2 = "";
        if (version == 4) {
            ethertype = "0x806";
            ethertype2 = "0x800";
        } else if (version == 6) {
            ethertype = "0x86DD";
        }
        log.info("Origin Switch: " + srcSwInfo.getMacAddress() + ", Dest Switch: " + destSwInfo.getMacAddress());
        //Insert flows to the destination switch
        if (!destSwInfo.getMacAddress().equals(srcSwInfo.getMacAddress())) {
            json[0] = Utils.createJson("arpin-mod-" + ipDest, destSwInfo.getMacAddress(), ipDest, ethertype, destSwInfo.getInputPort(), outPortDstSw);
            json[1] = Utils.createJson("arpto-mod-" + ipSource, destSwInfo.getMacAddress(), ipSource, ethertype, outPortDstSw);
            json[2] = Utils.createJson("ip4in-mod-" + ipDest, destSwInfo.getMacAddress(), ipDest, ethertype2, destSwInfo.getInputPort(), outPortDstSw);
            json[3] = Utils.createJson("ip4to-mod-" + ipSource, destSwInfo.getMacAddress(), ipSource, ethertype2, outPortDstSw);
            try {
                log.debug("Inserting flows in the destination switch...");
                for (int i = 0; i < json.length; i++) {
                    Utils.putFlowHttpRequest(Url, json[i]);
                }
            } catch (Exception e) {
            }
        }

        log.info("Find routes in other Switches... Switches in the middle. Scenario 2 and 3");
        List<Route> routeSubnetList = model.getTable(version).listOtherRoutes(route, srcSwInfo, destSwInfo);
        if (routeSubnetList.size() > 1) {
            for (Route r : routeSubnetList) {
                log.info("Send a proactive message");
                destSwInfo = r.getSwitchInfo();
                controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
                String Url2 = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
                json[0] = Utils.createJson("arpto-mod-" + r.getDestinationAddress(), r.getSwitchInfo().getMacAddress(), r.getDestinationAddress(), ethertype,  r.getSwitchInfo().getOutputPort()); 
                json[1] = Utils.createJson("ip4to-mod-" + r.getDestinationAddress(), r.getSwitchInfo().getMacAddress(), r.getDestinationAddress(), ethertype2,  r.getSwitchInfo().getOutputPort()); 
                try {
                    for (int i = 0; i < json.length; i++) {
                        Utils.putFlowHttpRequest(Url2, json[i]);
                    }
                } catch (Exception e) {
                }
            }
        }
        return Response.ok("Proactive messages sent.").build();
    }
    
    @Override
    public String getLog() throws CapabilityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
