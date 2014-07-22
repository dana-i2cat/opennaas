package org.opennaas.extensions.genericnetwork.capability.circuitaggregation;

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
import org.opennaas.extensions.genericnetwork.model.circuit.Circuit;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CircuitAggregationCapability extends AbstractCapability implements ICircuitAggregationCapability {

	public static final String	CAPABILITY_TYPE				= "circuitaggregation";

	private Log					log							= LogFactory.getLog(CircuitAggregationCapability.class);

	private String				resourceId					= "";

	public static final String	USE_AGGREGATION_PROPERTY	= "enabled";
	private final boolean		useAggregation;

	public CircuitAggregationCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		if (this.getCapabilityDescriptor().getProperty(USE_AGGREGATION_PROPERTY) == null) {
			this.useAggregation = false;
		} else {
			this.useAggregation = Boolean.parseBoolean(this.getCapabilityDescriptor().getProperty(USE_AGGREGATION_PROPERTY).getValue());
		}
		log.debug("Built new Circuit Aggregation Capability");
	}

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
			return Activator.getActionSetService(CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	// ///////////////////////////////////////////////
	// ICircuitAggregationCapability implementation //
	// ///////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	@Override
	public Set<Circuit> aggregateCircuits(Set<Circuit> notAggregated) throws CapabilityException {
		// is Circuit Aggregation enabled?
		if (useAggregation) {
			IAction action = createActionAndCheckParams(CircuitAggregationActionSet.CALCULATE_AGGREGATED_CIRCUITS, notAggregated);
			ActionResponse response = executeAction(action);

			if (!response.getStatus().equals(ActionResponse.STATUS.OK))
				throw new ActionException(response.toString());

			if (!(response.getResult() instanceof Set<?>))
				throw new ActionException("Invalid return type in action " + CircuitAggregationActionSet.CALCULATE_AGGREGATED_CIRCUITS);

			for (Object obj : (Set<?>) response.getResult()) {
				if (!(obj instanceof Circuit)) {
					throw new ActionException("Invalid return type in action " + CircuitAggregationActionSet.CALCULATE_AGGREGATED_CIRCUITS);
				}
			}

			return (Set<Circuit>) response.getResult();
		}

		// return exactly same circuits if Circuit Aggregation is not enabled
		return notAggregated;
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
