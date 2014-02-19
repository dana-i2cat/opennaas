package org.opennaas.extensions.genericnetwork.capability.pathfinding;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.util.HashMap;
import java.util.Map;

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
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;

public class PathFindingCapability extends AbstractCapability implements IPathFindingCapability {

	public static final String	CAPABILITY_TYPE	= "pathfinding";

	private Log					log				= LogFactory.getLog(PathFindingCapability.class);
	private String				resourceId		= "";

	public PathFindingCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new PathFinder Capability");

	}

	/**
	 * FIXME This method should not get the url from descriptor and create the action which such param. This implies a mixing between the capability
	 * and the action implementation, which is the one requiring to read routes from file. Since the action has no access to the resource descriptor,
	 * we're implementing such a workaround.
	 * 
	 */
	@Override
	public Route findPathForRequest(CircuitRequest circuitRequest) throws CapabilityException {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PathFindingParamsMapping.REQUEST_KEY, circuitRequest);
		params.put(PathFindingParamsMapping.ROUTES_FILE_KEY, this.getCapabilityDescriptor().getProperty(PathFindingParamsMapping.ROUTES_FILE_KEY)
				.getValue());
		params.put(PathFindingParamsMapping.ROUTES_MAPPING_KEY,
				this.getCapabilityDescriptor().getProperty(PathFindingParamsMapping.ROUTES_MAPPING_KEY).getValue());

		IAction action = createActionAndCheckParams(PathFindingActionSet.FIND_PATH_FOR_REQUEST, params);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK))
			throw new ActionException(response.toString());

		return (Route) response.getResult();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {

		log.debug("Getting Actionset for capability " + CAPABILITY_TYPE);

		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getActionSetService(CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction arg0) throws CapabilityException {
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");

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
