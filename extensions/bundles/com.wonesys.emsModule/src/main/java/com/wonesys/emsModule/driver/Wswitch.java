/*
 * Os410G.java
 *
 * Created on 30 de enero de 2007, 9:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.driver;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys EMSModule
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