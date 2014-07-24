package org.opennaas.extensions.vrf.selector.capability;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Selector
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
@Path("/")
public interface IRoutingCapability {

    /**
     * Get Route given Destination IP, DPID of the switch and the input
     * port where the packet entry in the switch
     *
     * @param ipSource Source IP Address in integer format (received from Floodlight)
     * @param ipDest Destination IP Address in integer format (received from Floodlight)
     * @param switchDPID DPID of the switch
     * @param inputPort Input port
     * @return output Port where the switch forward the packet
     */
    @Path("/route/{ipSource}/{ipDest}/{switchDPID}/{inputPort}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoute(@PathParam("ipSource") String ipSource,
            @PathParam("ipDest") String ipDest,
            @PathParam("switchDPID") String switchDPID,
            @PathParam("inputPort") int inputPort);

    /**
     * Set the working mode of the selector (static or dijkstra)
     *
     * @param mode
     * @return response code
     */
    @Path("/routeMode/{mode}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response setSelectorMode(@PathParam("mode") String mode);

   /**
     * Get the working mode of the selector (static or dijkstra)
     *
     * @return routing Mode
     */
    @Path("/routeMode")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSelectorMode();

}
