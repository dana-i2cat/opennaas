package org.opennaas.extensions.vrf.dijkstraroute.capability;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Dijkstra Route
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;
import org.opennaas.extensions.vrf.dijkstraroute.model.dijkstra.DijkstraAlgorithm;
import org.opennaas.extensions.vrf.model.L2Forward;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.model.topology.Edge;
import org.opennaas.extensions.vrf.model.topology.Graph;
import org.opennaas.extensions.vrf.model.topology.TopologyInfo;
import org.opennaas.extensions.vrf.model.topology.Vertex;
import org.opennaas.extensions.vrf.utils.Utils;
import org.opennaas.extensions.vrf.utils.UtilsTopology;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class DijkstraRoutingCapability implements IDijkstraRoutingCapability {

    Log log = LogFactory.getLog(DijkstraRoutingCapability.class);
    private List<Vertex> nodes = new ArrayList<Vertex>();
    private List<Edge> edges = new ArrayList<Edge>();
    private final int staticDijkstraCost = 1;
    private final String username = "admin";
    private final String password = "123456";
    private final String netResourceName = "ofnet1";
    private String topologyFilename = "data/dynamicTopology.json";

    @Override
    public Response getDynamicRoute(String source, String target, String DPID, int inPort) {
        source = Utils.intIPv4toString(Integer.parseInt(source));
        target = Utils.intIPv4toString(Integer.parseInt(target));
        log.error("Request route " + source + " dst: " + target + " DPID: "+DPID);

//        TopologyInfo topInfo = UtilsTopology.createAdjacencyMatrix(topologyFilename, 1);
        TopologyInfo topInfo = getTopology(staticDijkstraCost);
        edges = topInfo.getEdges();
        nodes = topInfo.getNodes();
        Vertex src = getVertex(source);
        Vertex dst = getVertex(target);

        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(src);//calculate the adjacent matrix from the requested source vertex
        LinkedList<Vertex> path = dijkstra.getPath(dst);
        log.error("Path: " + path);

        List<VRFRoute> listRoutes;
        if (path != null) {//if path null???
            listRoutes = creatingRoutes(path, source, target);
        } else {
            return Response.ok("Path null. This route is not possible.").build();
        }

        int outPutPortSrcSw = getOutPortSrcSw(path, DPID);
        Response response = proactiveRouting(listRoutes);
        List<OFFlow> listOF = ((List<OFFlow>) response.getEntity());
        if (listOF.isEmpty()) {
            return Response.status(404).type("text/plain").entity("Route Not found.").build();
        }
        StringBuilder listFlows = Utils.createJSONPath(source, null, listOF, target);
        return Response.ok(outPutPortSrcSw + ":" + listFlows).build();

        //path format ; [192.168.1.1, 00:00:00:00:00:00:00:01, 00:00:00:00:00:00:00:03, 192.168.2.51]
    }

    private Response proactiveRouting(List<VRFRoute> routeSubnetList) {
        log.info("Proactive Routing. Searching the last Switch of the Route...");

        List<OFFlow> listFlow = new ArrayList<OFFlow>();

        //Conversion List of VRFRoute to List of FloodlightFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
                insertRoutetoStaticBundle(r);
                log.error("Route " + r.getSourceAddress() + " " + r.getDestinationAddress() + " " + r.getSwitchInfo().getDPID() + " " + r.getSwitchInfo().getInputPort() + " " + r.getSwitchInfo().getOutputPort());
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2048"));
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2054"));
            }
        }
        for (OFFlow listFlow1 : listFlow) {
            log.error("Flow " + listFlow1.getMatch().getSrcIp() + " " + listFlow1.getMatch().getDstIp() + " " + listFlow1.getDPID() + " " + listFlow1.getMatch().getIngressPort() + " " + listFlow1.getActions().get(0).getType() + ": " + listFlow1.getActions().get(0).getValue());
            insertFlow(listFlow1);
        }
        return Response.ok(listFlow).build();
    }

    /**
     * Insert OFFlow to OpenFlow Switch
     *
     * @param flow
     * @return
     */
    private Response insertFlow(OFFlow flow) {
        log.info("Dynamic Provision OpenFlow Flow Link");
        String protocol;
        IResource resource;
        try {
            String resourceName = getSwitchMapping(flow.getDPID());
            protocol = getProtocolType(resourceName);
            resource = getIResource(resourceName);
            if (protocol == null) {
                return Response.ok("Protocol is null").build();
            }if (resource == null) {
                return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
            }
            IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
            if (protocol.equals("opendaylight")) {
                if (!flow.getMatch().getEtherType().equals("2054") && !flow.getMatch().getEtherType().equals("0x0806")){
                 OpenDaylightOFFlow odlFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToODL(flow);
                 forwardingCapability.createOpenflowForwardingRule(odlFlow);
                 }
            } else if (protocol.equals("floodlight")) {
                FloodlightOFFlow fldFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToFLD(flow);
                forwardingCapability.createOpenflowForwardingRule(fldFlow);
            }
        } catch (ResourceException ex) {
            Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    private int getOutPortSrcSw(LinkedList<Vertex> path, String DPID) {
	for(int i=0; i<path.size(); i++){
            if(path.get(i).getDPID().equals(DPID)){
                return extractPort(path.get(i), path.get(i+1), 1);
            }
        }
        return extractPort(path.get(1), path.get(2), 1);
    }

    private List<VRFRoute> creatingRoutes(LinkedList<Vertex> path, String sourceIP, String targetIP) {
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

                L2Forward sw = new L2Forward("2", inputPort, outputPort, dpid);
                VRFRoute newRoute = new VRFRoute(sourceIP, targetIP, sw);
                newRoute.setType("dynamic");

                listRoutes.add(newRoute);

                sw = new L2Forward("2", outputPort, inputPort, dpid);
                newRoute = new VRFRoute(targetIP, sourceIP, sw);
                newRoute.setType("dynamic");

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
        for (Edge edge : edges) {
            //from the newVertex try to find the next hop
            if (edge.getSource().equals(source) && edge.getDestination().equals(actual)) {
//                log.error("S" + i + " " + source + " to " + actual + " Src: " + edges.get(i).getSrcPort() + " Dst: " + edges.get(i).getDstPort());
                if (edge.getSrcPort() == 0) {
                    port = edge.getDstPort();
                } else if (edge.getDstPort() == 0) {
                    port = edge.getSrcPort();
                } else {
                    if (type == 0) {
                        port = edge.getDstPort();
                    } else {
                        port = edge.getSrcPort();
                    }
                }
                break;
            }
        }
//        log.error("Return Port: " + port);
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
     * Call a rest service to insert a StaticRoute
     *
     * @param route
     * @return true if the environment has been created
     */
    public String insertRoutetoStaticBundle(VRFRoute route) {
        log.info("Calling insert Route Table service");
        String response = null;
        String url = "http://localhost:8888/opennaas/vrf/staticrouting/dynamic-route";

        Form fm = new Form();
        fm.set("route", (VRFRoute) route);

        WebClient client = WebClient.create(url);
        String base64encodedUsernameAndPassword = Utils.base64Encode(username + ":" + password);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);

        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(route);

        } catch (IOException ex) {
            Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        response = client.accept(MediaType.TEXT_PLAIN).type(MediaType.APPLICATION_JSON).put(response, String.class);

        log.error("Insert to other Bundle Response: " + response);
        return response;
    }

    @Override
    public Response getTopologyFilename() {
        return Response.ok(topologyFilename).build();
    }

    @Override
    public Response setTopologyFilename(String topologyFilename) {
        this.topologyFilename = topologyFilename;
        return Response.ok("FileName " + topologyFilename + "selected.").build();
    }

    public Response uploadDynamicTopology() {
        Response response = null;
        /**
         * NOT IMPLEMENTED YEET!!!!!!!!!!!!
         *
         */
        return response;
    }

    public Response callVTN(String DPID, String Port) {
        log.error("Calling VTN from Dynamic (Dijkstra) Routing.");
        if (DPID == null || Port == null) {
            log.error("DstDPID: " + DPID + " outPort: " + Port);
            return Response.status(400).entity("DstDPID or outPut port is null").build();
        }
        String url = "http://localhost:8888/opennaas/vtn/ipreq/" + DPID + "/" + Port;
        String base64encodedUsernameAndPassword = Utils.base64Encode(username + ":" + password);

        WebClient client = WebClient.create(url);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);
        client.accept(MediaType.TEXT_PLAIN);
        Response response = client.get();
        log.error("VTN Coordinator response: " + response.getStatus());
        return response;
    }
    
    private String getSwitchMapping(String DPID) {
        return getSwitchMap(DPID);
    }
    private String getProtocolType(String resourceName) {
        return getProtocol(resourceName);
    }
    
    /**
     * Request the resource name in OpenNaaS given DPID
     *
     * @param DPID
     * @return the resource name
     */
    private String getSwitchMap(String DPID) {
        log.info("Calling insert Route Table service");
        String response;
        String url = "http://localhost:8888/opennaas/vrf/routemgt/findMappingDPID/" + DPID;

        WebClient client = WebClient.create(url);
        String base64encodedUsernameAndPassword = Utils.base64Encode(username + ":" + password);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);

        response = client.accept(MediaType.TEXT_PLAIN).get(String.class);

        log.error("Insert to other Bundle Response: " + response);
        return response;
    }
    
    private String getProtocol(String resourceName) {
        log.info("Calling insert Route Table service");
        String response;
        String url = "http://localhost:8888/opennaas/vrf/routemgt/findProtocolType/" + resourceName;

        WebClient client = WebClient.create(url);
        String base64encodedUsernameAndPassword = Utils.base64Encode(username + ":" + password);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);

        response = client.accept(MediaType.TEXT_PLAIN).get(String.class);

        log.error("Insert to other Bundle Response: " + response);
        return response;
    }
    private IResource getIResource(String resourceName){
        try {
            IResourceManager resourceManager = org.opennaas.extensions.genericnetwork.Activator.getResourceManagerService();
            IResourceIdentifier resourceId = null;
            try {
                resourceId = resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName);
            } catch (ResourceException ex) {
                Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (resourceId == null) {
                log.error("IResource id is null.");
                return null;
            }
            try {
                return resourceManager.getResource(resourceId);
            } catch (ResourceException ex) {
                Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ActivatorException ex) {
            Logger.getLogger(DijkstraRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private TopologyInfo getTopology(int staticDijkstraCost) {
        log.info("Calling VTN from Static Routing.");
        String url = "http://localhost:8888/opennaas/genericnetwork/" + netResourceName + "/nettopology/topology";
        Topology response;
        String base64encodedUsernameAndPassword = Utils.base64Encode(username + ":" + password);
        WebClient client = WebClient.create(url);
        client.header("Authorization", "Basic " + base64encodedUsernameAndPassword);
        response = client.accept(MediaType.APPLICATION_XML).get(Topology.class);
        return UtilsTopology.createAdjacencyMatrix(response, staticDijkstraCost);
    }
}
