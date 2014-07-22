package org.opennaas.extensions.genericnetwork.actionsets.internal.ofprovision.actions;

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.capability.ofprovision.OFProvisioningNetworkActionSet;
import org.opennaas.extensions.genericnetwork.model.NetOFFlow;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
public class GetAllocatedFlowsAction extends Action {

	@Override
	public boolean checkParams(Object params) throws ActionException {
		// no params needed
		return true;
	}

	// FIXME: capability should read this information from underlying devices each time it is asked for it.
	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {

		Set<NetOFFlow> flows = new HashSet<NetOFFlow>();
		Iterator<List<NetOFFlow>> it = ((GenericNetworkModel) getModelToUpdate()).getNetFlowsPerResource().values().iterator();
		while (it.hasNext()) {
			flows.addAll(it.next());
		}

		ActionResponse response = ActionResponse.okResponse(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);
		response.setResult(flows);
		response.setStatus(STATUS.OK);
		return response;
	}

	// TODO: CORRECT WAY:
	// Retrieves live information from underlying devices
	// Requires information about which resources are part of the network

	// @Override
	// public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
	// throws ActionException {
	//
	// List<IResource> resources = getResourcesInNetwork();
	//
	// Map<String, List<NetOFFlow>> netFlowsPerResource = new HashMap<String, List<NetOFFlow>>();
	//
	// for (IResource resource : resources) {
	// List<NetOFFlow> netFlows = retrieveFlowsPerResource(resource);
	// netFlowsPerResource.put(resource.getResourceDescriptor().getInformation().getName(), netFlows);
	// }
	//
	// ActionResponse response = ActionResponse.okResponse(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS);
	// response.setResult(netFlowsPerResource);
	// response.setStatus(STATUS.OK);
	// return response;
	// }
	//
	// public List<NetOFFlow> retrieveFlowsPerResource(IResource resource) throws CapabilityException {
	// IOpenflowForwardingCapability forwardingCapability;
	// try {
	// forwardingCapability = (IOpenflowForwardingCapability) resource
	// .getCapabilityByInterface(IOpenflowForwardingCapability.class);
	// } catch (ResourceException e) {
	// log.debug("Ignoring resource not having IOpenflowForwardingCapability capability. ResourceId: " +
	// resource.getResourceIdentifier().getId());
	// return new ArrayList<NetOFFlow>(0);
	// }
	//
	// List<FloodlightOFFlow> flows = forwardingCapability.getOpenflowForwardingRules();
	// List<NetOFFlow> netFlows = new ArrayList<NetOFFlow>(flows.size());
	//
	// for (FloodlightOFFlow obtainedFlow : flows) {
	// netFlows.add(new NetOFFlow(obtainedFlow, resource.getResourceDescriptor().getInformation().getName()));
	// }
	//
	// return netFlows;
	// }
	//
	// private List<IResource> getResourcesInNetwork() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
