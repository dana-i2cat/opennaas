package org.opennaas.extensions.ofrouting.model;

import java.net.InetAddress;

/**
 *
 * @author josep
 */
public class Route {

    private int id;
    private InetAddress sourceAddress;
    private InetAddress destinationAddress;
    private Switch switchInfo;
    private Long timeToLive;

    public Route() {
    }

    public Route(int id, InetAddress sourceIp, InetAddress destIp, Switch SwitchInfo) {
        this.id = id++;
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
        this.switchInfo = SwitchInfo;
    }

    public Route(InetAddress sourceIp, InetAddress destIp, Switch SwitchInfo) {
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
        this.switchInfo = SwitchInfo;
    }

    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(InetAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(InetAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Route other = (Route) obj;
        if ((this.sourceAddress == null) ? (other.sourceAddress != null) : !this.sourceAddress.equals(other.sourceAddress)) {
            return false;
        }
        if ((this.destinationAddress == null) ? (other.destinationAddress != null) : !this.destinationAddress.equals(other.destinationAddress)) {
            return false;
        }
        if (this.switchInfo != other.switchInfo && (this.switchInfo == null || !this.switchInfo.equals(other.switchInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.sourceAddress != null ? this.sourceAddress.hashCode() : 0);
        hash = 43 * hash + (this.destinationAddress != null ? this.destinationAddress.hashCode() : 0);
        hash = 43 * hash + (this.switchInfo != null ? this.switchInfo.hashCode() : 0);
        return hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
