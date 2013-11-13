package org.opennaas.extensions.rfv.capability.routing;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
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
import org.opennaas.extensions.rfv.model.Route;
import org.opennaas.extensions.rfv.model.Switch;
import org.opennaas.extensions.rfv.utils.Utils;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.rfv.model.RFVModel;
import org.opennaas.extensions.rfv.model.RoutingTable;

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
    public Response getRoute(String ipSource, String ipDest, String switchMac, int inputPort, boolean proactive) throws CapabilityException {
        int outPortSW1 = 0;
        int version = 0;//IP version
        if (Utils.isIPv4Address(ipSource)) {
            ipSource = Utils.fromIPv4Address(Integer.parseInt(ipSource));
            ipDest = Utils.fromIPv4Address(Integer.parseInt(ipDest));
            version = 4;
        } else {
            ipSource = Utils.tryToCompressIPv6(ipSource);
            ipDest = Utils.tryToCompressIPv6(ipDest);
            version = 6;
        }

        RFVModel model = (RFVModel) resource.getModel();
        if (model.getTable(version) == null) {
            return Response.status(404).type("text/plain").entity("Table does not exist.").build();
        }

        log.info("Requested route: " + ipSource + " > " + ipDest + " " + switchMac + ", inPort: " + inputPort);
        Switch switchInfo = new Switch(inputPort, switchMac);

        Route route = new Route(ipSource, ipDest, switchInfo);
        int routeId = model.getTable(version).RouteExists(route);
        String subIpSrc ="";
        String subIpDest ="";
        if (routeId != 0) {
            outPortSW1 = model.getTable(version).getOutputPort(routeId);
            subIpSrc = model.getTable(version).getRouteId(routeId).getSourceAddress();
            subIpDest = model.getTable(version).getRouteId(routeId).getDestinationAddress();
        } else {
            outPortSW1 = 0;
            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }

        if (proactive) {
            proactiveRouting(ipSource, subIpSrc, ipDest, subIpDest, switchInfo, route, version);
        }
        return Response.ok(Integer.toString(outPortSW1) + ":" + ipDest).build();
    }

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchMac, int inputPort, int outputPort) throws CapabilityException {
        log.info("Insert route. Src: " + ipSource + " Dst: " + ipDest + " In: " + inputPort + " Out: " + outputPort);
        RFVModel model = (RFVModel) resource.getModel();

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
        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && inputPort != 0 && outputPort != 0) {
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchMac);
            Route route = null;
            route = new Route(ipSource, ipDest, switchInfo);

            String response = model.getTable(version).addRoute(route);
            return Response.status(201).entity(response).build();
        }
        return Response.status(403).type("text/plain").entity("Some value is empty").build();
    }

    @Override
    public Response removeRoute(int id, int version) throws CapabilityException {
        RFVModel model = (RFVModel) resource.getModel();
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
//                model.getTable().delRoute(route, version);
        String flow = "{\"name\":\"" + strIp + "\"}";
        String flow2 = "{\"name\":\"" + strArp + "\"}";

        log.debug("Flows: " + flow + "flow2: " + flow2);
        String uri ="";
        try{
        String controllerInfo = model.getSwitchController().get(route.getSwitchInfo().getMacAddress());
        uri = "http://" + controllerInfo;
        }catch (NullPointerException e){
            return Response.serverError().entity("Url of the controller not found.").build();
        }
        String response = deleteHttpRequest(uri, flow);
        log.debug(response);
        response = deleteHttpRequest(uri, flow2);
        log.debug(response);
//error in the response?
        return Response.ok("Removed").build();
    }

    @Override
    public Response removeRoute(String ipSource, String ipDest, String switchMac, int inputPort, int outputPort) throws CapabilityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Response removeRoutes() throws CapabilityException {
        RFVModel model = (RFVModel) resource.getModel();
        model.getIpv4().removeRoutes();
        model.getIpv6().removeRoutes();

        String uri ="";
        Map<String, String> listSwitches = model.getSwitchController();
        for (Entry<String, String> e: listSwitches.entrySet()) {
            log.debug("["+e.getKey() + "=" + e.getValue()+"]");
            uri = "http://" + e.getValue();
            String response = clearStaticFlows(uri, e.getKey());
        }
        return Response.ok("Removed").build();
    }

    @Override
    public Response getRoutes() throws CapabilityException {
        log.info("Get entire Model");
        RFVModel model = (RFVModel) resource.getModel();

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
    public Response getRoutes(int type) throws CapabilityException {
        log.info("Get entire Route Table of version " + type);
        RFVModel model = (RFVModel) resource.getModel();
        if (model.getTable(type) == null) {
            model.setTable(new RoutingTable(type), type);
        }
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getTable(type));
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    @Override
    public Response insertRouteFile(String filename) throws CapabilityException {
        log.info("Insert Routes from File");
        RFVModel model = (RFVModel) resource.getModel();
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
                            log.debug(node.get("srcAddr").getValueAsText());
                            log.debug(node.get("dstAddr").getValueAsText());
                            newRoute.setSourceAddress(node.get("srcAddr").getValueAsText());
                            newRoute.setDestinationAddress(node.get("dstAddr").getValueAsText());
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            model.getTable(4).addRoute(newRoute);
                        }
                    } else {
                        log.error("Error: records should be an array: skipping.");
                        response = "Error: records should be an array: skipping.";
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
                        log.error("Error: records should be an array: skipping.");
                        response = "Error: records should be an array: skipping.";
                        jp.skipChildren();
                    }
                } else {
                    log.error("Unprocessed property: " + fieldName);
                    response = "Unprocessed property: " + fieldName;
                    jp.skipChildren();
                }
            }
            return Response.ok(response).build();

        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(404).entity("Some error. Check it.").build();
    }

    @Override
    public Response getControllerStatus(String ip) throws CapabilityException {
        ip = ip.replace("-", ".");
        ip = ip.replace("%3A", ":");
        log.info("Request Controller status witht he following IP: " + ip);
        String status = "Online";
        try {
            String Url = "http://" + ip + "/wm/core/controller/switches/json";
            URLConnection connection = new URL(Url).openConnection();
            //connection.setRequestProperty("Accept-Charset", charset);
            connection.getInputStream();
            log.info("Status: "+status);
            return Response.ok(status).build();
        } catch (IOException ex) {
            status = "Offline";
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            log.info("Status: "+status);
            return Response.ok(status).build();
        }
    }

    @Override
    public Response putSwitchController(String ipController, String portController, String switchMac) throws CapabilityException {
        log.info("Put Switch-Controller info into table");
        RFVModel model = (RFVModel) resource.getModel();

        try{
        model.getSwitchController().put(switchMac, ipController + ":" + portController);
        }catch(Exception e){
            return Response.serverError().entity("Error, expection: "+e.getMessage()).build();
        }
        return Response.ok("Inserted correctly.").build();
    }

    @Override
    public Response getControllersInfo() throws CapabilityException {
        RFVModel model = (RFVModel) resource.getModel();
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getSwitchController());
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    private String deleteHttpRequest(String uri, String flow) {
        String response = "";
        try {
            OutputStreamWriter wr = null;
            URL url = new URL(uri + "/wm/staticflowentrypusher/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // override HTTP method allowing sending body
            connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
            connection.setDoOutput(true);

            // prepare body
            wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(flow);
            wr.flush();
            wr.close();
            // get HTTP Response
            response = IOUtils.toString(connection.getInputStream(), "UTF-8");
            if (!response.equals("{\"status\" : \"Entry " + flow + " deleted\"}")) {
                try {
                    throw new Exception("Invalid response: " + response);
                } catch (Exception ex) {
                    Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException e) {
            
        }
        return response;
    }
    
    private String clearStaticFlows(String uri, String dpid) {
        String response = "";
        try {
            OutputStreamWriter wr = null;
            URL url = new URL(uri + "/wm/staticflowentrypusher/clear/"+dpid+"/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // prepare body
            wr = new OutputStreamWriter(connection.getOutputStream());
            wr.flush();
            wr.close();
            // get HTTP Response
            response = IOUtils.toString(connection.getInputStream(), "UTF-8");
            log.error("Response: "+response);
        } catch (IOException e) {
            
        }
        return response;
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
            httpRequest(Url, json[i]);
            long midTime = System.currentTimeMillis() - initialTime;
            log.debug("GetResponse" + i + "... " + midTime + "Initial: " + initialTime);
        }
    }

    private void httpRequest(String Url, String json) {
        try {
            log.debug("Request to: " + Url);
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json);
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
        } catch (UnknownHostException e) {
            log.error("Url is null. Maybe the controllers are not registred.");
        } catch (Exception ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Response proactiveRouting(String ipSource, String subIpSrc, String ipDest, String subIpDest, Switch switchInfo, Route route, int version) throws CapabilityException {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        RFVModel model = (RFVModel) resource.getModel();
        String controllerInfo = null;
        Switch destSwInfo = null;
        try {
            destSwInfo = model.getTable(version).getDestinationSwitch(ipSource, ipDest, switchInfo);
            controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
        } catch (NullPointerException e) {
            log.error("Null Switch Info: " + e.getMessage());
            return Response.serverError().entity("Null Switch Info: " + e.getMessage()).build();
        }

        if (controllerInfo == null) {
            log.error("No information about the controller. Stop find route...");
            return Response.serverError().entity("No information about the controller. Stop find route...").build();
        }
        Route route1 = new Route(subIpSrc, ipDest, destSwInfo);
        int routeId = model.getTable(version).RouteExists(route1);

        int outputPortSW2 = 0;
        if (routeId != 0) {
            outputPortSW2 = model.getTable(version).getOutputPort(routeId);
        } else {
            outputPortSW2 = 0;
            log.error("NO output port in the last Switch.");
//            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }
        log.debug("Output port of the last Switch: " + outputPortSW2);

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
        log.info("Origin Switch: " + switchInfo.getMacAddress() + ", Dest Switch: " + destSwInfo.getMacAddress());
        if (!destSwInfo.getMacAddress().equals(switchInfo.getMacAddress())) {
            json[0] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arpin-mod-" + ipDest + "\", \"priority\":\"32767\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"" + ethertype + "\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
            json[1] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arpto-mod-" + ipSource + "\", \"priority\":\"32767\", \"dst-ip\":\"" + ipSource + "\", \"ether-type\":\"" + ethertype + "\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
            json[2] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4in-mod-" + ipDest + "\", \"priority\":\"32767\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"" + ethertype2 + "\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
            json[3] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4to-mod-" + ipSource + "\", \"priority\":\"32767\", \"dst-ip\":\"" + ipSource + "\", \"ether-type\":\"" + ethertype2 + "\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
            try {
                log.debug("Inserting flows in the destination switch...");
                for (int i = 0; i < json.length; i++) {
                    httpRequest(Url, json[i]);
                }
            } catch (Exception e) {
            }
        }

        //other switches
        log.info("Find routes in other Switches. Switches in the middle. Scenario 2 and 3");
        List<Route> routeSubnetList = model.getTable(version).otherRoutesExists(route, switchInfo, destSwInfo);
        if (routeSubnetList.size() > 1) {
            for (Route r : routeSubnetList) {
                destSwInfo = r.getSwitchInfo();
                controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
                String Url2 = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
                json[0] = "{\"switch\": \"" + r.getSwitchInfo().getMacAddress() + "\", \"name\":\"arpto-mod-" + r.getDestinationAddress() + "\", \"priority\":\"32767\", \"dst-ip\":\"" + r.getDestinationAddress() + ", \"ether-type\":\"" + ethertype + "\", \"active\":\"true\", \"actions\":\"output=" + r.getSwitchInfo().getOutputPort() + "\"}";
                json[1] = "{\"switch\": \"" + r.getSwitchInfo().getMacAddress() + "\", \"name\":\"ip4to-mod-" + r.getDestinationAddress() + "\", \"priority\":\"32767\", \"dst-ip\":\"" + r.getDestinationAddress() + ", \"ether-type\":\"" + ethertype2 + "\", \"active\":\"true\", \"actions\":\"output=" + r.getSwitchInfo().getOutputPort() + "\"}";
                try {
                    for (int i = 0; i < json.length; i++) {
                        httpRequest(Url2, json[i]);
                    }
                } catch (Exception e) {
                }
                outputPortSW2 = model.getTable(version).getOutputPort(route1);
            }
        }
        return Response.ok("Proactive messages sent.").build();
    }
}
