package org.opennaas.router.capability.ospf;

import net.i2cat.mantychore.model.OSPFService;

/**
 * @author Jordi Puig
 */
public interface IOSPFService {

	/**
	 * Enable OSPF on the interface.
	 */
	public Object activateOSPF(Object params);

	/**
	 * Disable OSPF on the interface.
	 */
	public Object deactivateOSPF(Object params);

	/**
	 * Configure a network interface in order to activate OSPF on it.
	 */
	public Object configureOSPF(Object params);

	/**
	 * Returns a list of all interfaces where the OSPF is configured and enabled (from model)
	 */
	public OSPFService showOSPFConfiguration();

	/**
	 * Returns a list of all interfaces where the OSPF is configured and enabled (from router)
	 * 
	 * @param routerId
	 * @return OSPF configuration
	 */
	public Object getOSPFConfiguration(OSPFService ospfService);

}
