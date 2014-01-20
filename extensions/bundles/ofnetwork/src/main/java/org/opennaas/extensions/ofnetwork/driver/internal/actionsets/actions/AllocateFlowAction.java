package org.opennaas.extensions.ofnetwork.driver.internal.actionsets.actions;

/*
 * #%L
 * OpenNaaS :: OF Network
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

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.ofnetwork.Activator;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;
import org.opennaas.extensions.ofnetwork.model.OFNetworkModel;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.IOpenflowForwardingCapability;
import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class AllocateFlowAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		NetOFFlow netFlow = (NetOFFlow) params;

		// transform NetOFFlow to FloodlightOFFlow
		FloodlightOFFlow flow = new FloodlightOFFlow(netFlow, null);

		try {
			String resourceName = netFlow.getResourceId();
			IResource resource = getResourceByName(resourceName);
			IOpenflowForwardingCapability forwardingCapability = (IOpenflowForwardingCapability) resource
					.getCapabilityByInterface(IOpenflowForwardingCapability.class);

			forwardingCapability.createOpenflowForwardingRule(flow);

			// update model
			if (((OFNetworkModel) getModelToUpdate()).getNetFlowsPerResource().containsKey(resourceName)) {
				((OFNetworkModel) getModelToUpdate()).getNetFlowsPerResource().get(resourceName).add(netFlow);
			} else {
				List<NetOFFlow> netFlows = new ArrayList<NetOFFlow>();
				netFlows.add(netFlow);
				((OFNetworkModel) getModelToUpdate()).getNetFlowsPerResource().put(resourceName, netFlows);
			}

		} catch (Exception e) {
			throw new ActionException("Error allocating flow : ", e);
		}

		return ActionResponse.okResponse(getActionID());
	}

	private IResource getResourceByName(String resourceName) throws ResourceException {
		try {
			IResourceManager resourceManager = Activator.getResourceManagerService();
			return resourceManager.getResource(resourceManager.getIdentifierFromResourceName("openflowswitch", resourceName));
		} catch (ActivatorException e) {
			throw new ResourceException("Couldn't get resource. Failed to get ResourceManagerService", e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof NetOFFlow)) {
			throw new ActionException("Action parameters must be set with a NetOFFlow");
		}

		return true;
	}
}
