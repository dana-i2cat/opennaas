package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class OpenDaylightController {

    private String controller_id;
    private String description;
    private String ipaddr;
    private String type;
    private String auditstatus;
    private String username;
    private String password;
    private String version;
    private String actual_version;
    private String operstatus;

    public OpenDaylightController(String controller_id, String ipaddr, String type, String version, String operstatus, String description) {
        this.controller_id = controller_id;
        this.description = description;
        this.ipaddr = ipaddr;
        this.type = type;
        this.version = version;
        this.operstatus = operstatus;
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

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuditstatus() {
        return auditstatus;
    }

    public void setAuditstatus(String auditstatus) {
        this.auditstatus = auditstatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getActual_version() {
        return actual_version;
    }

    public void setActual_version(String actual_version) {
        this.actual_version = actual_version;
    }

    public String getOperstatus() {
        return operstatus;
    }

    public void setOperstatus(String operstatus) {
        this.operstatus = operstatus;
    }
}
