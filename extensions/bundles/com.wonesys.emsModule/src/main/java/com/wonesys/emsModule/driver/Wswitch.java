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
public class Wswitch extends Driver {

	public static String	ID			= "W-SWITCH";
	public static String	nombre		= "W-SWITCH";
	public static String	informacion	= "W-SWITCH";

	public List getListTipos(String params) {
		ArrayList<String> list = new ArrayList<String>();
		return list;
	}

	public List getListTipos(String params, String oids, String genericTrap) {

		ArrayList<String> list = new ArrayList<String>();
		String id = "";

		try {
			String[] strList = params.split("#");
			String tipusAlarmaS = strList[0];
			String[] oidList = oids.split("#");
			String oidAlarma = oidList[0];
			/*
			 * Trap information: 2# OIDS: 1.3.6.1.2.1.2.2.1.1.2#
			 */
			if (genericTrap.equals("2")) {
				id = "LINKDOWN_" + tipusAlarmaS;
				list.add(id);
			}
			if (genericTrap.equals("3")) {
				id = "LINKUP_" + tipusAlarmaS;
				list.add(id);
			}
			if (oidAlarma.equals("1.3.6.1.4.1.8691.7.7.2.2")) {
				id = "W_SWITCH_POWER_1";
				list.add(id);
			}
			if (oidAlarma.equals("1.3.6.1.4.1.8691.7.7.2.3")) {
				id = "W_SWITCH_POWER_2";
				list.add(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public int getPuerto(String params) {

		return -1;

	}

}