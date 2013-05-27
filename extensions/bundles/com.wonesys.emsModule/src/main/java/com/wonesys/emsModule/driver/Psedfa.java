/*
 * Psedfa.java
 *
 * Created on 27 de agosto de 2008, 9:20
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
public class Psedfa extends Driver {

     public static String ID = "1.3.6.1.4.1.18223.9.14";
    //TODO id alarma incorrecte! canviar
    public static String Alarma = "1.3.6.1.4.1.18223.9.14.2.2";

    /** Creates a new instance of Psedfa */
    public Psedfa() {
    }

    public List getListTipos(String params) {

          /*
         *
        12:58:44,079 INFO  [STDOUT] hostaddr = 192.168.0.62, ver = 1, deco = false
        12:58:44,091 INFO  [STDOUT] varbind:0   29000
        12:58:44,091 INFO  [STDOUT] varbind:1   1.3.6.1.4.1.18223.9.11.2.3
        12:58:44,091 INFO  [STDOUT] varbind:2   0
        12:58:44,091 INFO  [STDOUT] varbind:3   15
        12:58:44,092 INFO  [STDOUT] varbind:4   0x0F
        12:58:44,114 WARN  [alarms] Element 192.168.0.62 not found on alarm OID 1.3.6.1.4.1.18223.9.11.2.3 Trap Information: 29000#1.3.6.1.4.1.18223.9.11.2.3#0#15#0x0F#

         */

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

        if(tipusAlarmaS.startsWith("0x01")) { //Loss of input
            list.add("PSEDFA_LOSS_INPUT");
        }
        else if(tipusAlarmaS.startsWith("0x02")) { //Loss of output
               list.add("PSEDFA_LOSS_OUTPUT");

        }
        else if(tipusAlarmaS.startsWith("0x03")) { //Temp alarm
               list.add("PSEDFA_MODULE_TEMP");

        }
        else if(tipusAlarmaS.startsWith("0x04")) { //Pump my bias alarm
               list.add("PSEDFA_BIAS");

        }
        else if(tipusAlarmaS.startsWith("0x05")) { //Pump my bias temp alarm
               list.add("PSEDFA_BIAS_TEMP");

        }
        else if(tipusAlarmaS.startsWith("0x09")) { //Power Placa
               list.add("PSEDFA_POWER_CARD");

        }
        else if(tipusAlarmaS.startsWith("0x0A")) { //Temperatura Placa
               list.add("PSEDFA_TEMP");

        }

        else if(tipusAlarmaS.startsWith("0x0C")) { //Fallo Rele 1 (5 (Capella)/3.3V(Metconnex))
               list.add("PSEDFA_POWER_RELE1");

        }
        else if(tipusAlarmaS.startsWith("0x0B")) { //Fallo Rele 2 (12V)
               list.add("PSEDFA_POWER_RELE2");

        }

        else if(tipusAlarmaS.startsWith("0x0F")) { //Reset
               list.add("PSEDFA_RESET");

        }


            return list;

    }

    public int getPuerto(String params) {

        return -1;
    }

}
