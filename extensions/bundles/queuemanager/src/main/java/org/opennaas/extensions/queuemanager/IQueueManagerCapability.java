package org.opennaas.extensions.queuemanager;

/*
 * #%L
 * OpenNaaS :: Queue Manager
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueResponse;

@Path("/")
public interface IQueueManagerCapability extends ICapability {

	/**
	 * @return
	 * @throws ProtocolException
	 * @throws CapabilityException
	 * @throws ActionException
	 */
	@POST
	@Path("/execute")
	@Produces(MediaType.APPLICATION_XML)
	public QueueResponse execute() throws ProtocolException,
			CapabilityException, ActionException;

	/**
	 * 
	 */
	@POST
	@Path("/clear")
	public void clear();

	/**
	 * @return
	 */
	@GET
	@Path("/getActionsId")
	@Produces(MediaType.APPLICATION_XML)
	public Response getActionsId();

	/**
	 * @param modifyParams
	 * @throws CapabilityException
	 */
	@POST
	@Path("/modify")
	@Consumes(MediaType.APPLICATION_XML)
	public void modify(ModifyParams modifyParams) throws CapabilityException;

	// TODO move to an other interface:
	// the fact that this method is not exported through ws suggest it should be placed in an other interface

	/**
	 * @param action
	 * @throws CapabilityException
	 */
	public void queueAction(IAction action) throws CapabilityException;

	/**
	 * @return
	 */
	public List<IAction> getActions();

}
