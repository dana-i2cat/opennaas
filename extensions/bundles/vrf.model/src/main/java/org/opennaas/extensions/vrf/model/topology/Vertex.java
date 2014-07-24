package org.opennaas.extensions.vrf.model.topology;

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

/**
 * Vertex class. Used in Dijkstra algorithm. Id of the node, dpid/ip and the type var is used in order to differentite the switchs from hosts.
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class Vertex {

    final private String id;
    final private String name;
    final private String dpid;
    final private int type;

    public Vertex(String id, int type) {
        this.id = id;
        this.name = id;
        this.dpid = id;
        this.type = type;
    }

    public Vertex(String id, String dpid, int type) {
        this.id = id;
        this.name = dpid;
        this.dpid = dpid;
        this.type = type;
    }
    
    public Vertex(String id, String name, String dpid, int type) {
        this.id = id;
        this.name = name;
        this.dpid = dpid;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getDPID() {
        return dpid;
    }

    public int getType() {
        return type;
    }        

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

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
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return dpid;
    }

}
