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
public class Roadm extends Driver {

	public static String	ID		= "1.3.6.1.4.1.18223.9.11";
	// TODO id alarma incorrecte! canviar
	public static String	Alarma	= "1.3.6.1.4.1.18223.9.11.2.3";

	public List getListTipos(String params) {

		ArrayList<String> list = new ArrayList<String>();
		String[] strList = params.split("#");
		String tipusAlarmaS = strList[3];

		if (!tipusAlarmaS.startsWith("0x")) {

			char[] chars = tipusAlarmaS.toCharArray();
			tipusAlarmaS = "";

			for (int i = 0; i < chars.length; i++) {

				String o = Integer.toHexString(chars[i]);
				tipusAlarmaS += o.length() < 2 ? "0" + o : o;

				if (i < chars.length - 1)
					tipusAlarmaS += ":";
			}

			tipusAlarmaS = "0x" + tipusAlarmaS.toUpperCase();

		}

		if (tipusAlarmaS.startsWith("0x01")) { // Fallo Alimentacion ROADM (Capella)
			list.add("ROADM_POWER");
		}
		else if (tipusAlarmaS.startsWith("0x02")) { // Fallo Procesador ROADM (Capella)
			list.add("ROADM_PROC");
		}
		else if (tipusAlarmaS.startsWith("0x03")) { // Led Rojo ROADM (Capella)
			list.add("ROADM_ERROR");
		}
		else if (tipusAlarmaS.startsWith("0x09")) { // Power Placa
			list.add("ROADM_POWER_CARD");
		}
		else if (tipusAlarmaS.startsWith("0x0A")) { // Temperatura Placa
			list.add("ROADM_TEMP");
		}

		else if (tipusAlarmaS.startsWith("0x0C")) { // Fallo Rele 1 (5 (Capella)/3.3V(Metconnex))
			list.add("ROADM_POWER_RELE1");
		}
		else if (tipusAlarmaS.startsWith("0x0B")) { // Fallo Rele 2 (12V)
			list.add("ROADM_POWER_RELE2");
		}
		else if (tipusAlarmaS.startsWith("0x0D")) { // Fallo Rele 3 (5V) ( Metconnex)
			list.add("ROADM_POWER_RELE3");
		}

		else if (tipusAlarmaS.startsWith("0x0E")) { // Fallo Rele 4 (N5V) (Metconnex)
			list.add("ROADM_POWER_RELE4");
		}
		else if (tipusAlarmaS.startsWith("0x0F")) { // Reset
			list.add("ROADM_RESET");
		}

		return list;
	}

	public int getPuerto(String params) {

		return -1;

	}

}