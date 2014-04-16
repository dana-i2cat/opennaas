package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class Link {

    private String controller1_id;
    private String domain1_id;
    private String logical_port1_id;
    private String controller2_id;
    private String domain2_id;
    private String logical_port2_id;

    public Link(String controller1_id, String domain1_id, String logical_port1_id, String controller2_id, String domain2_id, String logical_port2_id) {
        this.controller1_id = controller1_id;
        this.domain1_id = domain1_id;
        this.logical_port1_id = logical_port1_id;
        this.controller2_id = controller2_id;
        this.domain2_id = domain2_id;
        this.logical_port2_id = logical_port2_id;
    }

    public Link() {
    }

    public String getController1_id() {
        return controller1_id;
    }

    public void setController1_id(String controller1_id) {
        this.controller1_id = controller1_id;
    }

    public String getDomain1_id() {
        return domain1_id;
    }

    public void setDomain1_id(String domain1_id) {
        this.domain1_id = domain1_id;
    }

    public String getLogical_port1_id() {
        return logical_port1_id;
    }

    public void setLogical_port1_id(String logical_port1_id) {
        this.logical_port1_id = logical_port1_id;
    }

    public String getController2_id() {
        return controller2_id;
    }

    public void setController2_id(String controller2_id) {
        this.controller2_id = controller2_id;
    }

    public String getDomain2_id() {
        return domain2_id;
    }

    public void setDomain2_id(String domain2_id) {
        this.domain2_id = domain2_id;
    }

    public String getLogical_port2_id() {
        return logical_port2_id;
    }

    public void setLogical_port2_id(String logical_port2_id) {
        this.logical_port2_id = logical_port2_id;
    }
}
