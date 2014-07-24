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

import java.util.ArrayList;
import java.util.List;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class VRFModel implements IModel{
    
    private RoutingTable ipv4;
    private RoutingTable ipv6;

    @Override
    public List<String> getChildren() {
        return new ArrayList<String>();
    }

    @Override
    public String toXml() throws SerializationException {
        return ObjectSerializer.toXml(this);
    }

    public RoutingTable getTable(int version) {
        if (version == 4)
            return ipv4;
        else if (version == 6)
            return ipv6;
        return null;
    }

    public void setTable(RoutingTable table, int version) {
        if (version == 4)
            this.ipv4 = table;
        else if (version == 6)
            this.ipv6 = table;
    }

    public RoutingTable getIpv4() {
        return ipv4;
    }

    public void setIpv4(RoutingTable ipv4) {
        this.ipv4 = ipv4;
    }

    public RoutingTable getIpv6() {
        return ipv6;
    }

    public void setIpv6(RoutingTable ipv6) {
        this.ipv6 = ipv6;
    }
}
