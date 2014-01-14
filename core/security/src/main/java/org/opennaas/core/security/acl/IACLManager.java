package org.opennaas.core.security.acl;

/*
 * #%L
 * OpenNaaS :: Core :: Security
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IACLManager {

	@Path("/secureResource/{resourceId}/user/{user}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public void secureResource(@PathParam("resourceId") String resourceId, @PathParam("user") String user);

	@Path("/isResourceAccessible/{resourceId}")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Boolean isResourceAccessible(@PathParam("resourceId") String resourceId);
}
