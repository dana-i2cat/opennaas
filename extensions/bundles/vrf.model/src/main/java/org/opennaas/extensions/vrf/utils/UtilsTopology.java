package org.opennaas.extensions.vrf.utils;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Model
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
import com.google.common.collect.BiMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.genericnetwork.model.topology.Host;
import org.opennaas.extensions.genericnetwork.model.topology.Link;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.genericnetwork.model.topology.Switch;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.vrf.model.VRFRoute;
import org.opennaas.extensions.vrf.model.topology.Edge;
import org.opennaas.extensions.vrf.model.topology.TopologyInfo;
import org.opennaas.extensions.vrf.model.topology.Vertex;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class UtilsTopology {
    static Log log = LogFactory.getLog(Utils.class);

    /**
     * Read the json file that contains the topology and store the nodes and
     * edges in the global variables.
     *
     * @param topologyFilename
     * @param dijkstraCost
     * @return
     */
    public static TopologyInfo createAdjacencyMatrix(String topologyFilename, int dijkstraCost) {
        List<Vertex> nodes = new ArrayList<Vertex>();
        List<Edge> edges = new ArrayList<Edge>();

        try {
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createJsonParser(new File(topologyFilename));
            JsonToken current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                Logger.getLogger(UtilsTopology.class.getName()).log(Level.SEVERE, null, "Error: root should be object: quiting.");
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
                            int srcPort = Integer.parseInt(link.get("srcP").getValueAsText());
                            int dstPort = Integer.parseInt(link.get("dstP").getValueAsText());
                            Vertex srcV = null;
                            Vertex dstV = null;
                            for (Vertex v : nodes) {
                                if (v.getId().equals(srcId)) {
                                    srcV = new Vertex(v.getId(), v.getDPID(), v.getType());
                                } else if (v.getId().equals(dstId)) {
                                    dstV = new Vertex(v.getId(), v.getDPID(), v.getType());
                                }
                            }
                            Edge e = new Edge(link.get("id").getValueAsText(), srcV, dstV, dijkstraCost, srcPort, dstPort);
                            edges.add(e);
                            e = new Edge(link.get("id").getValueAsText() + "-", dstV, srcV, dijkstraCost, dstPort, srcPort);
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
            Logger.getLogger(UtilsTopology.class.getName()).log(Level.SEVERE, null, ex);
        }
        TopologyInfo topoInfo = new TopologyInfo(nodes, edges);
        return topoInfo;
    }

    public static TopologyInfo createAdjacencyMatrix(Topology topology, int dijkstraCost) {
        List<Vertex> nodes = new ArrayList<Vertex>();
        List<Edge> edges = new ArrayList<Edge>();
        int i = 0;
        for (NetworkElement nE : topology.getNetworkElements()) {
            Vertex v = null;
            if (nE instanceof Host) {
                v = new Vertex(Integer.toString(i), nE.getId(), ((Host) nE).getIp(), 1);
            } else if (nE instanceof Switch) {
                v = new Vertex(Integer.toString(i), nE.getId(), ((Switch) nE).getId(), 0);
            }
            nodes.add(v);
            i++;
        }
        BiMap<String, DevicePortId> mappingPortElem = topology.getNetworkDevicePortIdsMap();
        i = 0;
        for (Link l : topology.getLinks()) {
            String srcPortName = l.getSrcPort().getId();
            String dstPortName = l.getDstPort().getId();
            int srcPort = Integer.parseInt(mappingPortElem.get(srcPortName).getDevicePortId());
            int dstPort = Integer.parseInt(mappingPortElem.get(dstPortName).getDevicePortId());
            Vertex srcV = null;
            Vertex dstV = null;
            for (Vertex v : nodes) {
                if (v.getName().equals(mappingPortElem.get(srcPortName).getDeviceId())) {
                    srcV = new Vertex(v.getId(), v.getDPID(), v.getType());
                } else if (v.getName().equals(mappingPortElem.get(dstPortName).getDeviceId())) {
                    dstV = new Vertex(v.getId(), v.getDPID(), v.getType());
                }
            }
            Edge e = new Edge(Integer.toString(i), srcV, dstV, dijkstraCost, srcPort, dstPort);
            edges.add(e);
            i++;
            e = new Edge(Integer.toString(i), dstV, srcV, dijkstraCost, dstPort, srcPort);
            edges.add(e);
            i++;
        }

        TopologyInfo topoInfo = new TopologyInfo(nodes, edges);
        return topoInfo;
    }

    public static List<VRFRoute> moveValueAtIndexToEnd(List<VRFRoute> arrayToBeShifted, int index) {
        VRFRoute valueBeingMoved = arrayToBeShifted.get(index);

        for (int i = index; i < arrayToBeShifted.size() - 1; i++) {
            arrayToBeShifted.set(i, arrayToBeShifted.get(i + 1));
        }
        arrayToBeShifted.set(arrayToBeShifted.size() - 1, valueBeingMoved);
        return arrayToBeShifted;
    }
}
