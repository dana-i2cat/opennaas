package org.opennaas.extensions.pdu.capability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.extensions.gim.controller.APCPDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.PDUPowerController;
import org.opennaas.extensions.gim.controller.PDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.snmp.APCDriver_SNMP;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;

public class PDUDriverInstantiator {
	
	public static PDUPowerController create(PDU pdu, String pduIPAddress) throws Exception {
		
		APCDriver_SNMP				snmpDriver;
		APCPDUPowerControllerDriver	apcDriver;
		PDUPowerController			pduController;
		
		snmpDriver = new APCDriver_SNMP(pduIPAddress);

		apcDriver = new APCPDUPowerControllerDriver();
		apcDriver.setDriver(snmpDriver);
		
		// FIXME retrieve ports from driver
		// List<PDUPort> ports = apcDriver.getPorts();
		List<PDUPort> ports = new ArrayList<PDUPort>(Arrays.asList(new PDUPort()));
		pdu.setPduPorts(ports);
		
		// FIXME apcDriver should populate outletIndexes when reading ports
		apcDriver.getOutletIndexes().put(ports.get(0), 1);

		pduController = new PDUPowerController();
		pduController.setPdu(pdu);
		pduController.setDriver(apcDriver);
		
		return pduController;
	}
	
	

}
