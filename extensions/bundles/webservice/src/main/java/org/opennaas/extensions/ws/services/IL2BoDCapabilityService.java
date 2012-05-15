package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.network.model.topology.Interface;

/**
 * @author Jordi Puig
 */

@WebService(portName = "L2BoDCapabilityPort", serviceName = "L2BoDCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IL2BoDCapabilityService {

	/**
	 * @throws CapabilityException
	 * @throws ResourceException
	 */
	public void requestConnection(String resourceId, String interfaceName1, String interfaceName2, String vlanid, String capacity,
			String endTime) throws CapabilityException, ResourceException;

	/**
	 * @throws CapabilityException
	 */
	public void shutDownConnection(String resourceId, List<Interface> listInterfaces) throws CapabilityException;

}