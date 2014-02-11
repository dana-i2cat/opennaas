package org.opennaas.extensions.vrf.dijkstraroute.model.dijkstra;

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
