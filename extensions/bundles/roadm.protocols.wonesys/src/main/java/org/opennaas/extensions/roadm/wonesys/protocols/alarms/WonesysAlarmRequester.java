package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Protocol
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;

import com.wonesys.emsModule.alarms.Alarm;
import com.wonesys.emsModule.alarms.AlarmsControler;

/**
 * Allows periodically request of alarms
 * 
 * @author isart
 * 
 */
public class WonesysAlarmRequester extends TimerTask {

	/**
	 * EMSModule alarms controller
	 */
	AlarmsControler	acontroller			= null;

	/**
	 * Date of reception of last alarm
	 */
	long			lastReceptionDate	= 0;

	public WonesysAlarmRequester(AlarmsControler acontroller) {
		this.acontroller = acontroller;

	}

	public void run() {
		Collection<Alarm> alarms = requestAlarmsSync();
		notifyAlarmsToListeners(alarms);
	}

	private Collection<Alarm> requestAlarmsSync() {

		Collection<Alarm> alarms = acontroller.getAlarmsList();
		return alarms;
	}

	private void notifyAlarmsToListeners(Collection<Alarm> alarms) {

		if (alarms == null)
			return;

		Iterator<Alarm> it = alarms.iterator();
		long lastReceptionDate_tmp = lastReceptionDate;
		Collection<Alarm> newAlarms = new ArrayList<Alarm>();
		// filter alarms by Date
		while (it.hasNext()) {
			Alarm alarm = it.next();
			long receptionDate = alarm.getDataRecepcio();
			if (receptionDate > lastReceptionDate) {
				if (receptionDate > lastReceptionDate_tmp) {
					lastReceptionDate_tmp = receptionDate;
				}
				newAlarms.add(alarm);
			}
		}
		lastReceptionDate = lastReceptionDate_tmp;

		for (Alarm alarm : newAlarms) {
			// notify event
			// there's no need of using same EventManager for publishing and for handling
			new WonesysEventManager().publishEvent(createAlarmEvent(alarm));
		}
	}

	private WonesysAlarmEvent createAlarmEvent(Alarm alarm) {
		return new WonesysAlarmEvent(alarm);
	}

}
