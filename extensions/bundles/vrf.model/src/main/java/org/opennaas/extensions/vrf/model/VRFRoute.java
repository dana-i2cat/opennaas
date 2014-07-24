package org.opennaas.extensions.vrf.model;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.vrf.utils.Utils;
/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
@XmlRootElement(name="VRFRoute")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class VRFRoute{
    
    Log log = LogFactory.getLog(VRFRoute.class);
    private int id;
    private String name;
    private String sourceAddress;
    private String destinationAddress;
    private L2Forward switchInfo;
    private String type;
    private long lifeTime;

    public VRFRoute(){
        
    }
    
   public VRFRoute(String name){
       this.name = name;
   }
    
    public VRFRoute(String sourceIp, String destIp, L2Forward SwitchInfo) {
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
        this.switchInfo = SwitchInfo;
    }
    
    public VRFRoute(String sourceIp, String destIp, L2Forward SwitchInfo, long lifeTime) {
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
        this.switchInfo = SwitchInfo;
        this.lifeTime = lifeTime;
    }
    
     public VRFRoute(String sourceIp, String destIp) {
        this.sourceAddress = sourceIp;
        this.destinationAddress = destIp;
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
        return lifeTime;
    }

    public void setTimeToLive(Long timeToLive) {
        this.lifeTime = timeToLive;
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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VRFRoute other = (VRFRoute) obj;

        String thisSrc = this.getSourceAddress();
        String otherSrc = other.getSourceAddress();

        log.debug("Equal source ip, This: "+thisSrc+" =? "+otherSrc);
        if(!Utils.netMatch(thisSrc, otherSrc)){
            return false;
        }
        
        String thisDst = this.getDestinationAddress();
        String otherDst = other.getDestinationAddress();

        log.debug("Equal destination ip, This: "+thisDst+" =? "+otherDst);
        if(!Utils.netMatch(thisDst, otherDst)){
            return false;
        }
        if (this.switchInfo.getDPID() != other.switchInfo.getDPID() && (this.switchInfo.getDPID() == null || !this.switchInfo.getDPID().equals(other.switchInfo.getDPID()))) {
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

    /**
     * Try to match Subnet addr with destination addr.
     * @param obj
     * @return 
     */
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
        
        String thisSrc = this.getSourceAddress();
        String otherSrc = other.getSourceAddress();
        if(!Utils.netMatch(thisSrc, otherSrc)){
            return false;
        }
        
         return true;
    }
}
