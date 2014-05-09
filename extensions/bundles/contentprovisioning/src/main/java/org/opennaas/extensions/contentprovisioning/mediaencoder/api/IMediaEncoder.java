package org.opennaas.extensions.contentprovisioning.mediaencoder.api;

/*
 * #%L
 * OpenNaaS :: Content Provisioning
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.contentprovisioning.mediaencoder.api.messages.Reset;
import org.opennaas.extensions.contentprovisioning.mediaencoder.api.messages.Start;
import org.opennaas.extensions.contentprovisioning.mediaencoder.api.messages.Stop;

/**
 * Basic REST API interface for Elemental Live (product information <a href="https://www.elementaltechnologies.com/products/elemental-live">here</a>)
 * 
 * <p>
 * It allows starting, stopping and resetting predefined encoding jobs.
 * </p>
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/live_events")
public interface IMediaEncoder {

	/**
	 * Start encoding job
	 * 
	 * @param jobId
	 *            encoding job ID
	 * @return
	 */
	@POST
	@Path("/{jobId}/start")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String start(@PathParam("jobId") int jobId, Start start);

	/**
	 * Stop encoding job
	 * 
	 * @param jobId
	 *            encoding job ID
	 * @return
	 */
	@POST
	@Path("/{jobId}/stop")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String stop(@PathParam("jobId") int jobId, Stop stop);

	/**
	 * Reset encoding job
	 * 
	 * @param jobId
	 *            encoding job ID
	 * @return
	 */
	@POST
	@Path("/{jobId}/reset")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String reset(@PathParam("jobId") int jobId, Reset reset);

}
