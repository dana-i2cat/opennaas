package net.i2cat.luminis.actionsets.wonesys.actions.alarms;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.actionsets.wonesys.Activator;
import net.i2cat.luminis.actionsets.wonesys.WonesysAlarmsDriver;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.nexus.events.IEventManager;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.alarms.CapabilityAlarm;
import net.i2cat.nexus.resources.alarms.ResourceAlarm;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
