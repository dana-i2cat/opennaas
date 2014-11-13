package org.opennaas.extensions.openflowswitch.capability.controllerinformation;

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
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.HealthState;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.MemoryUsage;
import org.opennaas.extensions.openflowswitch.repository.Activator;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ControllerInformationCapability extends AbstractCapability implements IControllerInformationCapability {

	public static String	CAPABILITY_TYPE	= "controllerinformation";

	private Log				log				= LogFactory.getLog(ControllerInformationCapability.class);

	private String			resourceId		= "";

	public ControllerInformationCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Controller Information Capability");
	}

	@Override
	public String getCapabilityName() {
		return this.CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getControllerInformationActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);

		}
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				IControllerInformationCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	// ###############################################
	// ### IControllerInformationCapability methods ###
	// ###############################################

	@Override
	public HealthState getHealthState() throws CapabilityException {
		log.info("Getting Controller's Health State.");

		IAction action = createActionAndCheckParams(ControllerInformationActionSet.GET_HEALTH_STATE, null);
		ActionResponse response = executeAction(action);

		Object result = response.getResult();
		if (!(result instanceof HealthState)) {
			throw new CapabilityException("Unexpected action response object:" + result.getClass().getName());
		}

		return (HealthState) result;

	}

	@Override
	public MemoryUsage getControllerMemoryUsage() throws CapabilityException {

		log.info("Getting Controller's Memory Usage.");

		IAction action = createActionAndCheckParams(ControllerInformationActionSet.GET_MEMORY_USAGE, null);
		ActionResponse response = executeAction(action);

		Object result = response.getResult();
		if (!(result instanceof MemoryUsage)) {
			throw new CapabilityException("Unexpected action response object:" + result.getClass().getName());
		}

		return (MemoryUsage) result;
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
		} catch (ActivatorException ae) {
			String errorMsg = "Error getting protocol manager - " + ae.getMessage();
			log.error(errorMsg);
			throw new CapabilityException(errorMsg, ae);
		} catch (ActionException ae) {
			log.error("Error executing " + action.getActionID() + " action - " + ae.getMessage());
			throw (ae);
		}

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			String errMsg = "Error executing " + action.getActionID() + " action - " + response.getInformation();
			log.error(errMsg);
			throw new ActionException(errMsg);
		}
		return response;
	}

}
