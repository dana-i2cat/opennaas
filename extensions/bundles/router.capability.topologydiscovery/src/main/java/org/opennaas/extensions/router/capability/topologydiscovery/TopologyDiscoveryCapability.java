package org.opennaas.extensions.router.capability.topologydiscovery;

/*
 * #%L
 * OpenNaaS :: Router :: Topology Discovery capability
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
import org.opennaas.extensions.router.capability.topologydiscovery.model.LocalInformation;
import org.opennaas.extensions.router.capability.topologydiscovery.model.Neighbours;
import org.opennaas.extensions.router.capability.topologydiscovery.model.Port;

public class TopologyDiscoveryCapability extends AbstractCapability implements ITopologyDiscoveryCapability {

	public static String	CAPABILITY_TYPE	= "topologydiscovery";

	Log						log				= LogFactory.getLog(TopologyDiscoveryCapability.class);

	private String			resourceId		= "";

	public TopologyDiscoveryCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new ToplogyDiscovery Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), ITopologyDiscoveryCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		registration.unregister();
		super.deactivate();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getTopologyDiscoveryActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		// throw new UnsupportedOperationException();

	}

	// ################################
	// ## ITopologyDiscovery Methods ##
	// ################################

	@Override
	public Neighbours getNeighbours() throws CapabilityException {

		log.info("Getting " + resourceId + " neighbours");

		IAction action = createActionAndCheckParams(TopologyDiscoveryActionSet.TOPOLOGY_DISCOVERY_GET_NEIGHBOURS, null);

		ActionResponse response = executeAction(action);

		Neighbours neighbours = (Neighbours) response.getResult();
		for (String localIfaceName : neighbours.getDevicePortMap().keySet()) {

			action = createActionAndCheckParams(TopologyDiscoveryActionSet.TOPOLOGY_DISCOVERY_GET_INTERFACE_NEIGHBOUR, localIfaceName);
			response = executeAction(action);

			Port remotePort = neighbours.getDevicePortMap().get(localIfaceName);
			remotePort.setPortId((String) response.getResult());

		}

		return neighbours;
	}

	@Override
	public LocalInformation getLocalInformation() throws CapabilityException {

		log.info("Getting " + resourceId + " local information");

		IAction action = createActionAndCheckParams(TopologyDiscoveryActionSet.TOPOLOGY_DISCOVERY_GET_LOCAL_INFORMATION, null);

		ActionResponse response = executeAction(action);

		return (LocalInformation) response.getResult();

	}

	private ActionResponse executeAction(IAction action) throws CapabilityException {
		ActionResponse response;
		try {
			IProtocolManager protocolManager = Activator.getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			response = action.execute(protocolSessionManager);

		} catch (ProtocolException pe) {
			log.error("Error with protocol session - " + pe.getMessage());
			throw new CapabilityException(pe);
		} catch (ActionException ae) {
			log.error("Error executing " + action.getActionID() + " action - " + ae.getMessage());
			throw (ae);
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		}

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			String errMsg = "Error executing " + action.getActionID() + " action - " + response.getInformation();
			log.error(errMsg);
			throw new ActionException(errMsg);
		}
		return response;
	}

}
