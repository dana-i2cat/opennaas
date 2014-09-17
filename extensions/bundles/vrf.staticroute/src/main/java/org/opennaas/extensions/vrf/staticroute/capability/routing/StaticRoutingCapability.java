package org.opennaas.extensions.vrf.staticroute.capability.routing;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Static Routing
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;
import org.opennaas.extensions.vrf.model.L2Forward;
import org.opennaas.extensions.vrf.model.VRFModel;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.model.topology.Edge;
import org.opennaas.extensions.vrf.model.topology.TopologyInfo;
import org.opennaas.extensions.vrf.model.topology.Vertex;
import org.opennaas.extensions.vrf.staticroute.capability.routemgt.StaticRouteMgtCapability;
import org.opennaas.extensions.vrf.utils.Utils;
import org.opennaas.extensions.vrf.utils.UtilsTopology;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
public class StaticRoutingCapability implements IStaticRoutingCapability {

    Log log = LogFactory.getLog(StaticRoutingCapability.class);
    private final VRFModel vrfModel;
    private final String username = "admin";
    private final String password = "123456";
    private String netResourceName = "ofnet1";//default value. Usin REST can be changed
    private List<Vertex> nodes = new ArrayList<Vertex>();
    private List<Edge> edges = new ArrayList<Edge>();
    private boolean vtn_enabled = false;

    public StaticRoutingCapability() {
        this.vrfModel = StaticRouteMgtCapability.getVRFModel();
    }

    public VRFModel getVRFModel() {
        return vrfModel;
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

        if (vrfModel.getTable(version) == null) {
            return Response.status(404).type("text/plain").entity("IP Table does not exist.").build();
        }

        log.error("Requested STATIC route: " + ipSource + " > " + ipDest + " " + switchDPID + ", inPort: " + inputPort + ". Proactive: " + proactive);
        L2Forward switchInfo = new L2Forward(inputPort, switchDPID);

        VRFRoute route = new VRFRoute(ipSource, ipDest, switchInfo);
        int routeId = vrfModel.getTable(version).RouteExists(route);
        if (routeId != 0) {
            outPortSrcSw = vrfModel.getTable(version).getOutputPort(routeId);
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
        List<VRFRoute> routeSubnetList = vrfModel.getTable(version).getListRoutes(route, route.getSwitchInfo(), route.getSwitchInfo());
        List<OFFlow> listFlow = new ArrayList<OFFlow>();

        if (edges.isEmpty()) {
            defineTopologyVars();
        }
        sortRoutes(routeSubnetList, route);//requires topology (Edges)
        //Conversion List of VRFRoute to List of OFFlow
        if (routeSubnetList.size() > 0) {
            for (VRFRoute r : routeSubnetList) {
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2048"));
                listFlow.add(Utils.VRFRouteToOFFlow(r, "2054"));
            }
        }
        //VTN OpenDaylight
        if (vtn_enabled) {
            String srcDPID = route.getSwitchInfo().getDPID();
            String inPort = Integer.toString(route.getSwitchInfo().getInputPort());//String inPort = listFlow.get(0).getMatch().getIngressPort();
            String dstDPID = listFlow.get(listFlow.size() - 1).getDPID();
            String outPort = listFlow.get(listFlow.size() - 1).getActions().get(0).getValue();
            Response response;
            String initialSw = getProtocolType(srcDPID);
            String targetSw = getProtocolType(dstDPID);
            if (initialSw.equals("opendaylight")) {
                response = callVTN(srcDPID, inPort);
            }
            if (targetSw.equals("opendaylight")) {
                response = callVTN(dstDPID, outPort);
            }
        }
        for (OFFlow flow : listFlow) {
            log.error("Provision Flow " + flow.getMatch().getSrcIp() + " " + flow.getMatch().getDstIp() + " " + flow.getDPID() + " " + flow.getActions().get(0).getType() + ": " + flow.getActions().get(0).getValue() + " " + flow.getPriority());
            insertFlow(flow);
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
        log.info("Provision OpenFlow Flow Link");
        try {
            String resourceName = getSwitchMapping(flow.getDPID());
            String protocol = getProtocolType(resourceName);
            if (protocol == null) {
                return Response.ok("Protocol is null").build();
            }
            IResource resource = Utils.getIResource(resourceName);
            if (resource == null) {
                return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
            }
            IOpenflowForwardingCapability forwardingCapability;// = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
            if (protocol.equals("opendaylight")) {
                if (!flow.getMatch().getEtherType().equals("2054") && !flow.getMatch().getEtherType().equals("0x0806")) {
                    forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
                    OpenDaylightOFFlow odlFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToODL(flow);
                    forwardingCapability.createOpenflowForwardingRule(odlFlow);
                }
            } else if (protocol.equals("floodlight")) {
                forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
                FloodlightOFFlow fldFlow = org.opennaas.extensions.openflowswitch.utils.Utils.OFFlowToFLD(flow);
                forwardingCapability.createOpenflowForwardingRule(fldFlow);
            }
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
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
        Boolean set = true;
        int maxLoop = routes.size() * 4;
        for (int j = 0; j < routes.size(); j++) {
            if (nodeSrc.equals(routes.get(j).getSwitchInfo().getDPID())) {
                //the defined routes contains two directions. It is possible that there are two routes with the same DPID. Then, we add to sort routes
                set = false;
            } else {
                for (Edge edge : edges) {
                    //find the dest node given a source node. Initial node is the source host
                    if ((edge.getSource().getDPID().equals(nodeSrc) && edge.getDestination().getDPID().equals(routes.get(j).getSwitchInfo().getDPID())) || (edge.getDestination().getDPID().equals(nodeSrc) && edge.getSource().getDPID().equals(routes.get(j).getSwitchInfo().getDPID()))) {
                        nodeSrc = routes.get(j).getSwitchInfo().getDPID();
                        set = false;
                        break;
                    } else {
                        //if not exists a match, move the dpid to the final of the array in order to analyze later
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
            if (maxLoop <= 0) {//avoid infinite bucles
                j++;
            }
        }
    }

    /**
     * Request the resource name in OpenNaaS given DPID
     *
     * @param DPID
     * @return the resource name
     */
    private String getSwitchMapping(String DPID) {
        return StaticRouteMgtCapability.getSwitchMapping(DPID);
    }

    private String getProtocolType(String resourceName) {
        return StaticRouteMgtCapability.getProtocolType(resourceName);
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

    @Override
    public Response setGenNetResource(String resourceName) {
        netResourceName = resourceName;
        return Response.ok("Resource name changed.").build();
    }

    private void defineTopologyVars() {
        TopologyInfo topInfo = getTopology(1);
        edges = topInfo.getEdges();
        nodes = topInfo.getNodes();
    }
}
