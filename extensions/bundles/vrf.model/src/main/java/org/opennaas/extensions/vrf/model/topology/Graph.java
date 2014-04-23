package org.opennaas.extensions.vrf.model.topology;

import java.util.List;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */

public class Graph {

    private final List<Vertex> vertexes;
    private final List<Edge> edges;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

}
