package org.opennaas.extensions.ws.services;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.router.model.LogicalPort;

/**
 * @author Jordi Puig
 */
@WebService(targetNamespace = "http://ws.opennaas.org", serviceName = "ChassisCapabilityService")
public interface IChassisCapabilityService {

	/**
	 * Activates given physical interface (iface) so it can receive/send traffic.
	 * 
	 * Note: This call uses the driver to communicate with the physical device this capability belongs to, and uses actions to modify the device
	 * state. This call end by adding required actions to the device queue, hence device state is not modified yet. An execution of this device queue
	 * is required for queued actions to take effect.
	 * 
	 * @param iface
	 *            to activate (must be a physical one)
	 * @throws CapabilityException
	 *             if any error occurred. In that case, queue remains untouched.
	 */
	@WebMethod(operationName = "upPhysicalInterface")
	@RequestWrapper(targetNamespace = "http://ws.opennaas.org/types", className = "org.opennaas.types.UpPhysicalInterfaceRequest")
	public void upPhysicalInterface(String resourceId, LogicalPort iface);

}