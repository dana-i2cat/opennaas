package org.opennaas.extensions.genericnetwork.capability.ofprovision;

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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.model.NetOFFlow;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class OFProvisioningNetworkCapability extends AbstractCapability implements IOFProvisioningNetworkCapability {

	public static final String	CAPABILITY_TYPE	= "ofprovisionnet";

	Log							log				= LogFactory.getLog(OFProvisioningNetworkCapabilityFactory.class);

	private String				resourceId		= "";

	public OFProvisioningNetworkCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Openflow Provisioning Network Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				IOFProvisioningNetworkCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getActionSetService(OFProvisioningNetworkCapability.CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void allocateFlows(List<NetOFFlow> flows) throws CapabilityException {
		// Allocate in reverse order.
		// Reverse order is desired to ensure "circuits" are completely established when traffic starts to pass within them.
		// This is so to minimize the amount of packet-in messages to treat.
		for (int i = flows.size() - 1; i >= 0; i--) {
			allocateFlow(flows.get(i));
		}
	}

	@Override
	public void deallocateFlows(List<NetOFFlow> flows) throws CapabilityException {
		// Deallocate in given order.
		// Given order is desired to ensure "circuits" do not accept traffic when deallocation is taking place.
		// This is so to minimize the amount of packet-in messages to treat.
		for (NetOFFlow flow : flows) {
			deallocateFlow(flow);
		}
	}

	@Override
	public Set<NetOFFlow> getAllocatedFlows() throws CapabilityException {

		IAction action = createActionAndCheckParams(OFProvisioningNetworkActionSet.GETALLOCATEDFLOWS, null);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		Set<NetOFFlow> result;
		if (response.getResult() != null && response.getResult() instanceof Collection<?>) {
			// assuming the action returns what it is meant to
			result = (Set<NetOFFlow>) response.getResult();
		} else {
			throw new CapabilityException("Failed to retrieve result from action response of action " + action.getActionID());
		}

		return result;
	}

	@Override
	public void replaceFlows(List<NetOFFlow> current, List<NetOFFlow> desired) throws CapabilityException {
		List<NetOFFlow> toAllocate = new ArrayList<NetOFFlow>(desired.size());
		List<NetOFFlow> toDeallocate = new ArrayList<NetOFFlow>(current.size());
		List<NetOFFlow> common = new ArrayList<NetOFFlow>(current.size());

		for (NetOFFlow flow : desired) {
			if (!flowIsInList(flow, current)) {
				toAllocate.add(flow);
			} else {
				common.add(flow);
			}
		}

		// toAllocate contains all flows in desired that are not in current
		// common contains all flows that are both in current and in desired

		for (NetOFFlow flow : current) {
			if (!flowIsInList(flow, common)) {
				toDeallocate.add(flow);
			}
		}

		// toAllocate contains all flows in desired that are not in current
		// toDeallocate contains all flows in current that are not in desired
		// common contains all flows that are both in current and in desired

		allocateFlows(toAllocate);
		deallocateFlows(toDeallocate);
	}

	private boolean flowIsInList(NetOFFlow flow, List<NetOFFlow> flows) {
		for (NetOFFlow tmp : flows) {
			if (flow.equalsIgnoringName(tmp)) {
				return true;
			}
		}
		return false;
	}

	private String allocateFlow(NetOFFlow netOFFlow) throws CapabilityException {

		NetOFFlow flowCopy = new NetOFFlow(netOFFlow);
		IAction action = createActionAndCheckParams(OFProvisioningNetworkActionSet.ALLOCATEFLOW, netOFFlow);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		return flowCopy.getName();
	}

	private String deallocateFlow(NetOFFlow netOFFlow) throws CapabilityException {
		NetOFFlow flowCopy = new NetOFFlow(netOFFlow);
		IAction action = createActionAndCheckParams(OFProvisioningNetworkActionSet.DEALLOCATEFLOW, netOFFlow);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		return flowCopy.getName();
	}

	private ActionResponse executeAction(IAction action) throws CapabilityException {

		try {
			IProtocolManager protocolManager = getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			ActionResponse response = action.execute(protocolSessionManager);
			return response;

		} catch (ProtocolException pe) {
			log.error("Error getting protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		}
	}

	private IProtocolManager getProtocolManagerService() throws ActivatorException {
		return Activator.getProtocolManagerService();
	}

}
