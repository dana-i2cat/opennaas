/*
 * Os410G.java
 *
 * Created on 30 de enero de 2007, 9:20
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
public class Optr10g extends Driver {

	public static int		idNumber	= 5;
	public static String	ID			= "1.3.6.1.4.1.18223.9.5";
	public static String	Alarma		= "1.3.6.1.4.1.18223.9.5.2.3";
	public static String	nombre		= "OPTR10G";
	public static String	informacion	= "OPTR10G";

	/*
	 * public void setParams(Alarm alarma, String params) {
	 *
	 * String[] strList = params.split("#");
	 *
	 * String tipusAlarmaS = strList[3];
	 *
	 * try {
	 *
	 * if (strList[3].startsWith("0x01") || strList[3].startsWith("0x02") || strList[3].startsWith("0x03") || strList[3].startsWith("0x04")){
	 *
	 * Xfp xfp = new Xfp(); // xfp.setParams(alarma, params); return;
	 *
	 * } else if(tipusAlarmaS.startsWith("0x09")) { //Board Power setTipo(alarma,"G10_POWER");
	 *
	 * } else if(tipusAlarmaS.startsWith("0x0A")) { //Board Temperature setTipo(alarma,"G10_TEMP");
	 *
	 *
	 * } else if(tipusAlarmaS.startsWith("0x0B")) { //EXC Status setTipo(alarma,"G10_CONFIG");
	 *
	 *
	 * } else if(tipusAlarmaS.startsWith("0x0C")) { //EXC Status setTipo(alarma,"G10_EXC_COM");
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
	 * }
	 */

	public List getListTipos(String params) {

		ArrayList<String> list = new ArrayList<String>();
		String[] strList = params.split("#");
		String tipusAlarmaS = strList[3];

		try {
			if (strList[3].startsWith("0x01") ||
								strList[3].startsWith("0x02") ||
								strList[3].startsWith("0x03") ||
								strList[3].startsWith("0x04")) {

				Xfp xfp = new Xfp();
				// xfp.setParams(alarma, params);
				return xfp.getListTipos(params);

			} else if (tipusAlarmaS.startsWith("0x09")) { // Board Power
				list.add("G10_POWER");

			} else if (tipusAlarmaS.startsWith("0x0A")) { // Board Temperature
				list.add("G10_TEMP");

			} else if (tipusAlarmaS.startsWith("0x0B")) { // EXC Status
				list.add("G10_CONFIG");

			} else if (tipusAlarmaS.startsWith("0x0C")) { // EXC Status
				list.add("G10_EXC_COM");

			} else if (tipusAlarmaS.startsWith("0x10")) { // Reset
				list.add("CROSS_RESET");

			}

		} catch (Exception e) {
			// error de decoding, id = unknown

			e.printStackTrace();

		}

		return list;

	}

	public int getPuerto(String params) {

		return new Xfp().getPuerto(params);

	}

}