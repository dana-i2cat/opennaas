package org.opennaas.extensions.vrf.dijkstraroute.capability;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.openflowswitch.capability.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.sdnnetwork.capability.ofprovision.IOFProvisioningNetworkCapability;
import org.opennaas.extensions.vrf.model.L2Forward;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.dijkstraroute.model.dijkstra.DijkstraAlgorithm;
import org.opennaas.extensions.vrf.dijkstraroute.model.dijkstra.Edge;
import org.opennaas.extensions.vrf.dijkstraroute.model.dijkstra.Graph;
import org.opennaas.extensions.vrf.utils.Utils;
import org.opennaas.extensions.vrf.dijkstraroute.model.dijkstra.Vertex;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class DijkstraRoutingCapability implements IDijkstraRoutingCapability {

    Log log = LogFactory.getLog(DijkstraRoutingCapability.class);
    private String logMessage = "Communication failure";
    private String streamInfo;
    private List<Vertex> nodes = new ArrayList<Vertex>();
    private List<Edge> edges = new ArrayList<Edge>();
    private final int staticDijkstraCost = 1;

    /*    public RoutingCapability(){
     vrfModel = new VRFModel();
     }
     */
    /// /////////////////////////////
    // DijkstraRoutingCapability Methods //
    /// /////////////////////////////
    
    @Override
    public Response getDynamicRoute(String source, String target) {
        log.error("Request route " + source + " dst: " + target);

        createAdjacencyMatrix();
        Vertex src = getVertex(source);
        Vertex dst = getVertex(target);

        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(src);//calculate the adjacent matrix from the requested source vertex
        LinkedList<Vertex> path = dijkstra.getPath(dst);
        log.error("Path: " + path);

        List<VRFRoute> listRoutes = creatingRoutes(path, source, target);

        StringBuilder listFlows = new StringBuilder();
        List<FloodlightOFFlow> listOF;
        Response response = proactiveRouting(listRoutes);
        listOF = ((List<FloodlightOFFlow>) response.getEntity());
        if (listOF.isEmpty()) {
            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }
        listFlows.append("[");
        listFlows.append("{ip:'").append(source).append("'},")//source IP
                ;//first switch id

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
        listFlows.append("{ip:'").append(target).append("'}]");//final destination
        return Response.ok("route: " + listFlows).build();

        //path format ; [192.168.1.1, 00:00:00:00:00:00:00:01, 00:00:00:00:00:00:00:03, 192.168.2.51]
        //return path.toString();
//        return response;
    }
    
    private Response proactiveRouting(List<VRFRoute> listVRF) {
        log.info("Proactive Routing. Searching the last Switch of the Route...");
        List<VRFRoute> routeSubnetList = listVRF;

        List<FloodlightOFFlow> listFlow = new ArrayList<FloodlightOFFlow>();

        //Conversion List of VRFRoute to List of FloodlightFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
                log.error("Route " + r.getSourceAddress() + " " + r.getDestinationAddress() + " " + r.getSwitchInfo().getDPID() + " " + r.getSwitchInfo().getInputPort() + " " + r.getSwitchInfo().getOutputPort());
                listFlow.add(Utils.VRFRouteToFloodlightFlow(r, "2048"));
//                listFlow.add(Utils.VRFRouteToFloodlightFlow(r, "2054"));
            }
        }

        // provision each link and mark the last one
        for (int i = 0; i < listFlow.size(); i++) {
            try {
//log.error("Flow "+listFlow.get(i).getMatch().getSrcIp()+" "+listFlow.get(i).getMatch().getDstIp()+" "+listFlow.get(i).getSwitchId());
                FloodlightOFFlow flow = listFlow.get(i);
                flow.getMatch().setEtherType("2048");
                flow.setName(String.valueOf(flow.getName()) + "-2048-" + listFlow.get(i).getMatch().getSrcIp() + "-" + listFlow.get(i).getMatch().getDstIp());
                provisionLink(flow);

                flow.getMatch().setEtherType("2054");
                flow.setName(String.valueOf(flow.getName()) + "-2054-" + listFlow.get(i).getMatch().getSrcIp() + "-" + listFlow.get(i).getMatch().getDstIp());
                provisionLink(flow);
            } catch (ResourceException e) {
//                throw new ActionException("Error provisioning link : ", e);
            } catch (ActivatorException e) {
//                throw new ActionException("Error provisioning link : ", e);
            }
        }
        return Response.ok(listFlow).build();
    }

    private Response provisionLink(FloodlightOFFlow flow/*, NetworkConnection connection, SDNNetworkOFFlow sdnNetworkOFFlow, boolean isLastLinkInRoute*/) throws ResourceException, ActivatorException {
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
                Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            removeLog();
        }
    }

    @Override
    public String getStream() {
        return streamInfo;
    }
    //---------------------END DEMO FUNCTIONS

    

    private List<VRFRoute> creatingRoutes(LinkedList<Vertex> path, String sourceIp, String targetIP) {
        List<VRFRoute> listRoutes = new ArrayList<VRFRoute>();
        Vertex source = path.get(0);
        String dpid;
        for (int j = 1; j < path.size() - 1; j++) {
            Vertex actual = path.get(j);
            Vertex nextVertex = path.get(j + 1);
            if (path.get(j).getType() == 0) {//is a switch
                int inputPort = extractPort(source, actual, 0);
                dpid = actual.getDPID();
                int outputPort = extractPort(actual, nextVertex, 1);

                L2Forward sw = new L2Forward();
                sw.setDPID(dpid);
                sw.setInputPort(inputPort);
                sw.setOutputPort(outputPort);
                VRFRoute newRoute = new VRFRoute();
                newRoute.setSourceAddress(sourceIp);
                newRoute.setDestinationAddress(targetIP);
                newRoute.setSwitchInfo(sw);

                listRoutes.add(newRoute);
                source = path.get(j);
            }
        }

        return listRoutes;

    }

    /**
     * Given a source vertex and target vertex, this function returns the port.
     *
     * @param source
     * @param actual
     * @param type Is used in order to differentiate when we need the dst
     * port(first execution) or de source port
     * @return
     */
    private int extractPort(Vertex source, Vertex actual, int type) {
        int port = 0;
        for (int i = 0; i < edges.size(); i++) {
            //from the newVertex try to find the next hop
            if (edges.get(i).getSource().equals(source) && edges.get(i).getDestination().equals(actual)) {
log.error("S" + i + " " + source + " to " + actual + " Src: " + edges.get(i).getSrcPort() + " Dst: " + edges.get(i).getDstPort());
                if (edges.get(i).getSrcPort() == 0) {
                    port = edges.get(i).getDstPort();
                } else if (edges.get(i).getDstPort() == 0) {
                    port = edges.get(i).getSrcPort();
                } else {
                    if (type == 0) {
                        port = edges.get(i).getDstPort();
                    } else {
                        port = edges.get(i).getSrcPort();
                    }
                }
                break;
            }
        }
log.error("Return Port: " + port);
        return port;
    }

    private Vertex getVertex(String dpid) {
        for (Vertex v : nodes) {
            if (v.getDPID().equals(dpid)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Read the json file that contains the topology and store the nodes and
     * edges in the global variables.
     */
    private void createAdjacencyMatrix() {

        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();

        try {
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createJsonParser(new File("dynamicTopology.json"));
            JsonToken current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                log.error("Error: root should be object: quiting.");
            }
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                current = jp.nextToken();// move from field name to field value
                if (fieldName.equals("nodes")) {
                    if (current == JsonToken.START_ARRAY) {
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            JsonNode node = jp.readValueAsTree();
                            int type;
                            if (node.get("type").getValueAsText().equals("sw")) {
                                type = 0;
                            } else {
                                type = 1;
                            }
                            Vertex v = new Vertex(node.get("id").getValueAsText(),
                                    node.get("dpid").getValueAsText(),
                                    type);
                            nodes.add(v);
                        }
                    } else {
                        jp.skipChildren();
                    }
                } else if (fieldName.equals("links")) {
                    if (current == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            JsonNode link = jp.readValueAsTree();
                            String srcId = link.get("source").getValueAsText();
                            String dstId = link.get("target").getValueAsText();
                            int srcPort = Integer.parseInt(link.get("srcPort").getValueAsText());
                            int dstPort = Integer.parseInt(link.get("dstPort").getValueAsText());
                            Vertex srcV = null;
                            Vertex dstV = null;
                            for (Vertex v : nodes) {
                                if (v.getId().equals(srcId)) {
                                    srcV = new Vertex(v.getId(), v.getDPID(), v.getType());
                                } else if (v.getId().equals(dstId)) {
                                    dstV = new Vertex(v.getId(), v.getDPID(), v.getType());
                                }
                            }
                            Edge e = new Edge(link.get("id").getValueAsText(), srcV, dstV, staticDijkstraCost, srcPort, dstPort);
                            edges.add(e);
                            e = new Edge(link.get("id").getValueAsText() + "-", dstV, srcV, staticDijkstraCost, dstPort, srcPort);
                            edges.add(e);
                        }
                    } else {
                        jp.skipChildren();
                    }
                } else {
                    jp.skipChildren();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}