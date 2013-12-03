package org.opennaas.extensions.vrf.capability.routing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.vrf.model.VRFModel;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.model.RoutingTable;
import org.opennaas.extensions.vrf.model.Switch;
import org.opennaas.extensions.vrf.utils.Utils;
import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Route;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class RoutingCapability implements IRoutingCapability {

    Log log = LogFactory.getLog(RoutingCapability.class);
    private VRFModel vrfModel;

    public VRFModel getVRFModel() {
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
        int version;//IP version
        if (Utils.isIPv4Address(ipSource) && Utils.isIPv4Address(ipDest)) {
            ipSource = Utils.intIPv4toString(Integer.parseInt(ipSource));
            ipDest = Utils.intIPv4toString(Integer.parseInt(ipDest));
            version = 4;
        } else if(Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            ipSource = Utils.tryToCompressIPv6(ipSource);
            ipDest = Utils.tryToCompressIPv6(ipDest);
            version = 6;
        } else{
            return Response.serverError().entity("Ip not recognized").build();
        }

        VRFModel model = getVRFModel();
        if (model.getTable(version) == null) {
            return Response.status(404).type("text/plain").entity("IP Table does not exist.").build();
        }

        log.info("Requested route: " + ipSource + " > " + ipDest + " " + switchDPID + ", inPort: " + inputPort);
        Switch switchInfo = new Switch(inputPort, switchDPID);

        VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo);
                
        int routeId = model.getTable(version).RouteExists(route);
        if (routeId != 0) {
            outPortSrcSw = model.getTable(version).getOutputPort(routeId);
        } else {
            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }

        if (proactive) {            
            proactiveRouting(switchInfo, route, version);
        }
        return Response.ok(Integer.toString(outPortSrcSw) + ":" + ipDest).build();
    }

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) {
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
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchDPID);
            VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo);

            String response = model.getTable(version).addRoute(route);
            return Response.status(201).entity(response).build();
        }
        return Response.status(403).type("text/plain").entity("Some value is empty").build();
    }

    @Override
    public Response removeRoute(int id, int version) {
        log.info("Removing route "+id+" from table IPv"+version);
        VRFModel model = getVRFModel();
        VRFRoute route = model.getTable(version).getRouteId(id);

        //call OpenNaaS provisioner

        return Response.ok("Removed").build();
    }

    @Override
    public Response removeRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort) {
        VRFModel model = getVRFModel();
        int version;
        if (Utils.isIPv4Address(ipSource) && Utils.isIPv4Address(ipDest)) {            
            version = 4;
        } else if(Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            version = 6;
        } else{
            return Response.serverError().entity("Ip not recognized").build();
        }
        Switch switchInfo = new Switch("2", inputPort, outputPort, switchDPID);
        VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo);
        int routeId = model.getTable(version).RouteExists(route);
        return removeRoute(routeId, version);
    }

    @Override
    public Response removeRoutes(){
        VRFModel model = getVRFModel();
        model.getIpv4().removeRoutes();
        model.getIpv6().removeRoutes();

        //call OpenNaaS
        
        return Response.ok("Removed").build();
    }

    @Override
    public Response getRoutes(){
        log.info("Get entire Model");
        VRFModel model = getVRFModel();

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
    public Response getRoutes(int version){
        log.info("Get entire Route Table of version IPv" + version);
        VRFModel model = getVRFModel();
        if (model.getTable(version) == null) {
            if(version == 4  || version == 6){
                model.setTable(new RoutingTable(version), version);
            }else{
                return Response.serverError().entity("This IP version does not exist.").build();
            }
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
    public Response insertRouteFile(String filename/*, InputStream file*/) {
        log.info("Insert Routes from File");
        
        VRFModel model = new VRFModel();
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
                            Switch newSwitch = new Switch();
                            newRoute.setSourceAddress(node.get("srcAddr").getValueAsText());
                            newRoute.setDestinationAddress(node.get("dstAddr").getValueAsText());
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            if (fieldName.equals("routeIPv4")) {
                                model.getTable(4).addRoute(newRoute);
                            }
                            else if (fieldName.equals("routeIPv6")) {
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
        
        return Response.status(404).entity("Some error. Check the file. Possible error: "+response).build();
    }

    private Response proactiveRouting(Switch srcSwInfo, VRFRoute route, int version) {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        VRFModel model = getVRFModel();
        
        List<VRFRoute> routeSubnetList = model.getTable(version).getListRoutes(route, srcSwInfo, srcSwInfo);
        
        List<FloodlightOFFlow> listFlow = new ArrayList<FloodlightOFFlow>();
        
        //Conversion List of VRFRoute to List of FloodlightFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
                listFlow.add(Utils.VRFRouteToFloodlightFlow(r));
            }
        }
/*        
        List<NetworkConnection> listNetCon = new ArrayList<NetworkConnection>();
        NetworkConnection netCon = new NetworkConnection();
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
                netCon = Utils.VRFRouteToNetCon(r);
                listNetCon.add(netCon);
            }
        }
        Route ONRoute = new Route();
        ONRoute.setNetworkConnections(listNetCon);
*/
        /*call provisioner OpenNaaS */
    
        // provision each link and mark the last one
	for (int i = 0; i < listFlow.size(); i++) {
//            NetworkConnection networkConnection = listNetCon.get(i);
            try{
                provisionLink(listFlow.get(i));
/*                provisionLink(networkConnection, new SDNNetworkOFFlow(flow),
						i == listNetCon.size() - 1);
*/            }catch (Exception e){
//                throw new ActionException("Error provisioning link : ", e);
            }
        }
/*        
//        SDNNetworkModel sdn = new SDNNetworkModel();
        Map<String, IResource>	switches;
//        sdn.getDeviceResourceMap();
        IResource switchResource = getSwitchResourceFromName(connection.getSource().getDeviceId());
        Switch sw = getSwitchResource(flow.getSwitchId());
        sw.getFlowCap().allocatelow(flow);
*/        
        return Response.ok("Proactive messages sent.").build();
    }

    private void provisionLink(FloodlightOFFlow flow/*, NetworkConnection connection, SDNNetworkOFFlow sdnNetworkOFFlow, boolean isLastLinkInRoute*/) throws ResourceException,ActivatorException {
        log.info("Provision Flow Link Floodlight");
        String resourceName = flow.getSwitchId();
        log.info("RsourceName: "+resourceName);
        IResource resource = getResourceByName(resourceName);
        IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
        forwardingCapability.createOpenflowForwardingRule(flow);
    }

    private IResource getResourceByName(String resourceName) throws ActivatorException, ResourceException {
        log.info("Get Resource By Name "+resourceName);
        log.info("................................................");
        IResourceManager resourceManager = org.opennaas.extensions.sdnnetwork.Activator.getResourceManagerService();
        IProtocolManager protocol = org.opennaas.extensions.sdnnetwork.Activator.getProtocolManagerService();
        for(String test : protocol.getAllResourceIds()){
            log.info(test);
            try {
                ProtocolSessionManager psm = (ProtocolSessionManager) protocol.getProtocolSessionManager(test);
               log.info("True? "+psm.getRegisteredContexts().get(0).getSessionParameters().containsValue(resourceName));
               List<ProtocolSessionContext> lis = psm.getRegisteredContexts();
               for(ProtocolSessionContext p : lis){
                   log.info(p.getSessionParameters().containsValue(resourceName));
                   IProtocolSession ps = psm.obtainSession(p, true);
                   String sessionId = ps.getSessionId();
//                   resourceManager.
               }
               
               log.info(psm.getResourceID());
            } catch (ProtocolException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        log.info("ResourceManager "+resourceManager.getNameFromResourceID(resourceName));
        log.info("ResourceManager "+resourceManager.listResources().get(0).getResourceIdentifier().getId());
        log.info("ResourceManager "+resourceManager.listResourcesByType("openflowswitch").get(0).getResourceDescriptor().getInformation().getName());
        log.info("ResourceManager "+resourceManager.listResourcesByType("openflowswitch").get(1).getResourceDescriptor().getInformation().getName());
        IResourceIdentifier resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);
        log.info("Resource id :"+resourceId);
        resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", "s1");
        log.info("Resource id :"+resourceId);
        if(resourceId == null){
            log.info("Resource id is null");    
        }
        return resourceManager.getResource(resourceId);
    }
    
    @Override
    public String getLog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}