package org.opennaas.extensions.ws.services;

import java.util.List;

import javax.jws.WebService;

import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;

/**
 * @author Jordi Puig
 */

@WebService(portName = "L2BoDCapabilityPort", serviceName = "L2BoDCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public interface IL2BoDCapabilityService {

	/**
	 * @param resourceId
	 * @param interfaceName1
	 * @param interfaceName2
	 * @param vlanid
	 * @param capacity
	 * @param endTime
	 * @throws CapabilityException
	 * @throws ResourceException
	 */
	public void requestConnection(String resourceId, String interfaceName1, String interfaceName2, String vlanid, String capacity,
			String endTime) throws CapabilityException, ResourceException;

	/**
	 * @param resourceId
	 * @param listInterfaces
	 * @throws CapabilityException
	 * @throws ResourceException
	 */
	public void shutDownConnection(String resourceId, List<String> listInterfaces) throws CapabilityException, ResourceException;

}