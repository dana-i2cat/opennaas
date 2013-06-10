package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;

public class GIMController {

	public static PDUPort getPortById(PDU pdu, String portId) throws Exception {
		if (portId == null)
			throw new Exception("Could not find PDUport " + portId);

		for (PDUPort port : pdu.getPduPorts()) {
			if (portId.equals(port.getId())) {
				return port;
			}
		}
		throw new Exception("Could not find PDUport " + portId);
	}

}
