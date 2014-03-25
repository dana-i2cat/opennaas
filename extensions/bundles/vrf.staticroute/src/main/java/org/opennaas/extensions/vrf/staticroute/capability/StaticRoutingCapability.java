package org.opennaas.extensions.vrf.staticroute.capability;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFAction;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFMatch;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.vrf.model.L2Forward;
import org.opennaas.extensions.vrf.model.RoutingTable;
import org.opennaas.extensions.vrf.model.VRFModel;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.utils.Utils;
//import org.opennaas.extensions.openflowswitch.utils.Utils;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class StaticRoutingCapability implements IStaticRoutingCapability {

    Log log = LogFactory.getLog(StaticRoutingCapability.class);
    private VRFModel vrfModel;
    private String logMessage = "Communication failure";
    private String streamInfo;

    public StaticRoutingCapability() {
        this.vrfModel = new VRFModel();
        this.vrfModel.setTable(new RoutingTable(4), 4);
        this.vrfModel.setTable(new RoutingTable(6), 6);
    }

    public VRFModel getVRFModel() {
        return vrfModel;
    }

    public void setVRFModel(VRFModel VRFModel) {
        this.vrfModel = VRFModel;
    }

    /// /////////////////////////////
    // IRoutingCapability Methods //
    /// /////////////////////////////
    @Override
    public Response getRoute(String ipSource, String ipDest, String switchDPID, int inputPort, boolean proactive) {
        int outPortSrcSw;
        Response response;
        int version;//IP version
        if (Utils.isIPv4Address(ipSource) && Utils.isIPv4Address(ipDest)) {
            ipSource = Utils.intIPv4toString(Integer.parseInt(ipSource));
            ipDest = Utils.intIPv4toString(Integer.parseInt(ipDest));
            version = 4;
        } else if (Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            ipSource = Utils.tryToCompressIPv6(ipSource);
            ipDest = Utils.tryToCompressIPv6(ipDest);
            version = 6;
        } else {
            return Response.serverError().entity("Ip not recognized").build();
        }

        //---------------------DEMO
        streamInfo = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        logMessage = dateFormat.format(date) + " -> Requested route from: " + ipSource + " to " + ipDest + ". Input port: " + inputPort + "; switch:" + switchDPID;
        //---------------------END DEMO

        VRFModel model = getVRFModel();
        if (model.getTable(version) == null) {
            return Response.status(404).type("text/plain").entity("IP Table does not exist.").build();
        }

        log.error("Requested STATIC route: " + ipSource + " > " + ipDest + " " + switchDPID + ", inPort: " + inputPort + ". Proactive: " + proactive);
        L2Forward switchInfo = new L2Forward(inputPort, switchDPID);

        VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo);

        int routeId = model.getTable(version).RouteExists(route);
        if (routeId != 0) {
            outPortSrcSw = model.getTable(version).getOutputPort(routeId);
        } else {
            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }
        /* Proactive routing */
        StringBuilder listFlows = new StringBuilder();
        List<FloodlightOFFlow> listOF;
        if (proactive) {
            response = proactiveRouting(switchInfo, route, version);
            listOF = ((List<FloodlightOFFlow>) response.getEntity());
            if (listOF.isEmpty()) {
                return Response.status(404).type("text/plain").entity("Route Not found.").build();
            }
            listFlows.append("[");
            listFlows.append("{ip:'").append(ipSource).append("'},")//source IP
                    .append("{dpid:'").append(switchDPID).append("'},");//first switch id

            for (int i = 0; i < listOF.size(); i++) {
                if (i == 0) {
                    listFlows.append("{dpid:'");
                    listFlows.append(listOF.get(i).getSwitchId());
                    listFlows.append("'},");//others switch ids
                }
                for (int j = 0; j < i; j++) {
                    if (!listFlows.toString().contains(listOF.get(i).getSwitchId())) {
                        listFlows.append("{dpid:'");
                        listFlows.append(listOF.get(i).getSwitchId());
                        listFlows.append("'},");//others switch ids
                    }
                }

            }
            listFlows.append("{ip:'").append(ipDest).append("'}]");//final destination
        }
        //---------------------DEMO
        date = new Date();
        logMessage = logMessage + "\n" + dateFormat.format(date) + " -> Packet Routed to out port: " + outPortSrcSw + " of the switch: " + switchDPID;
        new updateLog().start();
        streamInfo = listFlows.toString();
        //---------------------END DEMO
        return Response.ok(Integer.toString(outPortSrcSw) + ":" + listFlows).build();
    }

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort, int lifeTime) {
        log.error("Insert route. Src: " + ipSource + " Dst: " + ipDest + " In: " + inputPort + " Out: " + outputPort);
        VRFModel model = getVRFModel();

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
            L2Forward switchInfo = new L2Forward(Integer.toString(inputPort), inputPort, outputPort, switchDPID);
            VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo, lifeTime);

            String response = model.getTable(version).addRoute(route);
            return Response.status(201).entity(response).build();
        }
        return Response.status(403).type("text/plain").entity("Some value is empty").build();
    }

    @Override
    public Response removeRoute(int id, int version) {
        log.error("Removing route " + id + " from table IPv" + version);
        VRFModel model = getVRFModel();
        VRFRoute route = model.getTable(version).getRouteId(id);
        streamInfo = "";
        //call OpenNaaS provisioner
        OFFlow flowArp = Utils.VRFRouteToOFFlow(route, "2054");
        OFFlow flowIp = Utils.VRFRouteToOFFlow(route, "2048");

        //Conversion List of VRFRoute to List of FloodlightFlow
        Response response1;
        Response response2;
        response1 = removeFlow(flowArp);
        response2 = removeFlow(flowIp);
        /**
         * If the flow is no removed (aRP orIP) OpenNaaS always will show the
         * route removeLink should return: ok, not exist or error
         */
        if (response1.getStatus() == 200 && response2.getStatus() == 200) {
            model.getTable(version).removeRoute(id);
        } else {
            model.getTable(version).removeRoute(id);
//                return Response.notModified("Route not removed.").build();
        }

        return Response.ok("Removed").build();
    }

    @Override
    public Response removeRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) {
        log.error("Removing route given all parameters");
        VRFModel model = getVRFModel();
        int version;
        if (Utils.isIPv4Address(ipSource) && Utils.isIPv4Address(ipDest)) {
            version = 4;
        } else if (Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            version = 6;
        } else {
            return Response.serverError().entity("Ip not recognized").build();
        }
        L2Forward switchInfo = new L2Forward("2", inputPort, outputPort, switchDPID);
        VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo);
        int routeId = model.getTable(version).RouteExists(route);
        return removeRoute(routeId, version);
    }

    @Override
    public Response removeRoutes() {
        log.error("REMOVE ALL ROUTES");
        VRFModel model = getVRFModel();
        List<VRFRoute> listRoutes = model.getIpv4().getRouteTable();
        List<Integer> listId = new ArrayList<Integer>();
        for (VRFRoute route : listRoutes) {
            listId.add(route.getId());
        }
        for (int id : listId) {
            removeRoute(id, 4);
        }
//        model.getIpv4().removeRoutes();
//        model.getIpv6().removeRoutes();

        //call OpenNaaS
        clearAllFlows();//DEMOOO
        return Response.ok("Removed").build();
    }

    @Override
    public Response getRoutes() {
        log.info("Get entire Model");
        VRFModel model = getVRFModel();

        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model);
            if (response == null) {
                response = "Empty model. Please, insert routes.";
            }
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    @Override
    public Response getRoutes(int version) {
        log.info("Get entire Route Table of version IPv" + version);
        VRFModel model = getVRFModel();
        if (version != 4 && version != 6) {
            return Response.serverError().entity("This IP version does not exist.").build();
        }
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getTable(version));
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    @Override
    public Response getRoutes(int version, String ipSource, String ipDest) {
        log.info("Get entire Route Table of version IPv" + version);
        ipSource = Utils.intIPv4toString(Integer.parseInt(ipSource));
        ipDest = Utils.intIPv4toString(Integer.parseInt(ipDest));
        VRFModel model = getVRFModel();
        RoutingTable newRT = new RoutingTable(version);
        VRFRoute route = new VRFRoute(ipSource, ipDest);
        List<VRFRoute> routeSubnetList = model.getTable(version).getListRoutes(route);
        newRT.setRouteTable(routeSubnetList);
        if (version != 4 && version != 6) {
            return Response.serverError().entity("This IP version does not exist.").build();
        }
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(newRT);
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(response).build();
    }

    @Override
    public Response insertRouteFile(String filename, InputStream file) {
        log.info("Insert Routes from File");
        String content = "";
        try {
            content = Utils.convertStreamToString(file);
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        VRFModel model = getVRFModel();
        String response = "Inserted";
        try {
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createJsonParser(content);
            JsonToken current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                log.error("Error: root should be object: quiting.");
                return Response.status(404).entity("Error: root should be object: quiting.").build();
            }
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                current = jp.nextToken();// move from field name to field value
                if (fieldName.equals("routeIPv4") || fieldName.equals("routeIPv6")) {
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
                            VRFRoute newRoute = new VRFRoute();
                            L2Forward newSwitch = new L2Forward();
                            newRoute.setSourceAddress(node.get("srcAddr").getValueAsText());
                            newRoute.setDestinationAddress(node.get("dstAddr").getValueAsText());
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setDPID(node.get("swInfo").getPath("dpid").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            if (fieldName.equals("routeIPv4")) {
                                model.getTable(4).addRoute(newRoute);
                            } else if (fieldName.equals("routeIPv6")) {
                                model.getTable(6).addRoute(newRoute);
                            }
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
            setVRFModel(model);
            return Response.ok(response).build();

        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(404).entity("Some error. Check the file. Possible error: " + response).build();
    }

    private Response proactiveRouting(L2Forward srcSwInfo, VRFRoute route, int version) {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        VRFModel model = getVRFModel();
        List<VRFRoute> routeSubnetList = model.getTable(version).getListRoutes(route, srcSwInfo, srcSwInfo);

//        List<FloodlightOFFlow> listFlow = new ArrayList<FloodlightOFFlow>();
        List<OFFlow> listFlow = new ArrayList<OFFlow>();

        //Conversion List of VRFRoute to List of FloodlightFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
//log.error(r.getSourceAddress()+" "+r.getDestinationAddress()+" "+r.getSwitchInfo().getDPID());
//                listFlow.add(Utils.VRFRouteToFloodlightFlow(r, "2048"));
//                listFlow.add(Utils.VRFRouteToFloodlightFlow(r, "2054"));
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2048"));
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2054"));
            }
        }

        // provision each link and mark the last one
        for (int i = 0; i < listFlow.size(); i++) {

            log.debug("Flow " + listFlow.get(i).getMatch().getSrcIp() + " " + listFlow.get(i).getMatch().getDstIp() + " " + listFlow.get(i).getDPID() + " " + listFlow.get(i).getActions().get(0).getType() + ": " + listFlow.get(i).getActions().get(0).getValue());
//                response = provisionLink(listFlow.get(i));
            OFFlow flow = listFlow.get(i);
//                provisionLink(flow);
            insertFlow(flow);

        }
        return Response.ok(listFlow).build();
    }

    private Response provisionLink(FloodlightOFFlow flow) throws ResourceException, ActivatorException {
        log.info("Provision Flow Link Floodlight");
        String switchId = flow.getSwitchId();
        IResource resource = getResourceByName(switchId);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        forwardingCapability.createOpenflowForwardingRule(flow);
        return Response.ok().build();
    }

    private Response removeLink(FloodlightOFFlow flow) throws ResourceException, ActivatorException {
        log.error("REMOVE Provision Flow Link Floodlight");
        String switchId = flow.getSwitchId();
        IResource resource = getResourceByName(switchId);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        try {
            //forwardingCapability.removeOpenflowForwardingRule(flow.getName());
            /*
             * DEMO. New Size should be less than 2. In all switches
             */
            int size = forwardingCapability.getOpenflowForwardingRules().size();
            if (size > 2) {
//                return Response.status(Response.Status.NOT_FOUND).build();
                log.error("More than 2 FLOWS IN SWITCH. DO NoT DELETE ROUTE.");
            }
            log.error("New Size " + size);
        } catch (Exception e) {
            log.error("Error removing flow... " + e.getMessage());
//            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }

    private IResource getResourceByName(String resourceName) throws ActivatorException, ResourceException {
        log.info("Get Resource By switch ID: " + resourceName);
        IResourceManager resourceManager = org.opennaas.extensions.sdnnetwork.Activator.getResourceManagerService();
        log.info("ResourceManager " + resourceManager.getIdentifierFromResourceName("sdnnetwork", "sdn1").getId());
        IResource sdnNetResource = resourceManager.listResourcesByType("sdnnetwork").get(0);
        IOFProvisioningNetworkCapability sdnCapab = (IOFProvisioningNetworkCapability) sdnNetResource.getCapabilityByInterface(IOFProvisioningNetworkCapability.class);

        List<IResource> listResources = resourceManager.listResourcesByType("openflowswitch");
        String resourceSdnNetworkId = sdnCapab.getMapDeviceResource(resourceName);
        if (resourceSdnNetworkId == null) {
            log.error("This Switch ID is not mapped to any ofswitch resource.");
            return null;
        }
        for (IResource r : listResources) {
            if (r.getResourceDescriptor().getId().equals(resourceSdnNetworkId)) {
                resourceName = r.getResourceDescriptor().getInformation().getName();
                log.debug("Switch name is: " + resourceName);
            }
        }

        /*hardcode*/
        resourceName = "s" + resourceName.substring(resourceName.length() - 1);//00:00:00:00:02 --> s2
        IResourceIdentifier resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);

        log.info("IResource id:" + resourceId);
        if (resourceId == null) {
            log.error("IResource id is null.");
            return null;
        }
        return resourceManager.getResource(resourceId);
    }

    //---------------------START DEMO FUNCTIONS & CLASSES
    @Override
    public String getLog() {
        return logMessage;
    }

    public void removeLog() {
        logMessage = "";
    }

    public class updateLog extends Thread {

        public updateLog() {
        }

        @Override
        public void run() {
            try {
                updateLog.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            removeLog();
        }
    }

    @Override
    public String getStream() {
        return streamInfo;
    }
    //---------------------END DEMO FUNCTIONS

    @Override
    public Response insertRoute(String json) {
        ObjectMapper mapper = new ObjectMapper();
        VRFRoute route = new VRFRoute();
        try {
            route = mapper.readValue(json, VRFRoute.class);
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.error("Insert dynamic route. Src: " + route.getSourceAddress() + " Dst: " + route.getDestinationAddress());
        VRFModel model = getVRFModel();

        int version = 0;
        if (Utils.isIpAddress(route.getSourceAddress()) == 4 && Utils.isIpAddress(route.getDestinationAddress()) == 4) {
            version = 4;
        } else if (Utils.isIpAddress(route.getSourceAddress()) == 6 && Utils.isIpAddress(route.getDestinationAddress()) == 6) {
            version = 6;
        } else {
            log.error("IP version error. The detected version is: " + version);
            return Response.status(403).type("text/plain").entity("The IP version is not detected. Analyze the IP.").build();
        }
        if (model.getTable(version) == null) {
            return Response.status(403).type("text/plain").entity("IPv" + version + " table does not exist.").build();
        }
//        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchDPID.isEmpty() && inputPort != 0 && outputPort != 0) {

        String response = model.getTable(version).addRoute(route);
        return Response.status(201).entity(response).build();
//        }
    }

    public void clearAllFlows() {
        deleteHttpRequest("http://controllersVM:8191", "00:00:00:00:00:00:00:01");
        deleteHttpRequest("http://controllersVM:8191", "00:00:00:00:00:00:00:02");
        deleteHttpRequest("http://controllersVM:8191", "00:00:00:00:00:00:00:03");
        deleteHttpRequest("http://controllersVM:8192", "00:00:00:00:00:00:00:04");
        deleteHttpRequest("http://controllersVM:8192", "00:00:00:00:00:00:00:05");
        deleteHttpRequest("http://controllersVM:8192", "00:00:00:00:00:00:00:06");
        deleteHttpRequest("http://controllersVM2:8193", "00:00:00:00:00:00:00:07");
        deleteHttpRequest("http://controllersVM2:8193", "00:00:00:00:00:00:00:08");

        String json[] = new String[6];
        String controllerInfo[] = new String[6];
        controllerInfo[1] = controllerInfo[0] = "controllersVM:8191";
        controllerInfo[2] = controllerInfo[3] = "controllersVM:8192";
        controllerInfo[4] = controllerInfo[5] = "controllersVM2:8193";
        json[0] = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-11\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"dst-ip\":\"192.168.1.1\", \"src-ip\":\"192.168.1.91\",\"active\":\"true\", \"actions\":\"output=3\"}";
        json[1] = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-12\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"src-ip\":\"192.168.1.1\", \"dst-ip\":\"192.168.1.91\",\"active\":\"true\", \"actions\":\"output=65534\"}";
        json[2] = "{\"switch\": \"00:00:00:00:00:00:00:04\", \"name\":\"flow-mod-41\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"dst-ip\":\"192.168.4.4\", \"src-ip\":\"192.168.4.94\",\"active\":\"true\", \"actions\":\"output=4\"}";
        json[3] = "{\"switch\": \"00:00:00:00:00:00:00:04\", \"name\":\"flow-mod-42\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"src-ip\":\"192.168.4.4\", \"dst-ip\":\"192.168.4.94\",\"active\":\"true\", \"actions\":\"output=65534\"}";
        json[4] = "{\"switch\": \"00:00:00:00:00:00:00:08\", \"name\":\"flow-mod-81\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"dst-ip\":\"192.168.2.51\", \"src-ip\":\"192.168.2.98\",\"active\":\"true\", \"actions\":\"output=2\"}";
        json[5] = "{\"switch\": \"00:00:00:00:00:00:00:08\", \"name\":\"flow-mod-82\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"src-ip\":\"192.168.2.51\", \"dst-ip\":\"192.168.2.98\",\"active\":\"true\", \"actions\":\"output=65534\"}";

        for (int i = 0; i < json.length; i++) {
            httpRequest("http://" + controllerInfo[i] + "/wm/staticflowentrypusher/json", json[i]);
        }
        //insert hosts tunnel routes
    }

    private String deleteHttpRequest(String uri, String dpid) {
        String response = "";
        try {
            OutputStreamWriter wr = null;
            URL url = new URL(uri + "/wm/staticflowentrypusher/clear/" + dpid + "/json");
            log.error(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            //response = connection.getResponseMessage();
            return response;
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    private void httpRequest(String Url, String json) {
        try {
            log.error("Request to: " + Url);
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
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ****
     */
    private Response insertFlow(OFFlow flow) {
        Response response = null;
        String protocol;
        try {
            protocol = getProtocolType(flow.getDPID());
            response = provisionLink(flow, protocol);
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    private Response removeFlow(OFFlow flow) {
        Response response = null;
        log.info("Provision Flow Link Floodlight");

        String protocol = null;
        IResource resource = null;
        try {
            protocol = getProtocolType(flow.getDPID());
            resource = getResourceByName(flow.getDPID());
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability;
        try {
            forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
            if (protocol.equals("opendaylight")) {
                forwardingCapability.removeOpenflowForwardingRule(flow.getDPID(), flow.getName());
            } else if (protocol.equals("floodlight")) {
                forwardingCapability.removeOpenflowForwardingRule(flow.getDPID(), flow.getName());
            }
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @Override
    public String insertodl() {

        Response response = null;

        log.error("TEST ODL ...................");

        List<OpenDaylightOFFlow> list = createODLFlows();

        log.error("Flow defined");
        try {
            for (OpenDaylightOFFlow flow : list) {
                String protocol = getProtocolType(flow.getSwitchId());
                provisionLink(flow, protocol);
            }
            //provisionODLLink(flow);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Response ODL";
    }

    @Override
    public String removeodl() {

        Response response = null;

        log.error("TEST ODL REMOVE ...................");

        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        FloodlightOFMatch match = new FloodlightOFMatch();
        List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
        FloodlightOFAction action = new FloodlightOFAction();
        match.setDstIp("192.168.2.2");
        match.setSrcIp("192.168.2.2");
        match.setEtherType("2058");
        match.setIngressPort("1");
        action.setType("output");
        action.setValue("1");
        actions.add(action);

        flow.setSwitchId("00:00:00:00:00:00:00:09");
        flow.setActions(actions);
        flow.setMatch(match);
        flow.setName("TESTODL");
        log.error("Flow defined");
        String flowId = "TESTODL";
        try {
            String protocol = getProtocolType(flow.getSwitchId());
            removeLink(flowId, protocol, flow.getSwitchId());
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Response ODL";
    }

    public String getodlFlow() {
        Response response = null;

        log.error("TEST ODL GET FLOW ...................");
        String DPID = "00:00:00:00:00:00:00:09";
        String name = "TESTODL";
        log.error("Flow defined");
        try {
            String protocol = getProtocolType(DPID);
            getLink(name, DPID, protocol);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Response ODL";
    }

    /**
     * Get list of flows given DPID
     *
     * @return
     */
    @Override
    public String getodl() {

        Response response = null;

        log.error("TEST ODL GET ...................");
        String DPID = "00:00:00:00:00:00:00:09";
        log.error("Flow defined");
        try {
            String protocol = getProtocolType(DPID);
            response = getLinks(protocol, DPID);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Integer.toString(response.getStatus());
    }

    private Response provisionLink(OFFlow flow, String protocol) throws ResourceException, ActivatorException {
        log.info("Provision Flow Link Floodlight");

        String switchId = flow.getDPID();
        IResource resource = getResourceByName(switchId);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        if (protocol.equals("opendaylight")) {
            OpenDaylightOFFlow odlFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToODL(flow);
            forwardingCapability.createOpenflowForwardingRule(odlFlow);
        } else if (protocol.equals("floodlight")) {
            FloodlightOFFlow fldFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToFLD(flow);
            forwardingCapability.createOpenflowForwardingRule(fldFlow);
        }

        return Response.ok().build();
    }

    private Response removeLink(String flowId, String protocol, String DPID) throws ResourceException, ActivatorException {
        log.info("Provision Flow Link Floodlight");

        String switchId = DPID;
        IResource resource = getResourceByName(switchId);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        if (protocol.equals("opendaylight")) {
            forwardingCapability.removeOpenflowForwardingRule(DPID, flowId);
        } else if (protocol.equals("floodlight")) {
            forwardingCapability.removeOpenflowForwardingRule(DPID, flowId);
        }

        return Response.ok().build();
    }

    private Response getLinks(String protocol, String DPID) throws ResourceException, ActivatorException {
        log.info("Provision Flow Link Floodlight");
        List<OFFlow> response = new ArrayList<OFFlow>();
        IResource resource = getResourceByName(DPID);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        if (protocol.equals("opendaylight")) {
            response = forwardingCapability.getOpenflowForwardingRules();
        } else if (protocol.equals("floodlight")) {
            response = forwardingCapability.getOpenflowForwardingRules();
        }

        return Response.ok(response).build();
    }

    private Response getLink(String name, String DPID, String protocol) throws ResourceException, ActivatorException {
        log.info("Provision Flow Link Floodlight");

        IResource resource = getResourceByName(DPID);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        if (protocol.equals("opendaylight")) {
            flow = (OpenDaylightOFFlow) forwardingCapability.getOpenflowForwardingRule(DPID, name);
        } else if (protocol.equals("floodlight")) {
            forwardingCapability.getOpenflowForwardingRule(DPID, name);
        }
        log.error(flow.getName());
        log.error(flow.getMatch().getSrcIp());

        return Response.ok().build();
    }

    private String getProtocolType(String resourceName) throws ActivatorException, ResourceException {
        String protocol = null;
        IResourceManager resourceManager = org.opennaas.extensions.sdnnetwork.Activator.getResourceManagerService();

        IResource resource = getResourceByName(resourceName);//switchId

        log.error("Resource Id of the switch is: " + resource.getResourceIdentifier().getId());

        resourceName = "s" + resourceName.substring(resourceName.length() - 1);//00:00:00:00:02 --> s2
        IResourceIdentifier resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);
        IResource resourceDesc = resourceManager.getResourceById(resourceId.getId());

        protocol = resourceDesc.getResourceDescriptor().getInformation().getDescription();

        log.error("Protocol of switch is: " + protocol);
        return protocol;
    }

    private List<OpenDaylightOFFlow> createODLFlows() {
        List<OpenDaylightOFFlow> list = new ArrayList<OpenDaylightOFFlow>();

        OpenDaylightOFFlow flow = new OpenDaylightOFFlow();
        FloodlightOFMatch match = new FloodlightOFMatch();
        List<FloodlightOFAction> actions = new ArrayList<FloodlightOFAction>();
        FloodlightOFAction action = new FloodlightOFAction();
        match.setDstIp("192.168.1.1");
        match.setSrcIp("192.168.9.99");
        match.setEtherType("2058");
        match.setIngressPort("1");
        action.setType("output");
        action.setValue("1");
        actions.add(action);

        flow.setSwitchId("00:00:00:00:00:00:00:09");
        flow.setActions(actions);
        flow.setMatch(match);
        flow.setName("TESTODL");
        list.add(flow);

        flow = new OpenDaylightOFFlow();
        match = new FloodlightOFMatch();
        actions = new ArrayList<FloodlightOFAction>();
        action = new FloodlightOFAction();
        match.setDstIp("192.168.2.2");
        match.setSrcIp("192.168.8.97");
        match.setEtherType("2058");
        match.setIngressPort("3");
        action.setType("output");
        action.setValue("1");
        actions.add(action);

        flow.setSwitchId("00:00:00:00:00:00:00:09");
        flow.setActions(actions);
        flow.setMatch(match);
        flow.setName("TESTODL2");
        list.add(flow);

        return list;
    }

}
