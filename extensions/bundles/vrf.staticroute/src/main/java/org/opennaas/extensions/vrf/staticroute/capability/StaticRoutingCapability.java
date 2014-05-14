package org.opennaas.extensions.vrf.staticroute.capability;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.vrf.model.L2Forward;
import org.opennaas.extensions.vrf.model.RoutingTable;
import org.opennaas.extensions.vrf.model.VRFModel;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.model.topology.Edge;
import org.opennaas.extensions.vrf.model.topology.TopologyInfo;
import org.opennaas.extensions.vrf.model.topology.Vertex;
import org.opennaas.extensions.vrf.utils.Utils;
import org.opennaas.extensions.vrf.utils.UtilsTopology;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class StaticRoutingCapability implements IStaticRoutingCapability {

    Log log = LogFactory.getLog(StaticRoutingCapability.class);
    private VRFModel vrfModel;
    private final String username = "admin";
    private final String password = "123456";
    private List<Vertex> nodes = new ArrayList<Vertex>();
    private List<Edge> edges = new ArrayList<Edge>();
    private final String topologyFilename = "data/dynamicTopology.json";

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

    @Override
    public Response insertRoute(String ipSource, String ipDest, String switchDPID, int inputPort, int outputPort, int lifeTime) {
        log.info("Insert route. Src: " + ipSource + " Dst: " + ipDest + " In: " + inputPort + " Out: " + outputPort);
        VRFModel model = getVRFModel();

        int version;
        if (Utils.isIpAddress(ipSource) == 4 && Utils.isIpAddress(ipDest) == 4) {
            version = 4;
        } else if (Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            version = 6;
        } else {
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
        OFFlow flowArp = Utils.VRFRouteToOFFlow(route, "2054");
        OFFlow flowIp = Utils.VRFRouteToOFFlow(route, "2048");

        //Conversion List of VRFRoute to List of FloodlightFlow
        Response response1 = removeFlow(flowArp);
        Response response2 = removeFlow(flowIp);
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
        ipSource = Utils.intIPToString(ipSource, version);
        ipDest = Utils.intIPToString(ipDest, version);
        if (ipSource == null || ipDest == null) {
            return Response.serverError().entity("Ip (IPv" + version + ") not recognized").build();
        }
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
        VRFModel model = getVRFModel();
        String content = "";
        try {
            content = Utils.convertStreamToString(file);
        } catch (IOException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        Response response = Utils.insertRoutesFromJSONFile(content);
        List<VRFRoute> list = (List<VRFRoute>) response.getEntity();
        if (model.getTable(4) == null) {
            model.setTable(new RoutingTable(4), 4);
        }
        for (VRFRoute r : list) {
            model.getTable(4).addRoute(r);
        }
        setVRFModel(model);
        return Response.ok(response).build();
    }

    @Override
    public Response getRoute(String ipSource, String ipDest, String switchDPID, int inputPort, boolean proactive) {
        int outPortSrcSw;
        Response response;
        int version = Utils.detectIPVersion(ipSource);//IP version
        ipSource = Utils.intIPToString(ipSource, version);
        ipDest = Utils.intIPToString(ipDest, version);
        if (ipSource == null || ipDest == null) {
            return Response.serverError().entity("Ip (IPv" + version + ") not recognized").build();
        }

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
            return Response.status(404).type(MediaType.TEXT_PLAIN).entity("Route Not found.").build();
        }
        /* Proactive routing */

        StringBuilder listFlows = new StringBuilder();
        if (proactive) {
            response = proactiveRouting(route, version);
            List<OFFlow> listOF = ((List<OFFlow>) response.getEntity());
            if (listOF.isEmpty()) {
                return Response.status(404).type(MediaType.TEXT_PLAIN).entity("Route Not found.").build();
            }
            listFlows = Utils.createJSONPath(ipSource, switchDPID, listOF, ipDest);
        }
        return Response.ok(Integer.toString(outPortSrcSw) + ":" + listFlows).build();
    }

    private Response proactiveRouting(VRFRoute route, int version) {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        VRFModel model = getVRFModel();
        List<VRFRoute> routeSubnetList = model.getTable(version).getListRoutes(route, route.getSwitchInfo(), route.getSwitchInfo());
        List<OFFlow> listFlow = new ArrayList<OFFlow>();

	if(edges.isEmpty()){
	        TopologyInfo topInfo = UtilsTopology.createAdjacencyMatrix(topologyFilename, 1);
	        edges = topInfo.getEdges();
	        nodes = topInfo.getNodes();
	}
        log.error("Edges");
        for(Edge e: edges){
            log.error("e: "+e.getId()+" "+e.getSource().getId()+" "+e.getDestination().getId());
        }
        sortRoutes(routeSubnetList, route);

        //Conversion List of VRFRoute to List of OFFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2048"));
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2054"));
            }
        }
        //VTN OpenDaylight
        String srcDPID = route.getSwitchInfo().getDPID();
        String inPort = Integer.toString(route.getSwitchInfo().getInputPort());//String inPort = listFlow.get(0).getMatch().getIngressPort();
        String dstDPID = listFlow.get(listFlow.size() - 1).getDPID();
        String outPort = listFlow.get(listFlow.size() - 1).getActions().get(0).getValue();
        Response response;
        try {
            String initialSw = getProtocolType(srcDPID);
            String targetSw = getProtocolType(dstDPID);
            if (initialSw.equals("opendaylight")) {
                response = callVTN(srcDPID, inPort);
            }
            if (targetSw.equals("opendaylight")) {
                response = callVTN(dstDPID, outPort);
            }
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        // provision each link and mark the last one
        for (int i = 0; i < listFlow.size(); i++) {
            log.debug("Provision Flow " + listFlow.get(i).getMatch().getSrcIp() + " " + listFlow.get(i).getMatch().getDstIp() + " " + listFlow.get(i).getDPID() + " " + listFlow.get(i).getActions().get(0).getType() + ": " + listFlow.get(i).getActions().get(0).getValue());
//            insertFlow(listFlow.get(i));
        }
        return Response.ok(listFlow).build();
    }

    /**
     * Insert routes from dynamic bundle
     *
     * @param json
     * @return
     */
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

        int version;
        if (Utils.isIpAddress(route.getSourceAddress()) == 4 && Utils.isIpAddress(route.getDestinationAddress()) == 4) {
            version = 4;
        } else if (Utils.isIpAddress(route.getSourceAddress()) == 6 && Utils.isIpAddress(route.getDestinationAddress()) == 6) {
            version = 6;
        } else {
            return Response.status(403).type(MediaType.TEXT_PLAIN).entity("The IP version is not detected. Analyze the IP.").build();
        }
        if (model.getTable(version) == null) {
            return Response.status(403).type(MediaType.TEXT_PLAIN).entity("IPv" + version + " table does not exist.").build();
        }

        String response = model.getTable(version).addRoute(route);
        return Response.status(201).entity(response).build();
    }

    public void clearAllFlows() {
        org.opennaas.extensions.vrf.staticroute.capability.utils.Utils.deleteFloodlightFlowHttpRequest("http://controllersVM:8080", "00:00:00:00:00:00:00:01");
        org.opennaas.extensions.vrf.staticroute.capability.utils.Utils.deleteFloodlightFlowHttpRequest("http://controllersVM:8080", "00:00:00:00:00:00:00:02");
        org.opennaas.extensions.vrf.staticroute.capability.utils.Utils.deleteFloodlightFlowHttpRequest("http://controllersVM:8080", "00:00:00:00:00:00:00:03");

        String json[] = new String[6];
        String controllerInfo[] = new String[6];
        controllerInfo[1] = controllerInfo[0] = "controllersVM:8191";
        json[0] = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-11\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"dst-ip\":\"192.168.1.1\", \"src-ip\":\"192.168.1.91\",\"active\":\"true\", \"actions\":\"output=3\"}";
        json[1] = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-12\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"src-ip\":\"192.168.1.1\", \"dst-ip\":\"192.168.1.91\",\"active\":\"true\", \"actions\":\"output=65534\"}";
        json[2] = "{\"switch\": \"00:00:00:00:00:00:00:04\", \"name\":\"flow-mod-41\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"dst-ip\":\"192.168.4.4\", \"src-ip\":\"192.168.4.94\",\"active\":\"true\", \"actions\":\"output=4\"}";
        json[3] = "{\"switch\": \"00:00:00:00:00:00:00:04\", \"name\":\"flow-mod-42\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"src-ip\":\"192.168.4.4\", \"dst-ip\":\"192.168.4.94\",\"active\":\"true\", \"actions\":\"output=65534\"}";
        json[4] = "{\"switch\": \"00:00:00:00:00:00:00:08\", \"name\":\"flow-mod-81\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"dst-ip\":\"192.168.2.51\", \"src-ip\":\"192.168.2.98\",\"active\":\"true\", \"actions\":\"output=2\"}";
        json[5] = "{\"switch\": \"00:00:00:00:00:00:00:08\", \"name\":\"flow-mod-82\", \"cookie\":\"0\", \"priority\":\"32768\", \"ether-type\":\"0x0800\", \"src-ip\":\"192.168.2.51\", \"dst-ip\":\"192.168.2.98\",\"active\":\"true\", \"actions\":\"output=65534\"}";

        for (int i = 0; i < json.length; i++) {
            org.opennaas.extensions.vrf.staticroute.capability.utils.Utils.insertFloodlightFlowHttpRequest("http://" + controllerInfo[i] + "/wm/staticflowentrypusher/json", json[i]);
        }
    }

    /**
     * Insert OFFlow to OpenFlow Switch
     *
     * @param flow
     * @return
     */
    private Response insertFlow(OFFlow flow) {
        log.info("Provision OpenFlow Flow Link");
        String protocol;
        IResource resource;
        try {
            protocol = getProtocolType(flow.getDPID());
            if (protocol == null) {
                return Response.ok("Protocol is null").build();
            }
            resource = getResourceByName(flow.getDPID());
            if (resource == null) {
                return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
            }
            IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
            if (protocol.equals("opendaylight")) {
//                if (!flow.getMatch().getEtherType().equals("2054") && !flow.getMatch().getEtherType().equals("0x0806")) {
//                    OpenDaylightOFFlow odlFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToODL(flow);
//                    forwardingCapability.createOpenflowForwardingRule(odlFlow);
//                }
            } else if (protocol.equals("floodlight")) {
                FloodlightOFFlow fldFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToFLD(flow);
                forwardingCapability.createOpenflowForwardingRule(fldFlow);
            }
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    private Response removeFlow(OFFlow flow) {
        log.info("Remove Flow Link");
        String protocol;
        IResource resource;
        try {
            protocol = getProtocolType(flow.getDPID());
            resource = getResourceByName(flow.getDPID());
            if (resource == null) {
                return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
            }
            IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
            if (protocol.equals("opendaylight")) {
                forwardingCapability.removeOpenflowForwardingRule(flow.getDPID(), flow.getName());
            } else if (protocol.equals("floodlight")) {
                forwardingCapability.removeOpenflowForwardingRule(flow.getDPID(), flow.getName());
            }
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok().build();
    }

    private IResource getResourceByName(String resourceName) throws ActivatorException, ResourceException {
        IResourceManager resourceManager = org.opennaas.extensions.sdnnetwork.Activator.getResourceManagerService();
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

        if (resourceId == null) {
            log.error("IResource id is null.");
            return null;
        }
        return resourceManager.getResource(resourceId);
    }

    private String getProtocolType(String resourceName) throws ActivatorException, ResourceException {
        String protocol;
        IResourceManager resourceManager = org.opennaas.extensions.sdnnetwork.Activator.getResourceManagerService();

        IResource resource = getResourceByName(resourceName);//switchId

        resourceName = "s" + resourceName.substring(resourceName.length() - 1);//00:00:00:00:00:00:00:02 --> s2
        if(resourceName.equals("7")){//PSNC switches
            resourceName = resourceName.substring(19, resourceName.length() - 3);//00:00:00:00:00:00:00:02 --> s2
            if(resourceName.equals("6")){
                resourceName = "s1";
            }else if(resourceName.equals("8")){
                resourceName = "s2";
            }
        }
        IResourceIdentifier resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);
        IResource resourceDesc = resourceManager.getResourceById(resourceId.getId());

        protocol = resourceDesc.getResourceDescriptor().getInformation().getDescription();
        if (protocol == null) {
            protocol = "floodlight";
        }
        return protocol;
    }

    public Response callVTN(String DPID, String Port) {
        log.info("Calling VTN from Static Routing.");
        String url = "http://localhost:8888/opennaas/vtn/ipreq/" + DPID + "/" + Port;
        Response response;
        String base64encodedUsernameAndPassword = Utils.base64Encode(username + ":" + password);

        WebClient client = WebClient.create(url);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);
        client.accept(MediaType.TEXT_PLAIN);
        response = client.get();
        log.error("VTN Manager response: " + response.getStatus());
        return response;
    }

    /**
     * Sort routes given route and edges of topology
     *
     * @param route
     * @param routes
     * @return
     */
    private void sortRoutes(List<VRFRoute> routes, VRFRoute route) {
        String nodeSrc = route.getSwitchInfo().getDPID();
        log.error("nideSrc: "+nodeSrc);
        Boolean set = true;
        int maxLoop = routes.size()*4;
        for (int j = 0; j < routes.size(); j++) {
            if (nodeSrc.equals(routes.get(j).getSwitchInfo().getDPID())) {
                //the defined routes contains two directions. It is possible that there are two routes with the same DPID. Then, we add to sort routes
                set = false;
            } else {
                for (int i = 0; i < edges.size(); i++) {//find the dest node given a source node. Initial node is the source host
                    log.error(edges.get(i).getSource().getId()+"    i:"+i);
                    if ((edges.get(i).getSource().getDPID().equals(nodeSrc)
                            && edges.get(i).getDestination().getDPID().equals(routes.get(j).getSwitchInfo().getDPID())) || (edges.get(i).getDestination().getDPID().equals(nodeSrc)
                            && edges.get(i).getSource().getDPID().equals(routes.get(j).getSwitchInfo().getDPID()))) {
                        nodeSrc = routes.get(j).getSwitchInfo().getDPID();
                        set = false;
                        break;
                    } else {//if not exists a match, move the dpid to the final of the array in order to analyze later
                        set = true;
                    }
                }
            }
            if (set) {//move routes in order to reorder
                UtilsTopology.moveValueAtIndexToEnd(routes, j);
                set = false;
                j--;
                maxLoop--;
            }
            if(maxLoop <= 0){//avoid infinite bucles
                j++;
            }
        }
    }
}
