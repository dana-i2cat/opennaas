package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class vLink {

    private String vlk_name;
    private String vnode1_name;
    private String if1_name;
    private String vnode2_name;
    private String if2_name;
    private BoundaryMap boundaryMap;

    public String getVlk_name() {
        return vlk_name;
    }

    public void setVlk_name(String vlk_name) {
        this.vlk_name = vlk_name;
    }

    public String getVnode1_name() {
        return vnode1_name;
    }

    public void setVnode1_name(String vnode1_name) {
        this.vnode1_name = vnode1_name;
    }

    public String getIf1_name() {
        return if1_name;
    }

    public void setIf1_name(String if1_name) {
        this.if1_name = if1_name;
    }

    public String getVnode2_name() {
        return vnode2_name;
    }

    public void setVnode2_name(String vnode2_name) {
        this.vnode2_name = vnode2_name;
    }

    public String getIf2_name() {
        return if2_name;
    }

    public void setIf2_name(String if2_name) {
        this.if2_name = if2_name;
    }

    public BoundaryMap getBoundaryMap() {
        return boundaryMap;
    }

    public void setBoundaryMap(BoundaryMap boundaryMap) {
        this.boundaryMap = boundaryMap;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final vLink other = (vLink) obj;
        if ((this.vlk_name == null) ? (other.vlk_name != null) : !this.vlk_name.equals(other.vlk_name)) {
            return false;
        }
        if ((this.vnode1_name == null) ? (other.vnode1_name != null) : !this.vnode1_name.equals(other.vnode1_name)) {
            return false;
        }
        if ((this.if1_name == null) ? (other.if1_name != null) : !this.if1_name.equals(other.if1_name)) {
            return false;
        }
        if ((this.vnode2_name == null) ? (other.vnode2_name != null) : !this.vnode2_name.equals(other.vnode2_name)) {
            return false;
        }
        if ((this.if2_name == null) ? (other.if2_name != null) : !this.if2_name.equals(other.if2_name)) {
            return false;
        }
        if (this.boundaryMap != other.boundaryMap && (this.boundaryMap == null || !this.boundaryMap.equals(other.boundaryMap))) {
            return false;
        }
        return true;
    }

}
