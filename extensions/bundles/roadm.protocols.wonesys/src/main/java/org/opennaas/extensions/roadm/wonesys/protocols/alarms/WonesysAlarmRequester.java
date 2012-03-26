package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;

import com.wonesys.emsModule.alarms.Alarm;
import com.wonesys.emsModule.alarms.AlarmsControler;


/**
 * Allows periodically request of alarms
 * @author isart
 *
 */
public class WonesysAlarmRequester extends TimerTask {

	/**
	 * EMSModule alarms controller
	 */
	AlarmsControler acontroller = null;


	/**
	 * Date of reception of last alarm
	 */
	long lastReceptionDate = 0;

	public WonesysAlarmRequester(AlarmsControler acontroller){
		this.acontroller = acontroller;

	}

	public void run() {
		Collection<Alarm> alarms = requestAlarmsSync();
		notifyAlarmsToListeners(alarms);
	}

	private Collection<Alarm> requestAlarmsSync(){

		Collection<Alarm> alarms = acontroller.getAlarmsList();
		return alarms;
	}

	private void notifyAlarmsToListeners(Collection<Alarm> alarms){

		if (alarms == null) return;

		Iterator<Alarm> it = alarms.iterator();
		long lastReceptionDate_tmp = lastReceptionDate;
		Collection<Alarm> newAlarms = new ArrayList<Alarm>();
		//filter alarms by Date
		while (it.hasNext()){
			Alarm alarm = it.next();
			long receptionDate = alarm.getDataRecepcio();
			if (receptionDate > lastReceptionDate){
				if (receptionDate > lastReceptionDate_tmp){
					lastReceptionDate_tmp = receptionDate;
				}
				newAlarms.add(alarm);
			}
		}
		lastReceptionDate = lastReceptionDate_tmp;

		for (Alarm alarm: newAlarms){
			//notify event
			//there's no need of using same EventManager for publishing and for handling
			new WonesysEventManager().publishEvent(createAlarmEvent(alarm));
		}
	}

	private WonesysAlarmEvent createAlarmEvent(Alarm alarm){
		return new WonesysAlarmEvent(alarm);
	}

}
