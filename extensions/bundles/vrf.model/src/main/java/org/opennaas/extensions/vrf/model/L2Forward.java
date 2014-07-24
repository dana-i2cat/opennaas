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
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the fordwarding in a switch.
 * Forward packets in a Switch (layer2). Packet from input port to output port.
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
@XmlRootElement(name="L2Forward")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class L2Forward{

    private int numberPorts;//not used
    private List<String> listPorts = new ArrayList<String>();//not used
    private int inputPort;
    private int outputPort;
    private String dpid;

    public L2Forward(){
        
    }
    public L2Forward(String port, int inputPort, int outputPort, String macAddress) {
        listPorts.add(port);
        this.inputPort = inputPort;
        this.outputPort = outputPort;
        this.dpid = macAddress;
    }

    public L2Forward(int inputPort, String macAddress) {
        this.inputPort = inputPort;
        this.dpid = macAddress;
    }

    public String getDPID() {
        return dpid;
    }

    public void setDPID(String dpid) {
        this.dpid = dpid;
    }

    public List<String> getListPorts() {
        return listPorts;
    }

    public void setListPorts(List<String> listPorts) {
        this.listPorts = listPorts;
    }

    public int getNumberPorts() {
        return numberPorts;
    }

    public void setNumberPorts(int numberPorts) {
        this.numberPorts = numberPorts;
    }

    public int getInputPort() {
        return inputPort;
    }

    public void setInputPort(int inputPort) {
        this.inputPort = inputPort;
    }

    public int getOutputPort() {
        return outputPort;
    }

    public void setOutputPort(int outputPort) {
        this.outputPort = outputPort;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final L2Forward other = (L2Forward) obj;
        if (this.inputPort != other.inputPort) {
            return false;
        }
        if (this.outputPort != other.outputPort) {
            return false;
        }
        if ((this.dpid == null) ? (other.dpid != null) : !this.dpid.equals(other.dpid)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.numberPorts;
        hash = 29 * hash + (this.dpid != null ? this.dpid.hashCode() : 0);
        return hash;
    }
}
