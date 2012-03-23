/*
 * Optr25gexc_PM.java
 *
 * Created on 16 de marzo de 2010, 10:15
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
public class Optr25gexc_PM extends Driver {

	public static int		idNumber	= 8;
	public static String	ID			= "1.3.6.1.4.1.18223.9.8";
	public static String	Alarma		= "1.3.6.1.4.1.18223.9.8.2.2";
	public static String	nombre		= "OPTR2G5EXC-PM";
	public static String	informacion	= "OPTR2G5EXC-PM";
	public static int		numPorts	= 3;

	/*
	 * public void setParams(Alarm alarma, String params) {
	 *
	 * try {
	 *
	 * String[] strList = params.split("#");
	 *
	 * String tipusAlarmaS = strList[3];
	 *
	 *
	 * if (strList[3].startsWith("0x01") || strList[3].startsWith("0x02") || strList[3].startsWith("0x03") || strList[3].startsWith("0x04")){
	 *
	 * Sfp sfp = new Sfp(); // sfp.setParams(alarma, params);
	 *
	 * return; }
	 *
	 *
	 * else if(tipusAlarmaS.startsWith("0x05")) { //CDR Status setTipo(alarma,"CROSS_CDR");
	 *
	 *
	 * } else if(tipusAlarmaS.startsWith("0x09")) { //Board Power setTipo(alarma,"CHASIS_POWER");
	 *
	 *
	 * } else if(tipusAlarmaS.startsWith("0x0A")) { //Board Temperature setTipo(alarma,"CHASIS_TEMP");
	 *
	 *
	 * } else if(tipusAlarmaS.startsWith("0x0B")) { //EXC Status setTipo(alarma,"CROSS_EXC");
	 *
	 *
	 * } else if(tipusAlarmaS.startsWith("0x10")) { //Reset setTipo(alarma,"CROSS_RESET");
	 *
	 * }
	 *
	 * } catch (Exception e) { //error de decoding, id = unknown
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
		String[] strList = params.split("#");
		String tipusAlarmaS = strList[3];

		try {/*
			 * if (strList[3].startsWith("0x01") || strList[3].startsWith("0x02") || strList[3].startsWith("0x03") || strList[3].startsWith("0x04")){
			 *
			 * Sfp sfp = new Sfp(); // xfp.setParams(alarma, params); return sfp.getListTipos(params);
			 *
			 * } else
			 */
			if (tipusAlarmaS.startsWith("0x08")) { // CDR Status
				list.add("PM2G5_SFP_PRESENCE");

			}/*
			 * else if(tipusAlarmaS.startsWith("0x09")) { //Board Power list.add("CHASIS_POWER");
			 *
			 *
			 * } else if(tipusAlarmaS.startsWith("0x0A")) { //Board Temperature list.add("CHASIS_TEMP");
			 *
			 *
			 * } else if(tipusAlarmaS.startsWith("0x0B")) { //EXC Status list.add("CROSS_EXC");
			 *
			 *
			 * } else if(tipusAlarmaS.startsWith("0x10")) { //Reset list.add("CROSS_RESET");
			 *
			 * }
			 */

		} catch (Exception e) {
			// error de decoding, id = unknown

			e.printStackTrace();

		}

		return list;

	}

	public int getPuerto(String params) {
		try {

			String[] paramsList = params.split("#");
			String slot = "00";
			String str = paramsList[8];

			if (!(str.startsWith("0x01") || str.startsWith("0x02") || str.startsWith("0x03") || str.startsWith("0x05") || str.startsWith("0x06") || str
					.startsWith("0x07"))) {
				String[] bytes = str.split(":");
				slot = bytes[2];
			} else {
				return -1;
			}

			slot = slot.substring(1);

			return Integer.parseInt(slot);

		} catch (Exception e) {
			return -1;
		}
	}

}
