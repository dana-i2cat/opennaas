package org.opennaas.extensions.vrf.model.topology;

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

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Edge {

    private final String id;
    private final Vertex source;
    private final Vertex destination;
    private final int weight;
    private final int srcPort;
    private final int dstPort;

    public Edge(String id, Vertex source, Vertex destination, int weight, int srcPort, int dstPort) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }

    public String getId() {
        return id;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination + "" + srcPort + "" +dstPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public int getSrcPort() {
        return srcPort;
    }
}
