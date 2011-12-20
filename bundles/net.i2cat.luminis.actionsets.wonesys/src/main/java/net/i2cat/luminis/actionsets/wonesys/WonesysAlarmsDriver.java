package net.i2cat.luminis.actionsets.wonesys;

import java.util.Date;
import java.util.Properties;

import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.alarms.ResourceAlarm;

public class WonesysAlarmsDriver {

	public static ResourceAlarm wonesysAlarmToResourceAlarm(WonesysAlarm wonesysAlarm, IModel model, String resourceId) {

		// obtain missing data
		CardType cardType = getAlarmCardType(wonesysAlarm, (ProteusOpticalSwitch) model);
		String alarmCode = getAlarmCode(cardType, wonesysAlarm);

		Properties properties = loadResourceAlarmProperties(wonesysAlarm, alarmCode, resourceId);

		return new ResourceAlarm(properties);
	}

	private static CardType getAlarmCardType(WonesysAlarm wonesysAlarm, ProteusOpticalSwitch model) {

		int chasis = (Integer) wonesysAlarm.getProperty(WonesysAlarm.CHASSIS_PROPERTY);
		int slot = (Integer) wonesysAlarm.getProperty(WonesysAlarm.SLOT_PROPERTY);

		ProteusOpticalSwitchCard card = model.getCard(chasis, slot);
		if (card == null)
			return null;

		return card.getCardType();
	}

	private static String getAlarmCode(CardType cardType, WonesysAlarm wonesysAlarm) {
		if (cardType == null)
			return "UNKNOWN";

		if (cardType.equals(CardType.ROADM_ADD) || cardType.equals(CardType.ROADM_DROP))
			if (wonesysAlarm.getProperty(WonesysAlarm.ALARM_ID_PROPERTY) != null &&
					wonesysAlarm.getProperty(WonesysAlarm.ALARM_ID_PROPERTY).equals("80"))
				return "CPLANCHANGED";

		return "UNKNOWN";
	}

	private static Properties loadResourceAlarmProperties(WonesysAlarm wonesysAlarm, String alarmCode, String resourceId) {
		Properties properties = new Properties();
		properties.put(ResourceAlarm.RESOURCE_ID_PROPERTY, resourceId);
		properties.put(ResourceAlarm.ALARM_CODE_PROPERTY, alarmCode);
		properties.put(ResourceAlarm.DESCRIPTION_PROPERTY, WonesysAlarmsDriver.getWonesysAlarmDescriptionByCode(alarmCode));
		properties.put(ResourceAlarm.CAUSE_PROPERY, wonesysAlarm);
		properties.put(ResourceAlarm.ARRIVAL_TIME_PROPERTY, new Date().getTime());

		return properties;
	}

	private static String getWonesysAlarmDescriptionByCode(String alarmCode) {
		if (alarmCode.equals("CPLANCHANGED")) {
			return "A channel has changed. Refresh connections in order to see applied changes.";
		}
		return "Unknown alarm received.";
	}

}
