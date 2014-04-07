package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class VTN {

    private String vtn_name;
    private String description;

    public String getVtn_name() {
        return vtn_name;
    }

    public void setVtn_name(String vtn_name) {
        this.vtn_name = vtn_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.vtn_name != null ? this.vtn_name.hashCode() : 0);
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
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
        final VTN other = (VTN) obj;
        if ((this.vtn_name == null) ? (other.vtn_name != null) : !this.vtn_name.equals(other.vtn_name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        return true;
    }

}
