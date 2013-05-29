package org.opennaas.extensions.gim.controller.snmp;



import java.io.IOException;


import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.snmp4j.smi.OID;


public class APCDriver_SNMP {
	SNMPManager client;
	
	int apcNumBanks = 1;
	boolean apcOutletMonitoring = false;
	
	String deviceNameOID = "1.3.6.1.4.1.318.1.1.26.2.1.3.1"; //rpdu2
	
	String deviceVoltageOID = "1.3.6.1.4.1.318.1.1.26.6.3.1.6.1"; // rpdu2
	
	String devicePowerOID = "1.3.6.1.4.1.318.1.1.26.4.3.1.5.1"; // rpdu2
	String deviceEnergyOID = "1.3.6.1.4.1.318.1.1.26.4.3.1.9.1"; // rpdu2
	String deviceCurrentOID = "1.3.6.1.4.1.318.1.1.26.6.3.1.5.1"; // rpdu2
	
	String rootOutletNameOID = "1.3.6.1.4.1.318.1.1.26.9.4.1.1.3";
	String rootOutletCurrentOID = "1.3.6.1.4.1.318.1.1.26.9.4.3.1.6"; // rpdu2
	String rootOutletPowerOID = "1.3.6.1.4.1.318.1.1.26.9.4.3.1.7";
	
	String rootOutletEnergyOID = "1.3.6.1.4.1.318.1.1.26.9.4.3.1.11";
	
	//String rootOutletCurrentOID = ".1.3.6.1.4.1.318.1.1.26.9.4.3.1.7"; // for rPDU2, not supported by PDU
	
	String rootOutletStatusOID = "1.3.6.1.4.1.318.1.1.26.9.2.3.1.5"; //.1.3.6.1.4.1.318.1.1.12.3.5.1.1.4";
	String rootOutletCommandOID = "1.3.6.1.4.1.318.1.1.26.9.2.4.1.5"; //".1.3.6.1.4.1.318.1.1.12.3.3.1.1.4";
	
	
	public APCDriver_SNMP(String SNMPIP) throws IOException{
		
			client = new SNMPManager(SNMPIP);
			client.start();

	}
	
	public String getDeviceName() throws IOException{
		return client.getAsString(new OID(deviceNameOID));
	}
	
	public String getOutletName(int targetOutletIndex) throws IOException{
		return client.getAsString(new OID(rootOutletNameOID+"."+targetOutletIndex));
	}
	
	public Double getCurrentPower(int targetOutletIndex) throws IOException{
		
			/* 
			 *  as this APC PDU cannot report power at the outlet level, this is queried at the device level
			 *	if this device were to be used in reality we would have to do further manipulation of this value
			 *	or alternatively make the querying powersupply class aware that this value is the device value
			 */
		
			return Double.parseDouble(client.getAsString(new OID(rootOutletPowerOID+"."+targetOutletIndex)))/1000; // MIB value returned in hundredths of KW

	}
	
	public Double getCurrentEnergy(int targetOutletIndex) throws IOException{
		
		/* 
		 *  as this APC PDU cannot report energy at the outlet level, this is queried at the device level
		 *	if this device were to be used in reality we would have to do further manipulation of this value
		 *	or alternatively make the querying powersupply class aware that this value is the device value
		 */
	
		return Double.parseDouble(client.getAsString(new OID(rootOutletEnergyOID+"."+targetOutletIndex)))/10; // MIB value returned in tenths of KWh

}
	

	public Double getCurrentVoltage(int targetOutletIndex) throws IOException{
		/* 
		 *  The assumption here will be that each outlet will have the same voltage draw. Each individual device may perform in-line voltage adaptation but 
		 *  the ultimate voltage draw should be the same? Therefore the device voltage *should* be the same.
		 *  As a result the voltage OID is that of the overall device
		 */
		
		return Double.parseDouble(client.getAsString(new OID(deviceVoltageOID)));
	}

	public Double getCurrentCurrent(int targetOutletIndex) throws IOException{
		
		/*
		 * The current on this PDU is only reported at the 'device' level. This is represented as a PDU with only 1 bank, so we 
		 * query the Bank#1 for the device current.
		 * For an alternative PDU the rootOutletCurrentOID (from rPDU) should query the outlet current
		 */
		
		//return Double.parseDouble(client.getAsString(new OID(rootOutletCurrentOID+"."+targetOutletIndex)));
		return Double.parseDouble(client.getAsString(new OID(rootOutletCurrentOID+"."+targetOutletIndex)))/10;

		}

	public boolean getOutletStatus(int targetOutletIndex) throws IOException{
		//returns true if outlet is on, false if outlet is off
		
		int status = Integer.parseInt(client.getAsString(new OID(rootOutletStatusOID+"."+targetOutletIndex)));

		
		if(status==1)
			return false;
		else if(status==2)
			return true;
		else throw new IOException();
		
	}

	
	public boolean powerOnPort(int targetOutletIndex) throws IOException{
		
		client.setIntFromString(1, new OID(rootOutletCommandOID+"."+targetOutletIndex));
		
		return true;
	}
	
	public boolean powerOffPort(int targetOutletIndex) throws IOException{
		
		client.setIntFromString(2, new OID(rootOutletCommandOID+"."+targetOutletIndex));
		
		return true;
	}
	
	
}
