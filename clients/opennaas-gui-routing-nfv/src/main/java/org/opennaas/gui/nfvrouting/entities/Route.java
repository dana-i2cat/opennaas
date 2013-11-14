package org.opennaas.gui.nfvrouting.entities;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Route {

    @NotEmpty
    protected int id;
    @NotBlank(message = "{message.error.field.mandatory}")
    @NotEmpty
    private String sourceAddress;
    @NotEmpty
    private String destinationAddress;
    @NotEmpty
    private Switch switchInfo;
    private Long timeToLive;
    
    public Route (){
        this.switchInfo = new Switch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
    public String getDestinationAddress() {
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
}
