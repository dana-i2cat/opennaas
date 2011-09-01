/*
 * Xfp.java
 *
 * Created on 20 de febrero de 2007, 9:08
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
public class Xfp extends Driver {

	public List getListTipos(String parameters) {

		ArrayList<String> list = new ArrayList<String>();
		String[] strList = parameters.split("#");
		String str = strList[3];

		try {

			if (str.startsWith("0x01")) { // SFP presense
				list.add("XFP_PRESENCE");
			}

			/*
			 * Nota: Ya que en un mismo trap pueden haber varias alarmas, hay que mirar cada uno de los bits (aprox 64, 8 bytes)
			 * 
			 * Al determinar el tipo de alarma esta se añade al vector, de forma que retornamos un vector con una, ninguna o varias alarmas.
			 */

			if (str.startsWith("0x02")) { // SFPAlarm
				String[] params = str.split(":");

				// hay que determinar el tipo de alarma segun el bit de los params[2] y [3]
				// Por cada tipo de alarma activa, insertar un tipo de alarma al vector

				int b = Integer.decode("0x" + params[2]); // address 80

				if ((byte) (b & 0x80) > 0) {
					list.add("XFP_TEMP_HIGH_A");
				}

				if ((byte) (b & 0x40) > 0) {
					list.add("XFP_TEMP_LOW_A");
				}

				/*
				 * if ((b & 0x20) > 0) { list.add("SFP_VOLT_HIGH_A); vector.add(tipoAlarma); }
				 * 
				 * if ((b & 0x10) > 0) { list.add("SFP_VOLT_LOW_A); vector.add(tipoAlarma); }
				 */

				if ((byte) (b & 0x08) > 0) {
					list.add("XFP_BIAS_HIGH_A");
				}

				if ((byte) (b & 0x04) > 0) {
					list.add("XFP_BIAS_LOW_A");
				}

				if ((byte) (b & 0x02) > 0) {
					list.add("XFP_TX_HIGH_A");
				}

				if ((byte) (b & 0x01) > 0) {
					list.add("XFP_TX_LOW_A");
				}

				b = Integer.decode("0x" + params[3]); // Address 81

				if ((byte) (b & 0x80) > 0) {
					list.add("XFP_RX_HIGH_A");
				}

				if ((byte) (b & 0x40) > 0) {
					list.add("XFP_RX_LOW_A");
				}

				/*
				 * b = Integer.decode("0x" + params[4]); //Address 82
				 * 
				 * if ((byte)(b & 0x80) > 0) { list.add("XFP_TEMP_HIGH_W"); }
				 * 
				 * if ((byte)(b & 0x40) > 0) { list.add("XFP_TEMP_LOW_W"); }
				 * 
				 * if ((b & 0x20) > 0) { list.add("SFP_VOLT_HIGH_A); vector.add(tipoAlarma); }
				 * 
				 * if ((b & 0x10) > 0) { list.add("SFP_VOLT_LOW_A); vector.add(tipoAlarma); }
				 * 
				 * if ((byte)(b & 0x08) > 0) { list.add("XFP_BIAS_HIGH_W"); }
				 * 
				 * if ((byte)(b & 0x04) > 0) { list.add("XFP_BIAS_LOW_W"); }
				 * 
				 * if ((byte)(b & 0x02) > 0) { list.add("XFP_TX_HIGH_W"); }
				 * 
				 * if ((byte)(b & 0x01) > 0) { list.add("XFP_TX_LOW_W"); }
				 * 
				 * b = Integer.decode("0x" + params[4]); //Address 83
				 * 
				 * if ((byte)(b & 0x80) > 0) { list.add("XFP_RX_HIGH_W"); }
				 * 
				 * if ((byte)(b & 0x40) > 0) { list.add("XFP_RX_LOW_W"); }
				 */
				b = Integer.decode("0x" + params[4]); // Address 84

				if ((byte) (b & 0x80) > 0) {
					list.add("XFP_TX_NR");
				}

				if ((byte) (b & 0x40) > 0) {
					list.add("XFP_TX_FAULT");
				}

				if ((byte) (b & 0x20) > 0) {
					list.add("XFP_TX_CDR");
				}

				if ((byte) (b & 0x10) > 0) {
					list.add("XFP_RX_NR");
				}

				if ((byte) (b & 0x08) > 0) {
					list.add("XFP_RX_LOS");
				}

				if ((byte) (b & 0x04) > 0) {
					list.add("XFP_RX_CDR");
				}

				if ((byte) (b & 0x02) > 0) {
					list.add("XFP_MOD_NR");
				}

				if ((byte) (b & 0x01) > 0) {
					list.add("XFP_RESET");
				}

				b = Integer.decode("0x" + params[5]); // Address 85

				if ((byte) (b & 0x80) > 0) {
					list.add("XFP_APD");
				}

				if ((byte) (b & 0x40) > 0) {
					list.add("XFP_TEC");
				}

				if ((byte) (b & 0x20) > 0) {
					list.add("XFP_WAVE_UNLOCK");
				}

				b = Integer.decode("0x" + params[6]); // Address 86

				if ((byte) (b & 0xAA) > 0) {
					list.add("XFP_VOLT_HIGH_A");
				}

				if ((byte) (b & 0x55) > 0) {
					list.add("XFP_VOLT_LOW_A");
				}

				/*
				 * b = Byte.decode("0x" + params[9]); //Address 87
				 * 
				 * if ((byte)(b & 0xAA) > 0) { list.add("XFP_VOLT_HIGH_W"); }
				 * 
				 * if ((byte)(b & 0x55) > 0) { list.add("XFP_VOLT_LOW_W"); }
				 */

			} else if (str.startsWith("0x03")) { // Estado del CDR XFP

				list.add("XFP_CDR_STATUS");

			} else if (str.startsWith("0x04")) { // SFP/CDR Power
				/*
				 * NO IMPLEMENTADO SEGUN EL MANUAL DEL 10G
				 */

			}

		} catch (Exception e) {
			// error de decoding, id = unknown

			e.printStackTrace();

		}

		return list;

	}

	public int getPuerto(String params) {

		try {

			String[] paramsList = params.split("#");

			String str = paramsList[8];

			String slot = "00";

			// extreure el slot del sfp del alarma
			if (str.startsWith("0x01") || str.startsWith("0x02") || str.startsWith("0x03") || str.startsWith("0x05")) { // XFP presense
				String[] bytes = str.split(":");
				slot = bytes[1];
			}

			else {
				return -1;
			}

			slot = slot.substring(1);

			return Integer.parseInt(slot);

		} catch (Exception e) {
			return -1;
		}

	}

}
