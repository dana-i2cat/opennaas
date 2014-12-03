package org.opennaas.extensions.abno.client;

/*
 * #%L
 * OpenNaaS :: XIFI ABNO
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.abno.client.model.ABNOResponse;

/**
 * ABNO Client interface
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface IABNOClient {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public ABNOResponse createUnicastLink(@QueryParam("source_region") String srcRegion, @QueryParam("dest_region") String dstRegion,
			@QueryParam("source_mac") String srcMACAddress, @QueryParam("dest_mac") String dstMACAddress,
			@QueryParam("source_interface") String srcInterface, @QueryParam("destination_interface") String dstInterface,
			@QueryParam("operation") String operation, @QueryParam("Operation_Type") String operationType,
			@QueryParam("ID_Operation") long operationId);

}
