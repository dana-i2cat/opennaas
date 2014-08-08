package org.opennaas.extensions.vrf.staticroute.capability.routemgt;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.OFFlow;
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
public class StaticRouteMgtCapability implements IStaticRouteMgtCapability {

    Log log = LogFactory.getLog(StaticRouteMgtCapability.class);
    private final static VRFModel vrfModel = new VRFModel();
    private final static Map<String, String> switchMapping = new HashMap<String, String>();
    private final static Map<String, String> protocolType = new HashMap<String, String>();

    public StaticRouteMgtCapability() {
        //this.vrfModel = new VRFModel();
        vrfModel.setTable(new RoutingTable(4), 4);
        vrfModel.setTable(new RoutingTable(6), 6);
    }

    public final static VRFModel getVRFModel() {
        return vrfModel;
    }

    public void setVRFModel(VRFModel VRFModel) {
//        this.vrfModel = VRFModel;
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
        log.error("Remove all routes");
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
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Insert dynamic route. Src: " + route.getSourceAddress() + " Dst: " + route.getDestinationAddress() + " OutPort: " + route.getSwitchInfo().getOutputPort());
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

        return Response.status(201).entity(model.getTable(version).addRoute(route)).build();
    }

    private Response removeFlow(OFFlow flow) {
        log.info("Remove Flow Link");
        String protocol;
        try {
            String resourceName = getSwitchMapping(flow.getDPID());
            protocol = getProtocolType(resourceName);
            IResource resource = Utils.getIResource(resourceName);
            if (protocol == null) {
                return Response.ok("Protocol is null").build();
            }if (resource == null) {
                return Response.serverError().entity("Does not exist a OFSwitch resource mapped with this switch Id").build();
            }
            IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource.getCapabilityByInterface(IOpenflowForwardingCapability.class);
            if (protocol.equals("opendaylight")) {
                forwardingCapability.removeOpenflowForwardingRule(flow.getName());
            } else if (protocol.equals("floodlight")) {
                forwardingCapability.removeOpenflowForwardingRule(flow.getName());
            }
        } catch (ResourceException ex) {
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok().build();
    }

    //given DPID returns resourceName and save this relation
    private static String autoLearningMapping(String DPID) {
        IResourceManager resourceManager = null;
        String resourceName = null;
        try {
            resourceManager = org.opennaas.extensions.genericnetwork.Activator.getResourceManagerService();
        } catch (ActivatorException ex) {
            Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
        }

        //TODO: handle possible null pointer
        List<IResource> listResources = resourceManager.listResourcesByType("openflowswitch");
        for (IResource r : listResources) {
            resourceName = r.getResourceDescriptor().getInformation().getName();
            IProtocolSessionManager sessionManager = null;
            try {
                sessionManager = getProtocolSessionManager(r.getResourceDescriptor().getId());
            } catch (Exception ex) {
                Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            IProtocolSession protocolSession;
            try {
                protocolSession = sessionManager.obtainSessionByProtocol("floodlight", true);

                Map<String, Object> t = protocolSession.getSessionContext().getSessionParameters();
                String extractedDPID = (String) t.get("protocol.floodlight.switchid");
                if (extractedDPID.equals(DPID)) {
                    switchMapping.put(DPID, resourceName);
                    protocolType.put(resourceName, "floodlight");
                    return resourceName;
                }

                protocolSession = sessionManager.obtainSessionByProtocol("opendaylight", true);

                t = protocolSession.getSessionContext().getSessionParameters();
                extractedDPID = (String) t.get("protocol.opendaylight.switchid");
                if (extractedDPID.equals(DPID)) {
                    switchMapping.put(DPID, resourceName);
                    protocolType.put(resourceName, "opendaylight");
                    return resourceName;
                }
            } catch (ProtocolException ex) {
                Logger.getLogger(StaticRouteMgtCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "No resource name with this DPID";
    }

    public static IProtocolSessionManager getProtocolSessionManager(String resourceId) throws Exception {
        IProtocolManager protocolManager = org.opennaas.core.resources.Activator.getProtocolManagerService();
        return protocolManager.getProtocolSessionManager(resourceId);
    }

    public final static String getSwitchMapping(String DPID) {
        Iterator<String> keySetIterator = switchMapping.keySet().iterator();

        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            if (key.equals(DPID)) {
                return switchMapping.get(key);
            }
        }
        return StaticRouteMgtCapability.autoLearningMapping(DPID);
    }

    public final static String getProtocolType(String resourceName) {
        Iterator<String> keySetIterator = protocolType.keySet().iterator();

        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            if (key.equals(resourceName)) {
                return protocolType.get(key);
            }
        }
        return "No protocol detected.";
    }

    @Override
    public Response findMappingDPIDAPI(String DPID) {
        return Response.ok().entity(getSwitchMapping(DPID)).build();
    }

    @Override
    public Response findProtocolTypeAPI(String resourceName) {
        return Response.ok().entity(getProtocolType(resourceName)).build();
    }
}
