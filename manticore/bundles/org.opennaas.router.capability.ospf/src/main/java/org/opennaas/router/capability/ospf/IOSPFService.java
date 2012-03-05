package org.opennaas.router.capability.ospf;

import java.util.List;

import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.OSPFService;

import org.opennaas.core.resources.capability.CapabilityException;

/**
 * @author Jordi Puig
 */
public interface IOSPFService {

	/**
	 * Enable OSPF on the interface.
	 * 
	 * @param lLogicalPort
	 * @return requestStatus
	 */
	public Object activateOSPF(List<LogicalPort> lLogicalPort);

	/**
	 * Disable OSPF on the interface.
	 * 
	 * @param lLogicalPort
	 * @return requestStatus
	 */
	public Object deactivateOSPF(List<LogicalPort> lLogicalPort);

	/**
	 * Configure a network interface in order to activate OSPF on it.
	 * 
	 * @param ospfService
	 * @return requestStatus
	 */
	public Object configureOSPF(OSPFService ospfService);

	/**
	 * Returns a list of all interfaces where the OSPF is configured and enabled (from model)
	 * 
	 * @return ospfService
	 * @throws CapabilityException
	 */
	public OSPFService showOSPFConfiguration() throws CapabilityException;

	/**
	 * Returns a list of all interfaces where the OSPF is configured and enabled (from router)
	 * 
	 * @return ospfService
	 */
	public OSPFService getOSPFConfiguration();

}
