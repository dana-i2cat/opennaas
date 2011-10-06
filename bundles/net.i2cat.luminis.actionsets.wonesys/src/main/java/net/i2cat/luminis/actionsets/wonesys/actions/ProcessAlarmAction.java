package net.i2cat.luminis.actionsets.wonesys.actions;

import java.util.Date;
import java.util.Properties;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.actionsets.wonesys.Activator;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import net.i2cat.nexus.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

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

		if (isWonesysAlarm((Event) params)) {

			// transform WonesysAlarm to ResourceAlarm
			ResourceAlarm resourceAlarm = wonesysAlarmToResourceAlarm((WonesysAlarm) params, protocolSessionManager.getResourceID());

			try {
				publish(resourceAlarm);
			} catch (ActivatorException e) {
				return ActionResponse.errorResponse(actionID, "Failed to publish resource alarm!");
			}
		}

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

	private ResourceAlarm wonesysAlarmToResourceAlarm(WonesysAlarm wonesysAlarm, String resourceId) {

		// obtain missing data
		CardType cardType = getAlarmCardType(wonesysAlarm);
		String alarmCode = getAlarmCode(cardType, wonesysAlarm);

		Properties properties = loadAlarmProperties(wonesysAlarm, alarmCode, resourceId);

		return new ResourceAlarm(properties);
	}

	private CardType getAlarmCardType(WonesysAlarm wonesysAlarm) {

		int chasis = (Integer) wonesysAlarm.getProperty(WonesysAlarm.CHASSIS_PROPERTY);
		int slot = (Integer) wonesysAlarm.getProperty(WonesysAlarm.SLOT_PROPERTY);

		ProteusOpticalSwitchCard card = ((ProteusOpticalSwitch) getModelToUpdate()).getCard(chasis, slot);
		if (card == null)
			return null;

		return card.getCardType();
	}

	private String getAlarmCode(CardType cardType, WonesysAlarm wonesysAlarm) {

		if (cardType.equals(CardType.ROADM_ADD) || cardType.equals(CardType.ROADM_DROP))
			if (wonesysAlarm.getProperty(WonesysAlarm.ALARM_ID_PROPERTY) != null &&
					wonesysAlarm.getProperty(WonesysAlarm.ALARM_ID_PROPERTY).equals("80"))
				return "CPLANCHANGED";

		return "UNKNOW";
	}

	private Properties loadAlarmProperties(WonesysAlarm wonesysAlarm, String alarmCode, String resourceId) {
		Properties properties = new Properties();
		properties.put(ResourceAlarm.RESOURCE_ID_PROPERTY, resourceId);
		properties.put(ResourceAlarm.ALARM_CODE_PROPERTY, alarmCode);
		properties.put(ResourceAlarm.DESCRIPTION_PROPERTY, getDescription(alarmCode));
		properties.put(ResourceAlarm.CAUSE_PROPERY, wonesysAlarm);
		properties.put(ResourceAlarm.ARRIVAL_TIME_PROPERTY, new Date().getTime());

		return properties;
	}

	private String getDescription(String alarmCode) {
		if (alarmCode.equals("CPLANCHANGED")) {
			return "A channel has changed. Refresh connections in order to see applied changes.";
		}
		return "Unknown alarm received.";
	}

	private void publish(Event event) throws ActivatorException {
		IEventManager eventManager = Activator.getEventManagerService();
		eventManager.publishEvent(event);
	}

}
