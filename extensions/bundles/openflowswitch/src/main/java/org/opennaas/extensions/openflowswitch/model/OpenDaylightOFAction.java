package org.opennaas.extensions.openflowswitch.model;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch
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

@XmlAccessorType(XmlAccessType.FIELD)
public class OpenDaylightOFAction {

    public static final String TYPE_OUTPUT = "OUTPUT";
    public static final String TYPE_DROP = "DROP";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_CONTROLLER = "controller";
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_INGRESS_PORT = "ingress-port";
    public static final String TYPE_NORMAL = "normal";
    public static final String TYPE_FLOOD = "flood";
    public static final String TYPE_ENQUEUE = "enqueue";
    public static final String TYPE_STRIP_VLAN = "strip-vlan";
    public static final String TYPE_SET_VLAN_ID = "set-vlan-id";
    public static final String TYPE_SET_VLAN_PRIORITY = "set-vlan-priority";
    public static final String TYPE_SET_SRC_MAC = "set-src-mac";
    public static final String TYPE_SET_DST_MAC = "set-dst-mac";
    public static final String TYPE_TOS_BITS = "set-tos-bits";
    public static final String TYPE_SET_SRC_IP = "set-src-ip";
    public static final String TYPE_SET_DST_IP = "set-dst-ip";
    public static final String TYPE_SET_SRC_PORT = "set-src-port";
    public static final String TYPE_SET_DST_PORT = "set-dst-port";

    /**
     * OpenDaylightOFAction type
     *
     * @see <a
     * href="http://docs.projectfloodlight.org/display/floodlightcontroller/Static+Flow+Pusher+API+%28New%29">Floodlight
     * documentation: Static Flow Pusher API (New)</a>
     */
    protected String type;

    protected String value;

    /**
     * Deafult constructor
     */
    public OpenDaylightOFAction() {
    }

    /**
     * Copy constructor
     *
     * @param floodlightOFAction FloodlightOFAction to get a copy
     */
    public OpenDaylightOFAction(OpenDaylightOFAction floodlightOFAction) {
        this.type = floodlightOFAction.type;
        this.value = floodlightOFAction.value;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OpenDaylightOFAction other = (OpenDaylightOFAction) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
