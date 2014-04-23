package org.opennaas.extensions.vrf.model.topology;

/**
 * Vertex class. Used in Dijkstra algorithm. Id of the node, dpid/ip and the type var is used in order to differentite the switchs from hosts.
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Vertex {

    final private String id;
    final private String dpid;
    final private int type;

    public Vertex(String id, int type) {
        this.id = id;
        this.dpid = id;
        this.type = type;
    }

    public Vertex(String id, String dpid, int type) {
        this.id = id;
        this.dpid = dpid;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getDPID() {
        return dpid;
    }

    public int getType() {
        return type;
    }        

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return dpid;
    }

}
