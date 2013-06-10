package org.opennaas.extensions.gim.controller.capabilities;

import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;

public interface IPDUInventaryCapability {

	public List<PDUPort> listPorts() throws Exception;

}
