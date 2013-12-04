package org.opennaas.extensions.vrf.model;

import org.opennaas.extensions.vrf.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class VRFRoute {
    Log log = LogFactory.getLog(VRFRoute.class);
    private int id;
    private String sourceAddress;
    private String destinationAddress;
    private L2Forward switchInfo;
    private Long timeToLive;

    public VRFRoute(){
        
    }
    
    public VRFRoute(String sourceIp, String destIp, L2Forward SwitchInfo) {
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
        this.switchInfo = SwitchInfo;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
    
    public String getDestinationAddress(){
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public L2Forward getSwitchInfo() {
        return switchInfo;
    }

    public void setSwitchInfo(L2Forward switchInfo) {
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
        final VRFRoute other = (VRFRoute) obj;

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

    public boolean equalsOtherRoutes(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VRFRoute other = (VRFRoute) obj;
        
        String thisDst = this.getDestinationAddress();
        String otherDst = other.getDestinationAddress();
        if(!Utils.netMatch(thisDst, otherDst)){
            return false;
        }
         return true;
    }
}
