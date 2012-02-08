/*
 * AlarmsTipoControler.java
 *
 * Created on 9 de abril de 2008, 14:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.alarms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.wonesys.emsModule.driver.Driver;
import com.wonesys.emsModule.driver.DriverAlarmasGeneral;
import com.wonesys.emsModule.driver.Optr10g;
import com.wonesys.emsModule.driver.Optr25gexc;
import com.wonesys.emsModule.driver.Optr25gexc_PM;
import com.wonesys.emsModule.driver.OptriaSX;
import com.wonesys.emsModule.driver.Sinton;
import com.wonesys.emsModule.driver.Wcmctc;
import com.wonesys.emsModule.driver.Wswitch;

/**
 *
 * @author mbeltran
 */

/*
 * Exemple d'alarma (temperatura)
 *
 * str[0] = 307100 str[1] = 1.3.6.1.4.1.18223.9.4.2.2 str[2] = 0 str[3] = 4 str[4] = 0x0A:FF:FF str[5] = TEMP str[6] = High temperature str[7] = 1
 */

public class AlarmsTipoControler {

	static List<String> getTipos(String params, String oids, String genericTrap) {

		ArrayList<String> list = new ArrayList<String>();

		String[] paramsSplit = params.split("#");

		String oidAlarm = paramsSplit[1];

		Driver driver = null;

		if (oidAlarm.startsWith("1.3.6.1.6.3")) {

			return new DriverAlarmasGeneral().getListTipos(params);

		} else if (params.contains(Wcmctc.ID)) // W-CMC-TC
		{

			return new Wcmctc().getListTipos(params, oids);
		} else if (oids.contains("1.3.6.1.4.1.8691") || oids.contains("1.3.6.1.2.1.2.2.1.1")) {
			return new Wswitch().getListTipos(params, oids, genericTrap);
		} else if (oidAlarm.equals(Sinton.Alarma)) // Sinton
		{

			return new Sinton().getListTipos(params);
		} else if (oidAlarm.equals("1.3.6.1.4.1.18223.9.1.2.2") || // Equip OptriaSX
		oidAlarm.equals("1.3.6.1.4.1.18223.9.4.2.2") || // Tarja 4x4
		oidAlarm.equals("1.3.6.1.4.1.18223.9.5.2.3") || // Tarja 10G
		oidAlarm.equals("1.3.6.1.4.1.18223.9.8.2.2") || // Tarja PM2G5
		oidAlarm.equals("1.3.6.1.4.1.18223.9.11.2.3") || // Roadm
		oidAlarm.equals("1.3.6.1.4.1.18223.9.14.2.2") || // Edfa
		oidAlarm.equals("1.3.6.1.4.1.18223.9.17.2.3")) // opm
		{
			list.add("SNMP");
			return list;
		}

		/*
		 * else if (oidAlarm.equals(Optr25gexc.Alarma)) //Tarja 4x4 {
		 *
		 * return new Optr25gexc().getListTipos(params);
		 *
		 * } else if (oidAlarm.equals(Optr10g.Alarma)) //Tarja 10G {
		 *
		 * return new Optr10g().getListTipos(params); } else if (oidAlarm.equals(Roadm.Alarma)) //Roadm {
		 *
		 * return new Roadm().getListTipos(params); }
		 *
		 * else if (oidAlarm.equals(Psedfa.Alarma)) //Sinton {
		 *
		 * return new Psedfa().getListTipos(params); } else if (oidAlarm.equals(Osnl.Alarma)) //Sinton {
		 *
		 * return new Osnl().getListTipos(params); }
		 */

		list.add("ALARMS_UNKNOWN");

		return list;

	}

	static int getPuerto(String params) {

		String[] paramsSplit = params.split("#");

		String oidAlarm = paramsSplit[1];

		Driver driver = null;

		if (oidAlarm.startsWith("1.3.6.1.6.3")) {

			return new DriverAlarmasGeneral().getPuerto(params);

		} else if (oidAlarm.equals(OptriaSX.Alarma)) // Equip OptriaSX
		{

			return new OptriaSX().getPuerto(params);

		} else if (oidAlarm.equals(Optr25gexc.Alarma)) // Tarja 4x4
		{

			return new Optr25gexc().getPuerto(params);

		} else if (oidAlarm.equals(Optr10g.Alarma)) // Tarja 10G
		{

			return new Optr10g().getPuerto(params);
		} else if (oidAlarm.equals(Optr25gexc_PM.Alarma)) // Tarja 10G
		{

			return new Optr25gexc_PM().getPuerto(params);
		}

		return -1;

	}

	/*
	 * Exemple d'alarma (temperatura)
	 *
	 * str[0] = 307100 str[1] = 1.3.6.1.4.1.18223.9.4.2.2 str[2] = 0 str[3] = 4 str[4] = 0x0A:FF:FF str[5] = TEMP str[6] = High temperature str[7] = 1
	 */

	public void setTipo(Alarm alarma, String typeID) {

		if (typeID.equals("SNMP")) {

			String raw = alarma.getRawIndo();
			String[] strList = raw.split("#");

			alarma.setTypeName(strList[6]);
			alarma.setTypeID(strList[5]);
			alarma.setSeverity(severity(strList[7]));

		} else {

			// Read properties file.
			Properties properties = new Properties();
			try {

				InputStream in = this.getClass().getClassLoader().getResourceAsStream("com/wonesys/EMSmodule/driver/alarmtype.properties");

				if (in == null) {
					// File not found! (Manage the problem)
					System.err.println("Error loading Alarm Type properties file");
				}

				properties.load(in);

				// System.out.println("lo file: " + url);

				// properties.load(url.openStream());

				String typeParams = properties.getProperty(typeID);

				String[] types = typeParams.split("#");

				// System.out.println(types);

				alarma.setTypeName(types[0]);
				alarma.setTypeID(typeID);
				alarma.setSeverity(types[2]);

			} catch (IOException e) {
				System.err.println("Error loading properties file: " + e.getMessage());
			}
		}

	}

	private String severity(String string) {
		if (string.equals("0")) // clear
			return "Clear";
		if (string.equals("1")) // warning
			return "Warning";
		if (string.equals("2")) // minor
			return "Minor";
		if (string.equals("3")) // major
			return "Major";
		if (string.equals("4")) // critical
			return "Critical";

		return "UNKNOWN";
	}

}
