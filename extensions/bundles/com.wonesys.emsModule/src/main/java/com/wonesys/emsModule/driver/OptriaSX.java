/*
 * OptriaS4.java
 *
 * Created on 21 de diciembre de 2006, 10:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.driver;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mbeltran
 */
public class OptriaSX extends Driver {

	public static String	ID		= "1.3.6.1.4.1.18223.9.1";
	public static String	Alarma	= "1.3.6.1.4.1.18223.9.1.2.2";

	/*
	 * public void setParams(Alarm alarma, String params) {
	 * 
	 * try {
	 * 
	 * // System.out.println("entra");
	 * 
	 * String[] strList = params.split("#");
	 * 
	 * String tipusAlarmaS = strList[2];
	 * 
	 * if(tipusAlarmaS.equals("0x01")) { //temperatura setTipo(alarma,"ALARMS_CHASIS_TEMP");
	 * 
	 * } else if(tipusAlarmaS.startsWith("0x02")) { //potencia setTipo(alarma,"ALARMS_CHASIS_POWER");
	 * 
	 * } else if(tipusAlarmaS.startsWith("0x03")) { //ventiladores setTipo(alarma,"ALARMS_CHASIS_FAN");
	 * 
	 * } else if(tipusAlarmaS.startsWith("0x04")) { //tarjetas setTipo(alarma,"ALARMS_CHASIS_CARD");
	 * 
	 * } else setTipo(alarma,"ALARMS_UNKNOWN"); } catch (Exception e) { //error de decoding, id = unknown
	 * 
	 * e.printStackTrace();
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

	public List getListTipos(String params) {

		ArrayList<String> list = new ArrayList<String>();

		try {

			// System.out.println("entra");

			String[] strList = params.split("#");

			String tipusAlarmaS = strList[3];

			if (tipusAlarmaS.equals("0x01")) { // temperatura
				list.add("ALARMS_CHASIS_TEMP");

			} else if (tipusAlarmaS.startsWith("0x02")) { // potencia
				list.add("ALARMS_CHASIS_POWER");

			} else if (tipusAlarmaS.startsWith("0x03")) { // ventiladores
				list.add("ALARMS_CHASIS_FAN");

			} else if (tipusAlarmaS.startsWith("0x04")) { // tarjetas
				list.add("ALARMS_CHASIS_CARD");

			} else if (tipusAlarmaS.startsWith("0x05")) { // Dummy trap for test
				list.add("DUMMY");

			}

		} catch (Exception e) {
			// error de decoding, id = unknown

			e.printStackTrace();

		}

		return list;

	}

	public int getPuerto(String params) {

		return -1;

	}

}
