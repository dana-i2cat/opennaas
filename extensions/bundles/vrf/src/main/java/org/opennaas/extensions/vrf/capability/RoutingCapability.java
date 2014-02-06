package org.opennaas.extensions.vrf.capability;

import java.io.IOException;
import java.io.InputStream;
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
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.vrf.model.L2Forward;
import org.opennaas.extensions.vrf.model.RoutingTable;
import org.opennaas.extensions.vrf.model.VRFModel;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.utils.Utils;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class RoutingCapability implements IRoutingCapability {

    Log log = LogFactory.getLog(RoutingCapability.class);
    private VRFModel vrfModel;
    private String logMessage = "Communication failure";
    private String streamInfo;

    public VRFModel getVRFModel() {
        if (this.vrfModel == null) {
            this.vrfModel = new VRFModel();
            this.vrfModel.setTable(new RoutingTable(4), 4);
            this.vrfModel.setTable(new RoutingTable(6), 6);
        }
        return vrfModel;
    }

    public void setVRFModel(VRFModel VRFModel) {
        this.vrfModel = VRFModel;
    }

    /*    public RoutingCapability(){
     vrfModel = new VRFModel();
     }
     */
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
if(proactive)
        logMessage = dateFormat.format(date) + " Request received " + switchDPID +" -> " + ipSource + " throught port " + inputPort + " -> Route requested: " + ipDest;
        //---------------------END DEMO

        VRFModel model = getVRFModel();
        if (model.getTable(version) == null) {
            return Response.status(404).type("text/plain").entity("IP Table does not exist.").build();
        }

        log.error("Requested route: " + ipSource + " > " + ipDest + " " + switchDPID + ", inPort: " + inputPort);
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
        List<String> dupDPID = new ArrayList<String>();
        List<FloodlightOFFlow> listOF;
//        if (proactive) {
            response = proactiveRouting(switchInfo, route, version);
            listOF = ((List<FloodlightOFFlow>) response.getEntity());
            log.error("Size ListFlows "+listOF.size());
            if( listOF.isEmpty() ){
                return Response.status(404).type("text/plain").entity("Route Not found.").build();
            }
            listFlows.append("[");
            listFlows.append("{ip:'").append(ipSource).append("'},")//source IP
                    .append("{dpid:'").append(switchDPID).append("'},");//first switch id

            for (int i = 0; i< listOF.size(); i++) {
                if ( i == 0){
                    listFlows.append("{dpid:'");
                    listFlows.append(listOF.get(i).getSwitchId());
                    listFlows.append("'},");//others switch ids
                }
                for (int j = 0; j < i; j++){
                    if ( !listFlows.toString().contains(listOF.get(i).getSwitchId()) ){
                        listFlows.append("{dpid:'");
                        listFlows.append(listOF.get(i).getSwitchId());
                        listFlows.append("'},");//others switch ids
                    }
                }

            }
            listFlows.append("{ip:'").append(ipDest).append("'}]");//final destination
//        }
        //---------------------DEMO
        date = new Date();
if(proactive)
        logMessage = logMessage + "<br>" + dateFormat.format(date) + " -> Packet Routed to out port: " + outPortSrcSw + " of the switch: " + switchDPID;
        new updateLog().start();
        streamInfo = listFlows.toString();
        //---------------------END DEMO
        return Response.ok(Integer.toString(outPortSrcSw) + ":" + listFlows).build();
    }

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort, int lifeTime) {
        log.info("Insert route. Src: " + ipSource + " Dst: " + ipDest + " In: " + inputPort + " Out: " + outputPort);
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
        log.info("Removing route " + id + " from table IPv" + version);
        VRFModel model = getVRFModel();
        VRFRoute route = model.getTable(version).getRouteId(id);
        streamInfo = "";
        //call OpenNaaS provisioner
        FloodlightOFFlow flowArp = Utils.VRFRouteToFloodlightFlow(route, "2054");
        FloodlightOFFlow flowIp = Utils.VRFRouteToFloodlightFlow(route, "2048");

        model.getTable(version).removeRoute(id);
        //Conversion List of VRFRoute to List of FloodlightFlow
        try {
            removeLink(flowArp);
            removeLink(flowIp);
        } catch (ResourceException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ActivatorException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok("Removed").build();
    }

    @Override
    public Response removeRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) {
        log.info("Removing route given all parameters");
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
        VRFModel model = getVRFModel();
        model.getIpv4().removeRoutes();
        model.getIpv6().removeRoutes();

        //call OpenNaaS
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
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(404).entity("Some error. Check the file. Possible error: " + response).build();
    }

    private Response proactiveRouting(L2Forward srcSwInfo, VRFRoute route, int version) {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        VRFModel model = getVRFModel();
        List<VRFRoute> routeSubnetList = model.getTable(version).getListRoutes(route, srcSwInfo, srcSwInfo);

        List<FloodlightOFFlow> listFlow = new ArrayList<FloodlightOFFlow>();

        //Conversion List of VRFRoute to List of FloodlightFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
//log.error(r.getSourceAddress()+" "+r.getDestinationAddress()+" "+r.getSwitchInfo().getDPID());
                listFlow.add(Utils.VRFRouteToFloodlightFlow(r, "2048"));
            }
        }

        // provision each link and mark the last one
        for (int i = 0; i < listFlow.size(); i++) {
            try {
log.debug("Flow "+listFlow.get(i).getMatch().getSrcIp()+" "+listFlow.get(i).getMatch().getDstIp()+" "+listFlow.get(i).getSwitchId()+" "+listFlow.get(i).getActions().get(0).getType()+": "+listFlow.get(i).getActions().get(0).getValue());
//                response = provisionLink(listFlow.get(i));
                FloodlightOFFlow flow = listFlow.get(i);
                flow.getMatch().setEtherType("2048");
                flow.setName(String.valueOf(route.getId())+"-2048-"+listFlow.get(i).getMatch().getSrcIp()+"-"+listFlow.get(i).getMatch().getDstIp()+"-"+listFlow.get(i).getSwitchId().substring(listFlow.get(i).getSwitchId().length() - 2));
                provisionLink(flow);
                
                flow.getMatch().setEtherType("2054");
                flow.setName(String.valueOf(route.getId())+"-2054-"+listFlow.get(i).getMatch().getSrcIp()+"-"+listFlow.get(i).getMatch().getDstIp()+"-"+listFlow.get(i).getSwitchId().substring(listFlow.get(i).getSwitchId().length() - 2));
                provisionLink(flow);
            } catch (ResourceException e) {
//                throw new ActionException("Error provisioning link : ", e);
            } catch (ActivatorException e) {
//                throw new ActionException("Error provisioning link : ", e);
            }
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
        log.info("Provision Flow Link Floodlight");
        String switchId = flow.getSwitchId();
        IResource resource = getResourceByName(switchId);
        if (resource == null) {
            return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
        }
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        try{
        forwardingCapability.removeOpenflowForwardingRule(flow.getName());
        } catch(Exception e){
            log.error("Controller does not contain information about this route... "+e.getMessage());
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
    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public void setStreamInfo(String streamInfo) {
        this.streamInfo = streamInfo;
    }
    

    public class updateLog extends Thread {

        public updateLog() {
        }

        @Override
        public void run() {
            try {
                updateLog.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
//            setLogMessage("");
        }
    }
    
    @Override
    public String getStream(){
        return streamInfo;
    }
    //---------------------END DEMO FUNCTIONS
}
