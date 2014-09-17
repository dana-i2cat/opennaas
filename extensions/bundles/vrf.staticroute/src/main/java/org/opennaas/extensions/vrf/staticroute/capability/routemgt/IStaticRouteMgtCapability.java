package org.opennaas.extensions.vrf.staticroute.capability.routemgt;

/*
 * #%L
 * OpenNaaS :: Virtual Routing Function :: Static Routing
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
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josep Batallé (josep.batalle@i2cat.net)
 *
 */
@Path("/")
public interface IStaticRouteMgtCapability {

    /**
     * Insert new Route
     *
     * @param ipSource in String format
     * @param ipDest in String format
     * @param switchDPID
     * @param inputPort
     * @param outputPort
     * @param lifeTime
     * @return status
     */
    @Path("/route")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchDPID") String switchDPID,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort,
            @FormParam("lifeTime") int lifeTime);

    /**
     * Insert Routes from json file
     *
     * @param fileName The name of the file
     * @param viDescription
     * @return Status of the request.
     */
    @Path("/insertRouteFromFile/{fileName}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRouteFile(@PathParam("fileName") String fileName, InputStream viDescription);

    /**
     * Remove route given id of the route and the IP version
     *
     * @param id Identificator of the route in OpenNaaS
     * @param version IP version of this route
     * @return Status
     */
    @Path("/route")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@QueryParam("id") int id,
            @QueryParam("version") int version);

    /**
     * Remove route given all parameters of the route
     *
     * @param ipSource in String format
     * @param ipDest in String format
     * @param switchDPID
     * @param inputPort
     * @param outputPort
     * @return status
     */
    @Path("/routeParam")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoute(@FormParam("ipSource") String ipSource,
            @FormParam("ipDest") String ipDest,
            @FormParam("switchDPID") String switchDPID,
            @FormParam("inputPort") int inputPort,
            @FormParam("outputPort") int outputPort);

    /**
     * Remove all routes
     *
     * @return status
     */
    @Path("/routes")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeRoutes();

    /**
     * Get the entire Model
     *
     * @return json with the list of routes
     */
    @Path("/routes")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes();

    /**
     * Get the Route Table of specific IP version
     *
     * @param version Version of IP (4 or 6)
     * @return json with the list of the required table
     */
    @Path("/routes/{version}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes(@PathParam("version") int version);

    /**
     * Get Routes where the IP source/target appears. Used in GUI
     *
     * @param version Version of IP (4 or 6)
     * @param ipSrc
     * @param ipDst
     * @return json with the list of the required table
     */
    @Path("/routes/{version}/{ipSrc}/{ipDst}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRoutes(@PathParam("version") int version, @PathParam("ipSrc") String ipSrc, @PathParam("ipDst") String ipDst);

    /**
     * Called from dynamic routing bundle
     *
     * @param route
     * @return
     */
    @Path("/dynamic-route")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertRoute(String route);

    /**
     * Auto-learning DPID - resourceName used in OpenNaaS
     *
     * @param DPID
     * @return
     */
    @Path("/findMappingDPID/{DPID}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response findMappingDPIDAPI(@PathParam("DPID") String DPID);

    /**
     *
     * @param resourceName
     * @return
     */
    @Path("/findProtocolType/{resourceName}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response findProtocolTypeAPI(@PathParam("resourceName") String resourceName);

}
