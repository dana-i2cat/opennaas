package org.opennaas.extensions.pdu.capability;

import java.util.ArrayList;
import java.util.Arrays;

import org.opennaas.extensions.gim.controller.APCPDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.PDUPowerController;
import org.opennaas.extensions.gim.controller.snmp.APCDriver_SNMP;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PDUDriverInstantiator {

	public static PDUPowerController create(String resourceId, PDU pdu, String pduIPAddress) throws Exception {

		APCDriver_SNMP snmpDriver;
		APCPDUPowerControllerDriver apcDriver;
		PDUPowerController pduController;

		snmpDriver = new APCDriver_SNMP(pduIPAddress);

		apcDriver = new APCPDUPowerControllerDriver();
		apcDriver.setDriver(snmpDriver);

		// FIXME ports should be read by the driver at capability startup.
		// retrieve ports from driver
		// List<PDUPort> ports = apcDriver.getPorts();
		// pdu.setPduPorts(ports);

		PDUPort port = new PDUPort();
		port.setId("1");
		port.setPdu(pdu);
		port.setPowerMonitorLog(new PowerMonitorLog(1, 10));
		pdu.setPduPorts(new ArrayList<PDUPort>(Arrays.asList(port)));

		// FIXME apcDriver should populate outletIndexes when reading ports
		apcDriver.getOutletIndexes().put(port.getId(), 1);

		pduController = new PDUPowerController();
		pduController.setPdu(pdu);
		pduController.setDriver(apcDriver);

		return pduController;
	}

}
