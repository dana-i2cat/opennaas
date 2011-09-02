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
public class Sinton extends Driver {

	public static String	ID		= "1.3.6.1.4.1.18223.9.2";
	public static String	Alarma	= "1.3.6.1.4.1.18223.9.2.2.3";

	public List getListTipos(String params) {

		/*
		 * 01: Access SFP Presence 02: Trunk Sinton Laser ready 03: Sinton Laser Alarm2 04: Sinton Laser Alarm1 05: Access SFP Alarms 06: Access SFP
		 * Warnings 07: Sinton Laser Power Supply state 08: Transponder power supply state 12: SFP status change 13: Sinton module status change 14:
		 * Sinton tx power change
		 */

		ArrayList<String> list = new ArrayList<String>();
		String[] strList = params.split("#");
		String tipusAlarmaS = strList[3];
		String[] bytes = tipusAlarmaS.split(":");

		if (tipusAlarmaS.startsWith("0x01")) { // Access SFP Presence
			if (bytes[1].equals("01"))
				list.add("SINTON_SFP_PRESENCE_YES");
			else if (bytes[1].equals("00"))
				list.add("SINTON_SFP_PRESENCE_NO");
		} else if (tipusAlarmaS.startsWith("0x02")) { // Trunk Sinton Laser ready
			if (bytes[1].equals("01"))
				list.add("SINTON_READY_YES");
			else if (bytes[1].equals("00"))
				list.add("SINTON_READY_NO");
		} else if (tipusAlarmaS.startsWith("0x03")) { // Alarma Sinton 1
			list = (ArrayList) alarmaSinton(bytes);
		} else if (tipusAlarmaS.startsWith("0x04")) { // Alarma Sinton 2
			list = (ArrayList) alarmaSinton(bytes);
		} else if (tipusAlarmaS.startsWith("0x05")) { // Alarma SFP
			int b = Integer.decode("0x" + bytes[1]);
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

			b = Integer.decode("0x" + bytes[2]);

			if ((b & 0x80) > 0) {
				list.add("SFP_RX_HIGH_A");
			}
			if ((b & 0x40) > 0) {
				list.add("SFP_RX_LOW_A");
			}
		} else if (tipusAlarmaS.startsWith("0x06")) { // Warning SFP

			int b = Integer.decode("0x" + bytes[1]);
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

			b = Integer.decode("0x" + bytes[2]);

			if ((b & 0x80) > 0) {
				list.add("SFP_RX_HIGH_W");
			}
			if ((b & 0x40) > 0) {
				list.add("SFP_RX_LOW_W");
			}
		} else if (tipusAlarmaS.startsWith("0x07")) { // Sinton Laser Power Supply state
			list.add("SINTON_POWER");
		} else if (tipusAlarmaS.startsWith("0x08")) { // Transponder power supply state
			list.add("SINTON_SFP_POWER");
		} else if (tipusAlarmaS.startsWith("0x0C")) { // SFP status change
			list.add("SINTON_SFP_STATUS");
		} else if (tipusAlarmaS.startsWith("0x0D")) { // Sinton module status change
			list.add("SINTON_SFP_STATUS");
		} else if (tipusAlarmaS.startsWith("0x0E")) { // Sinton tx power change
			list.add("SINTON_TX_POWER");
		}

		return list;
	}

	private List alarmaSinton(String[] bytes) {

		ArrayList<String> list = new ArrayList<String>();

		/*
		 * Bit 15 Reserved Bit 7 0  Case Temperature normal 1 Case Temperature above 70°C Bit 14 UART Error Bit 6 0  Laser Currents Normal 1 
		 * Current exceeds end of life value Bit 13 IO Register Failure Bit 5 0  Open loop operation selected 1  Closed loop operation selected Bit
		 * 12 Data Checksum Failure Bit 4 0  DC and Clocks O.K. 1  Reset or supply/clock failure Bit 11 Program Checksum Failure Bit 3 0 
		 * Wavelength Stable 1  Wavelength Locking failure Bit 10 Boot Checksum Failure Bit 2 0  Optical power normal 1 Optical power low Bit 9 0
		 * No Aging Update Failure Bit 1 0  Locker temperature stable 1  Aging Update Failure 1  Locker temperature unstable Bit 8 0  Laser is
		 * OFF Bit 0 0 Laser temperature stable 1  Laser is ON 1 Laser temperature unstable
		 */

		int b = Integer.decode("0x" + bytes[1]);

		if ((b & 0x40) > 0) { // UART Error
			list.add("SINTON_UART_ERROR");
		}
		if ((b & 0x20) > 0) { // IO Register Failure
			list.add("SINTON_IO_FAILURE");
		}
		if ((b & 0x10) > 0) { // Data Checksum Failure
			list.add("SINTON_DATA_CHECKSUM");
		}
		if ((b & 0x08) > 0) {
			list.add("SINTON_PROGRAM_CHECKSUM");
		}
		if ((b & 0x04) > 0) { // Boot Checksum Failure
			list.add("SINTON_BOOT_CHECKSUM");
		}
		if ((b & 0x02) > 0) { // Aging Update Failure
			list.add("SINTON_AGING_UPDATE_FAIL");
		}
		if ((b & 0x01) == 0) { // Laser is OFF
			list.add("SINTON_LASER_OFF");
		}

		b = Integer.decode("0x" + bytes[2]);

		if ((b & 0x80) > 0) { // Case Temperature above 70°C
			list.add("SINTON_HIGH_TEMP");
		}
		if ((b & 0x40) > 0) { // Current exceeds end of life value
			list.add("LASER_END_OF_LIFE");
		}
		if ((b & 0x10) > 0) { // Reset or supply/clock failure
			list.add("SINTON_RESET");
		}
		if ((b & 0x08) > 0) { // Wavelength Locking failure
			list.add("SINTON_WAVELENGTH_LOCKING");
		}
		if ((b & 0x04) > 0) { // Optical power low
			list.add("SINTON_LOW_OPTICAL_POWER");
		}
		if ((b & 0x02) > 0) { // Locker temperature unstable
			list.add("SINTON_LOCKER_TEMP");
		}
		if ((b & 0x01) == 0) { // Laser temperature unstable
			list.add("SINTON_LASER_TEMP");
		}

		return list;

	}

	public int getPuerto(String params) {

		return -1;

	}

}