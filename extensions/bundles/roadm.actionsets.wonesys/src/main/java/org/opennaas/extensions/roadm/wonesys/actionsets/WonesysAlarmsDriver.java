package org.opennaas.extensions.roadm.wonesys.actionsets;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;

public class WonesysAlarmsDriver {

	public static ResourceAlarm wonesysAlarmToResourceAlarm(WonesysAlarm wonesysAlarm, IModel model, String resourceId) {

		// obtain missing data
		CardType cardType = getAlarmCardType(wonesysAlarm, (ProteusOpticalSwitch) model);
		String alarmCode = getAlarmCode(cardType, wonesysAlarm);

		Map<String, Object> properties = loadResourceAlarmProperties(wonesysAlarm, alarmCode, resourceId);

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

	private static Map<String, Object> loadResourceAlarmProperties(WonesysAlarm wonesysAlarm, String alarmCode, String resourceId) {
		Map<String, Object> properties = new HashMap<String, Object>();

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
