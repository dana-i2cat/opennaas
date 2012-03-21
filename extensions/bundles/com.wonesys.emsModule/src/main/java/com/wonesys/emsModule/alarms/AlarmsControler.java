/*
 * AlarmsControler.java
 *
 * Created on 8 de abril de 2008, 15:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.alarms;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mbeltran
 */
public class AlarmsControler {

	private HashMap<String, Alarm>	alarmMap	= new HashMap<String, Alarm>();
	private SnmpTrapListener		snmpTrapListener;

	public static boolean			debug		= true;

	/** Creates a new instance of AlarmsControler */
	public AlarmsControler() {
	}

	public void createAlarmListener(int port) throws IOException {

		snmpTrapListener = new SnmpTrapListener(port, this);
		snmpTrapListener.rcv();

	}

	public Collection<Alarm> getAlarmsList() {

		return alarmMap.values();

	}

	void registerAlarm(String oid, String ip, String params, String oids) {

		if (AlarmsControler.debug)
			System.out.println("AlarmsControler: Register Alarm with params: " + params);

		String paramsSplit[] = params.split("#");

		List<String> listTipos = AlarmsTipoControler.getTipos(params, oids, oid);

		for (String elem : listTipos) {

			Alarm alarm = new Alarm(ip, oid, params);
			new AlarmsTipoControler().setTipo(alarm, elem);

			String key = ip;

			if (elem.equals("SNMP"))
				key += "#" + paramsSplit[5];
			else
				key += "#" + elem;

			if (paramsSplit.length > 3)
				key += "#" + paramsSplit[2] + "#" + paramsSplit[3];
			else if (paramsSplit.length > 2)
				key += "#" + paramsSplit[2];

			// Add port to the alarm key
			key += "#" + AlarmsTipoControler.getPuerto(params);

			if (alarmMap.containsKey(key)) {
				alarmMap.get(key).incRepet();
				Calendar timerebut = Calendar.getInstance();
				alarmMap.get(key).setDataUltima(timerebut.getTimeInMillis());

				if (AlarmsControler.debug)
					System.out.println("AlarmsControler: Alarm exists, updating alarm " + key);

			} else {

				alarm.setPort(AlarmsTipoControler.getPuerto(params));
				alarmMap.put(key, alarm);

				if (AlarmsControler.debug)
					System.out.println("AlarmsControler: Alarm does not exists, creating alarm " + key);

			}

			// System.out.println("AlarmsControler: alarmsMap size = " + alarmMap.size());
			// System.out.println("AlarmsControler: alarms= " + alarmMap.values());
			// for (Alarm a : alarmMap.values()) {
			// System.out.println("Alarm: " + a.toString());
			// }

		}

	}

	public void flush() {
		alarmMap.clear();
	}

}
