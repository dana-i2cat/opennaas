package org.opennaas.extensions.genericnetwork.capability.statistics;

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
import org.opennaas.extensions.genericnetwork.model.NetworkStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class NetworkStatisticsCapability extends AbstractCapability implements INetworkStatisticsCapability {

	public static final String	CAPABILITY_TYPE	= "gnetstatistics";

	Log							log				= LogFactory.getLog(NetworkStatisticsCapability.class);

	private String				resourceId		= "";

	public NetworkStatisticsCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Network Statistics Capability");
	}

	// //////////////////////////////////// //
	// INetworkStatisticsCapability Methods //
	// //////////////////////////////////// //

	@Override
	public NetworkStatistics getNetworkStatistics() throws CapabilityException {
		log.info("Start of getNetworkStatistics call");

		IAction action = createActionAndCheckParams(NetworkStatisticsActionSet.GET_NETWORK_STATISTICS, null);
		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		if (!(response.getResult() instanceof NetworkStatistics))
			throw new ActionException("Failed to retrieve result from action response of action " + action.getActionID());

		NetworkStatistics netStatistics = (NetworkStatistics) response.getResult();

		log.info("End of getNetworkStatistics call");

		return netStatistics;
	}

	// ////////////////////////// //
	// AbstractCapability Methods //
	// ////////////////////////// //

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
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
			return Activator.getActionSetService(NetworkStatisticsCapability.CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				INetworkStatisticsCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
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
