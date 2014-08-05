package org.opennaas.extensions.openflowswitch.capability.offorwarding;

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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlowListWrapper;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
@Path("/")
public interface IOpenflowForwardingCapability extends ICapability {

	@POST
	@Path("/createOFFForwardingRule")
	@Consumes(MediaType.APPLICATION_XML)
	public void createOpenflowForwardingRule(FloodlightOFFlow forwardingRule) throws CapabilityException;

	@DELETE
	@Path("/removeOFForwardingRule/{flowId}")
	@Consumes(MediaType.APPLICATION_XML)
	public void removeOpenflowForwardingRule(@PathParam("flowId") String flowId) throws CapabilityException;

	public List<FloodlightOFFlow> getOpenflowForwardingRules() throws CapabilityException;

	@GET
	@Path("/getOFForwardingRules")
	@Produces(MediaType.APPLICATION_XML)
	public FloodlightOFFlowListWrapper getOpenflowForwardingRulesAPI() throws CapabilityException;

}
