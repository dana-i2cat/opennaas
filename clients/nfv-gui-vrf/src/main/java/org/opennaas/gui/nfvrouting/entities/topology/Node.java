package org.opennaas.gui.nfvrouting.entities.topology;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class Node {

    private int id;
    private String name;
    private String type;
    private String dpid;
    private String fixed = "true";
    private String ip;//used by hosts
    private String SW;//used by hosts
    private String port;//used by hosts

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDpid() {
        return dpid;
    }

    public void setDpid(String dpid) {
        this.dpid = dpid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSW() {
        return SW;
    }

    public void setSW(String SW) {
        this.SW = SW;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
    
    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

}
