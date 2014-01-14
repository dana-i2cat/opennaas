/*
 * DriverAlarmasGeneral.java
 *
 * Created on 31 de enero de 2007, 9:22
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
public class DriverAlarmasGeneral extends Driver {

	static final String	SNMPTrapsColdStart	= "1.3.6.1.6.3.1.1.5.1";
	static final String	SNMPTrapsWarmStart	= "1.3.6.1.6.3.1.1.5.2";
	static final String	SNMPTrapsLinkDown	= "1.3.6.1.6.3.1.1.5.3";
	static final String	SNMPTrapsLinkUp		= "1.3.6.1.6.3.1.1.5.4";

	/*
	 * public void setParams(Alarm alarma, String params) {
	 * 
	 * String[] paramsSplit = params.split("#");
	 * 
	 * String oidAlarm = paramsSplit[0];
	 * 
	 * if(oidAlarm.equals(SNMPTrapsColdStart)) {
	 * 
	 * setTipo(alarma,"COLDSTART");
	 * 
	 * } else if(oidAlarm.equals(SNMPTrapsWarmStart)) {
	 * 
	 * setTipo(alarma,"WARMSTART");
	 * 
	 * } else if(oidAlarm.equals(SNMPTrapsLinkDown)) {
	 * 
	 * setTipo(alarma,"LINKDOWN");
	 * 
	 * } else if(oidAlarm.equals(SNMPTrapsLinkUp)) {
	 * 
	 * setTipo(alarma, "LINKUP");
	 * 
	 * }
	 * 
	 * }
	 */

	public List getListTipos(String params) {

		ArrayList<String> list = new ArrayList<String>();
		String[] strList = params.split("#");

		try {

			String oidAlarm = strList[0];

			if (oidAlarm.equals(SNMPTrapsColdStart)) {

				list.add("COLDSTART");

			} else if (oidAlarm.equals(SNMPTrapsWarmStart)) {

				list.add("WARMSTART");

			} else if (oidAlarm.equals(SNMPTrapsLinkDown)) {

				list.add("LINKDOWN");

			} else if (oidAlarm.equals(SNMPTrapsLinkUp)) {

				list.add("LINKUP");

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
