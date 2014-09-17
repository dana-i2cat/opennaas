package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: OpenDaylight
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

import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.wrappers.OpenDaylightOFFlowsWrapper;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

@Path("/controller/nb/v2/flowprogrammer/default")
public interface IOpenDaylightStaticFlowPusherClient {

    /**
     * Adds a static flow.
     *
     * @param flow The flow to push.
     * @param DPID
     * @param name
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     */
    @Path("/node/OF/{DPID}/staticFlow/{name}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)//content-type
    @Produces(MediaType.APPLICATION_JSON)
    public void addFlow(OpenDaylightOFFlow flow, @PathParam("DPID") String DPID, @PathParam("name") String name) throws ProtocolException, Exception;

    /**
     * Deletes a static flow
     *
     * @param DPID
     * @param name
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     * @throws java.lang.Exception
     */
    @Path("/node/OF/{DPID}/staticFlow/{name}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void deleteFlow(@PathParam("DPID") String DPID, @PathParam("name") String name) throws ProtocolException, Exception;

    /**
     * Gets a list of flows by switch
     *
     * @param dpid
     * @return
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     */
    @Path("/node/OF/{DPID}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public OpenDaylightOFFlowsWrapper getFlows(@PathParam("DPID") String dpid) throws ProtocolException, Exception;

    /**
     * Get static flow.
     *
     * @param DPID
     * @param name
     * @return 
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     */
    @Path("/node/OF/{DPID}/staticFlow/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OpenDaylightOFFlow getFlow(@PathParam("DPID") String DPID, @PathParam("name") String name) throws ProtocolException, Exception;
    
    
    /**
     * Gets all list of all flows
     * @return 
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     */
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<OpenDaylightOFFlow>> getFlows() throws ProtocolException, Exception;

    /**
     * Deletes all static flows for a particular switch
     *
     * @param dpid The DPID of the switch to delete flows for.
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     */
    @Path("clear/{switchId}/json")
    public void deleteFlowsForSwitch(@PathParam("switchId") String dpid) throws ProtocolException, Exception;

    /**
     * Deletes all flows.
     * @throws org.opennaas.core.resources.protocol.ProtocolException
     */
    @Path("clear/all/json")
    public void deleteAllFlows() throws ProtocolException, Exception;
}
