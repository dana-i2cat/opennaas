package org.opennaas.extensions.ofrouting.model;

/**
 *
 * @author josep
 */
public class RouteSubnet {

    private int id;
    private Subnet sourceSubnet;
    private Subnet destSubnet;
    private Switch switchInfo;
    private Long timeToLive;

    public RouteSubnet() {
    }

    public RouteSubnet(int id, Subnet sourceSubnet, Subnet destSubnet, Switch SwitchInfo) {
        this.id = id++;
        this.sourceSubnet = sourceSubnet;
        this.destSubnet = destSubnet;
        this.switchInfo = SwitchInfo;
    }

    public RouteSubnet(Subnet sourceSubnet, Subnet destSubnet, Switch SwitchInfo) {
        this.sourceSubnet = sourceSubnet;
        this.destSubnet = destSubnet;
        this.switchInfo = SwitchInfo;
    }

    public Switch getSwitchInfo() {
        return switchInfo;
    }

    public void setSwitchInfo(Switch switchInfo) {
        this.switchInfo = switchInfo;
    }

    public Long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Subnet getDestSubnet() {
        return destSubnet;
    }

    public void setDestSubnet(Subnet destSubnet) {
        this.destSubnet = destSubnet;
    }

    public Subnet getSourceSubnet() {
        return sourceSubnet;
    }

    public void setSourceSubnet(Subnet sourceSubnet) {
        this.sourceSubnet = sourceSubnet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteSubnet other = (RouteSubnet) obj;

        if (this.sourceSubnet != other.sourceSubnet && (this.sourceSubnet == null || !this.sourceSubnet.equals(other.sourceSubnet))) {
            return false;
        }
        if (this.destSubnet != other.destSubnet && (this.destSubnet == null || !this.destSubnet.equals(other.destSubnet))) {
            return false;
        }
        /*        if (this.switchInfo != other.switchInfo && (this.switchInfo == null || !this.switchInfo.equals(other.switchInfo))) {
        return false;
        }
         */ return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.sourceSubnet != null ? this.sourceSubnet.hashCode() : 0);
        hash = 11 * hash + (this.destSubnet != null ? this.destSubnet.hashCode() : 0);
        hash = 11 * hash + (this.switchInfo != null ? this.switchInfo.hashCode() : 0);
        return hash;
    }
}
