package org.opennaas.extensions.roadm.wonesys.actionsets.actions.alarms;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Actionset
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
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.roadm.wonesys.actionsets.Activator;
import org.opennaas.extensions.roadm.wonesys.actionsets.WonesysAlarmsDriver;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
import org.osgi.service.event.Event;

public class ProcessAlarmAction extends Action {

	static Log	log	= LogFactory.getLog(ProcessAlarmAction.class);

	public ProcessAlarmAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.PROCESSALARM);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		if (!(params instanceof CapabilityAlarm)) {
			return ActionResponse.errorResponse(actionID, "Invalid parameters! ");
		}

		String resourceId = (String) ((CapabilityAlarm) params).getProperty(CapabilityAlarm.RESOURCE_ID_PROPERTY);
		if (resourceId == null) {
			return ActionResponse.errorResponse(actionID,
					"Failed to process Capability Alarm! Missing " + CapabilityAlarm.RESOURCE_ID_PROPERTY + " property");
		}

		Event cause = (Event) ((CapabilityAlarm) params).getProperty(CapabilityAlarm.CAUSE_PROPERTY);
		if (cause == null) {
			return ActionResponse.errorResponse(actionID,
					"Failed to process Capability Alarm! Missing " + CapabilityAlarm.CAUSE_PROPERTY + " property");
		}

		if (!isWonesysAlarm(cause)) {
			return ActionResponse.errorResponse(actionID,
					"Failed to process Capability Alarm! " + CapabilityAlarm.CAUSE_PROPERTY + " is unknown");
		}

		// transform WonesysAlarm to ResourceAlarm
		processWonesysAlarm((WonesysAlarm) cause, resourceId);

		return ActionResponse.okResponse(actionID);
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof Event)) {
			return false;
		}
		return true;
	}

	private boolean isWonesysAlarm(Event event) {
		return (event instanceof WonesysAlarm);
	}

	private void processWonesysAlarm(WonesysAlarm alarm, String resourceId) throws ActionException {

		log.debug("Processing Wonesys alarm...");
		ResourceAlarm resourceAlarm = WonesysAlarmsDriver.wonesysAlarmToResourceAlarm(alarm, getModelToUpdate(), resourceId);

		try {
			publish(resourceAlarm);
		} catch (ActivatorException e) {
			throw new ActionException("Failed to publish resource alarm!", e);
		}
	}

	private void publish(Event event) throws ActivatorException {
		IEventManager eventManager = Activator.getEventManagerService();
		eventManager.publishEvent(event);
	}

}
