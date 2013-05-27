/*
 * Osnl.java
 *
 * Created on 27 de agosto de 2008, 10:39
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
public class Osnl extends Driver {

    public static String ID = "1.3.6.1.4.1.18223.9.15";
    public static String Alarma = "1.3.6.1.4.1.18223.9.15.2.2";

    /** Creates a new instance of Osnl */
    public Osnl() {
    }

    public List getListTipos(String params) {

        ArrayList<String> list = new ArrayList<String>();
        String[] strList = params.split("#");
        String tipusAlarmaS = strList[3];

        if(!tipusAlarmaS.startsWith("0x")){

            char[] chars = tipusAlarmaS.toCharArray();
            tipusAlarmaS = "";

            for (int i = 0; i< chars.length; i++) {

                String o = Integer.toHexString(chars[i]);
                tipusAlarmaS += o.length() < 2 ? "0"+o : o;

                if(i < chars.length-1)
                    tipusAlarmaS += ":";
            }

            tipusAlarmaS = "0x" + tipusAlarmaS.toUpperCase();

        }

        if(tipusAlarmaS.contains("01")) { //Port 0 fiber B input power is too low.
            list.add("OSNL_0_B_RX_LOW");
        }
        if(tipusAlarmaS.contains("02")) { //Port 0 fiber B input power is too high.
            list.add("OSNL_0_B_RX_HIGH");
        }
        if(tipusAlarmaS.contains("03")) { //Port 0 fiber A input power is too low.
            list.add("OSNL_0_A_RX_LOW");
        }
        if(tipusAlarmaS.contains("04")) { //Port 0 fiber A input power is too high.
            list.add("OSNL_0_A_RX_HIGH");
        }
        if(tipusAlarmaS.contains("05")) { //Port 1 fiber B input power is too low.
            list.add("OSNL_1_B_RX_LOW");
        }
        if(tipusAlarmaS.contains("06")) { //Port 1 fiber B input power is too high.
            list.add("OSNL_1_B_RX_HIGH");
        }
        if(tipusAlarmaS.contains("07")) { //Port 1 fiber A input power is too low.
            list.add("OSNL_1_A_RX_LOW");
        }
        if(tipusAlarmaS.contains("08")) { //Port 1 fiber A input power is too high.
            list.add("OSNL_1_A_RX_HIGH");
        }
        if(tipusAlarmaS.contains("09")) { //Port 1 has been conmuted.
            list.add("OSNL_PORT_1_COM");
        }
        if(tipusAlarmaS.contains("0A")) { //Port 0 has been conmuted.
            list.add("OSNL_PORT_0_COM");
        }
        if(tipusAlarmaS.contains("0B")) { //Port 0 Fiber A ok.
            list.add("OSNL_PORT_0_A_OK");
        }
        if(tipusAlarmaS.contains("0C")) { //Port 0 Fiber B ok.
            list.add("OSNL_PORT_0_B_OK");
        }
         if(tipusAlarmaS.contains("0D")) { //Port 1 Fiber A ok.
            list.add("OSNL_PORT_1_A_OK");
        }
        if(tipusAlarmaS.contains("0E")) { //Port 1 Fiber B ok.
            list.add("OSNL_PORT_1_B_OK");
        }
        if(tipusAlarmaS.contains("10")) { //Reset
            list.add("OSNL_RESET");
        }

        return list;
    }

    public int getPuerto(String params) {
        return -1;
    }

}
