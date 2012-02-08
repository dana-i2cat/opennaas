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
public class Wcmctc extends Driver {

	public static String	ID			= "W-CMC-TC";
	public static String	nombre		= "W-CMC-TC";
	public static String	informacion	= "W-CMC-TC";

	public List getListTipos(String params) {
		ArrayList<String> list = new ArrayList<String>();
		return list;
	}

	public List getListTipos(String params, String oids) {

		ArrayList<String> list = new ArrayList<String>();

		try {
			String[] strList = params.split("#");
			String tipusAlarmaS = "";
			if (strList.length > 4)
				tipusAlarmaS = strList[4];
			String[] oidList = oids.split("#");
			if (tipusAlarmaS.equals("Access Sensor")) {
				/*
				 * [STDOUT] Access Sensor - wonesys@wonesys.com#W16-CMC-TC#None#1#Access Sensor#4# - 0 1.3.6.1.2.1.1.4.0 1 #1.3.6.1.2.1.1.5.0 2
				 * #1.3.6.1.2.1.1.6.0 3 #1.3.6.1.4.1.2606.4.2.3.7.2.1.1.1 4 #1.3.6.1.4.1.2606.4.2.3.7.2.1.2.1 5 #1.3.6.1.4.1.2606.4.2.3.7.2.1.3.1#
				 */
				String sensorUnit = String.valueOf(Integer.parseInt(String.valueOf(oidList[3].charAt(21))) - 2);
				String id = "CMC-TC-" + sensorUnit + "-" + strList[3];
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