package org.opennaas.extensions.contentprovisioning;

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

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@Path("/")
public interface IContentProvisioning {

	@POST
	@Path("/{streamId}")
	public void startStream(@PathParam("streamId") int streamId) throws Exception;

	@DELETE
	@Path("/{streamId}")
	public void stopStream(@PathParam("streamId") int streamId) throws Exception;

}
