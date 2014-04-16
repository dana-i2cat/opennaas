package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class BoundaryMap {

    private String boundary_id;
    private String vlan_id;

    public BoundaryMap(String boundary_id, String vlan_id) {
        this.boundary_id = boundary_id;
        this.vlan_id = vlan_id;
    }

    public BoundaryMap() {
    }

    public String getBoundary_id() {
        return boundary_id;
    }

    public void setBoundary_id(String boundary_id) {
        this.boundary_id = boundary_id;
    }

    public String getVlan_id() {
        return vlan_id;
    }

    public void setVlan_id(String vlan_id) {
        this.vlan_id = vlan_id;
    }
}
