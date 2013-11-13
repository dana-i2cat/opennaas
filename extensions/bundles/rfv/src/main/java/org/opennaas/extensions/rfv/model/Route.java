package org.opennaas.extensions.rfv.model;

import org.opennaas.extensions.rfv.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Route {
 Log log = LogFactory.getLog(Route.class);
    private int id;
    private String sourceAddress;
    private String destinationAddress;
    private Switch switchInfo;
    private Long timeToLive;

    public Route() {
    }

    public Route(String sourceIp, String destIp, Switch SwitchInfo) {
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
        this.switchInfo = SwitchInfo;
    }
    
    public String getDestinationAddress(){
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
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

        String thisDst = this.getDestinationAddress();
        String otherDst = other.getDestinationAddress();

        log.debug("Equal ip, This: "+thisDst+" =? "+otherDst);
        if(!Utils.netMatch(thisDst, otherDst)){
                return false;
        }
        if (this.switchInfo.getMacAddress() != other.switchInfo.getMacAddress() && (this.switchInfo.getMacAddress() == null || !this.switchInfo.getMacAddress().equals(other.switchInfo.getMacAddress()))) {
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
    
    public boolean equalsOtherSubRoute(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Route other = (Route) obj;
        
        String thisDst = this.getDestinationAddress();
        String otherDst = other.getDestinationAddress();

        if(!Utils.netMatch(thisDst, otherDst)){
                return false;
        }
        if (this.switchInfo.getMacAddress() != other.switchInfo.getMacAddress() && (this.switchInfo.getMacAddress() == null || !this.switchInfo.getMacAddress().equals(other.switchInfo.getMacAddress()))) {
            return false;
        }
         return true;
    }
}
