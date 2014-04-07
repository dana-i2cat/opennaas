
package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class OpenDaylightvBridge {
    
    private String vbr_name;
    private String controller_id;
    private String description;
    private String domain_id;

    public String getVbr_name() {
        return vbr_name;
    }

    public void setVbr_name(String vbr_name) {
        this.vbr_name = vbr_name;
    }

    public String getController_id() {
        return controller_id;
    }

    public void setController_id(String controller_id) {
        this.controller_id = controller_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain_id() {
        return domain_id;
    }

    public void setDomain_id(String domain_id) {
        this.domain_id = domain_id;
    }
    
    
    
}
