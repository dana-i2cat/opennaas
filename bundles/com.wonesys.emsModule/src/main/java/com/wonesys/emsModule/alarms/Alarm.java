/*
 * Alarm.java
 *
 * Created on 8 de abril de 2008, 15:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.alarms;

import java.util.Calendar;

/**
 * Alarm object representation.
 * <br>
 * Contains all the information form a registered alarm.
 *
 * @author mbeltran
 */

public class Alarm {
    
    private long dataRecepcio;
    private long dataUltima;
    private String typeName;
    private String typeID;
    private String group;
    private String severity;
    private String rawInfo;
    private int repet;
    private String ip;
    private int chasis;
    private int slot;
    private int port;
    
    /** 
     * Creates a new instance of Alarm 
     *
     * @param ip IP of the entity
     * @param oid alarm oid
     * @param params SNMP message in propietary format
     *
     */
    public Alarm(String ip, String oid, String params) {
        this.ip = ip;
        this.repet = 0;
        
        Calendar timerebut = Calendar.getInstance();
        
        this.dataRecepcio = timerebut.getTimeInMillis();
        this.dataUltima = timerebut.getTimeInMillis();
        
        String[] paramsSplit = params.split("#");
        
        if (paramsSplit.length > 3)
            try {
            this.chasis = Integer.valueOf(paramsSplit[2]);
            } catch (Exception e) {
                this.chasis = -1;
            }
            
        else
            this.chasis = -1;
        
        if (paramsSplit.length > 4)
            try {
                this.slot = Integer.valueOf(paramsSplit[3]);
            } catch (Exception e) {
                this.slot = -1;
            }
        else
            this.slot = -1;
        
        this.rawInfo = params;
        
    }
    
    
    /** 
     * Returns the first reception time of the alarm
     *
     * @return first repcetion time in POSIX milliseconds format
     *
     */
    public long getDataRecepcio() {
        return dataRecepcio;
    }
    
    /** 
     * Returns the last reception time of the alarm
     *
     * @return last repcetion time in POSIX milliseconds format
     *
     */
    public long getDataUltima() {
        return dataUltima;
    }
    
    /** 
     * Returns the type name of the alarm
     *
     * @return type name in human language format
     *
     */
    public String getTypeName() {
        return typeName;
    }
    
    /** 
     * Returns the type ID of the alarm
     *
     * @return type ID in a coded format
     *
     */
    public String getTypeID() {
        return typeID;
    }
    
     /** 
     * Returns the alarm severity
     * <p>
     * Posible values are:
     *  <ul>
     * <li><b>Critical</b> Critical alarm, system will stop working
     * <li><b>Major</b> Major alarm, system need attention
     * <li><b>Minor</b> Minor alarm, system should need attention
     * <li><b>Warning</b> Warning alarm, system will need attention
     * <li><b>Normal</b> System information message
     * <li><b>Clear</b> Alarm cleared message
     * <li><b>Unknown</b> Unknown alarm
     * </ul>
     *
     * @return type ID in a coded format
     *
     */
    public String getSeverity() {
        return severity;
    }
     
    /** 
     * Returns the SNMP messange
     *
     * @return the SNMP message
     *
     */
    public String getRawIndo() {
        return rawInfo;
    }
     
    /** 
     * Returns the count of alarms received
     *
     * @return count of alarms received
     *
     */
    public int getRepet() {
        return repet;
    }
     
    /** 
     * Returns the IP of the entity that send the alarms
     *
     * @return IP in string format
     *
     */
    public String getIp() {
        return ip;
    }
     
    /** 
     * Returns the slot of the card that caused the alarm, -1 if not set
     *
     * @return slot in int format
     *
     */
    public int getSlot() {
        return slot;
    }
     
    /** 
     * Returns the port number that caused the alarm, -1 if not set
     *
     * @return slot in int format
     *
     */
    public int getPort() {
        return port;
    }
     
    /** 
     * Sets the last received alarm time value
     *
     * @param dataUltima received alarm time in milliseconds 
     *
     */
    public void setDataUltima(long dataUltima) {
        this.dataUltima = dataUltima;
    }
     
    /** 
     * Increases de number of times that the same alarm has been received
     *
     */
    void incRepet() {
        repet++;
    }

    /** 
     * Sets the severity of the alarm
     *
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /** 
     * Sets the group of the alarm
     *
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /** 
     * Sets the typeID of the alarm
     *
     */
    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    /** 
     * Sets the typeName of the alarm
     *
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
     
    public void setPort(int port) {
        this.port = port;
    }

    /** 
     * Retuns a string representation of the alarm in this format:
     * <p>
     * IP: ip_value Chasis: chasis_number Slot: slot_number Port: port_number RawInfo: snmp_info typeName: type_name typeID: type_ID dataRecepcio: date_milis dataUltima: date_milis severity: severity repet: number
     *
     */
    public String toString() {
        
        String out = "";
        out +=  "IP: " + this.ip;
        out +=  " Chasis: " + this.chasis;
        out +=  " Slot: " + this.slot;
        out +=  " Port: " + this.port;
        out +=  " RawInfo: " + this.rawInfo;
        out +=  " typeName: " + this.typeName;
        out +=  " typeID: " + this.typeID;
        out +=  " dataRecepcio: " + this.dataRecepcio;
        out +=  " dataUltima: " + this.dataUltima;
        out +=  " severity: " + this.severity;
        out +=  " repet: " + this.repet;
                
        return out;
    }

  

    
}
