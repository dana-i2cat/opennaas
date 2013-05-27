/*
 * Sfp.java
 *
 * Created on 15 de enero de 2007, 12:12
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
public class Sfp extends Driver {

	public List getListTipos(String parameters) {

		ArrayList<String> list = new ArrayList<String>();
		String[] strList = parameters.split("#");
		String str = strList[3];

		try {

			if (str.startsWith("0x01")) { // SFP presense
				list.add("SFP_PRESENCE");
			}
			if (str.startsWith("0x02")) { // SFPAlarm
				String[] params = str.split(":");

				// hay que determinar el tipo de alarma segun el bit de los params[2] y [3]
				// Por cada tipo de alarma activa, insertar un tipo de alarma al vector

				int b = Integer.decode("0x" + params[2]);

				if ((b & 0x80) > 0) {
					list.add("SFP_TEMP_HIGH_A");

				}

				if ((b & 0x40) > 0) {
					list.add("SFP_TEMP_LOW_A");

				}

				if ((b & 0x20) > 0) {
					list.add("SFP_VOLT_HIGH_A");

				}

				if ((b & 0x10) > 0) {
					list.add("SFP_VOLT_LOW_A");

				}

				if ((b & 0x08) > 0) {
					list.add("SFP_BIAS_HIGH_A");

				}

				if ((b & 0x04) > 0) {
					list.add("SFP_BIAS_LOW_A");

				}

				if ((b & 0x02) > 0) {
					list.add("SFP_TX_HIGH_A");

				}

				if ((b & 0x01) > 0) {
					list.add("SFP_TX_LOW_A");

				}

				b = Integer.decode("0x" + params[3]);

				if ((b & 0x80) > 0) {
					list.add("SFP_RX_HIGH_A");

				}

				if ((b & 0x40) > 0) {
					list.add("SFP_RX_LOW_A");

				}

			} else if (str.startsWith("0x03")) { // SFPWarning
				String[] params = str.split(":");

				// hay que determinar el tipo de alarma segun el bit de los params[2] y [3]
				// Por cada tipo de alarma activa, insertar un tipo de alarma al vector

				int b = Integer.decode("0x" + params[2]);

				if ((b & 0x80) > 0) {
					list.add("SFP_TEMP_HIGH_W");

				}

				if ((b & 0x40) > 0) {
					list.add("SFP_TEMP_LOW_W");

				}

				if ((b & 0x20) > 0) {
					list.add("SFP_VOLT_HIGH_W");

				}

				if ((b & 0x10) > 0) {
					list.add("SFP_VOLT_LOW_W");

				}

				if ((b & 0x08) > 0) {
					list.add("SFP_BIAS_HIGH_W");

				}

				if ((b & 0x04) > 0) {
					list.add("SFP_BIAS_LOW_W");

				}

				if ((b & 0x02) > 0) {
					list.add("SFP_TX_HIGH_W");

				}

				if ((b & 0x01) > 0) {
					list.add("SFP_TX_LOW_W");

				}

				b = Integer.decode("0x" + params[3]);

				if ((b & 0x80) > 0) {
					list.add("SFP_RX_HIGH_W");

				}

				if ((b & 0x40) > 0) {
					list.add("SFP_RX_LOW_W");

				}

			} else if (str.startsWith("0x04")) { // SFP/CDR Power

				String[] params = str.split(":");

				int b = Integer.decode("0x" + params[2]);

				if ((b & 0x01) > 0) {
					list.add("SFP_POWER");

				}

				if ((b & 0x02) > 0) {
					list.add("CDR_POWER");

				}

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
			if (str.startsWith("0x01") || str.startsWith("0x02") || str.startsWith("0x03") || str.startsWith("0x04") || str.startsWith("0x05")) { // SFP
																																					// presense
				String[] bytes = str.split(":");
				slot = bytes[1];
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
