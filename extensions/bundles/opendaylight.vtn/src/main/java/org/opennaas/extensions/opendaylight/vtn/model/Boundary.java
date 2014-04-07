package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class Boundary {

    private String boundary_id;
    private Link link;

    public String getBoundary_id() {
        return boundary_id;
    }

    public void setBoundary_id(String boundary_id) {
        this.boundary_id = boundary_id;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    

}
