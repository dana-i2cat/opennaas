package org.opennaas.network.capability.ospf;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.command.Response;

public interface INetOSPFService {

	/**
	 * Enable OSPF on the network.
	 * 
	 * @return
	 * @throws CapabilityException
	 */
	public Response activateOSPF() throws CapabilityException;

}