package org.opennaas.extensions.quantum.capability.extensions.l3.shell;

import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;

/**
 * Quantum Extension L3 Networking API<br />
 * Based on <a href="http://docs.openstack.org/api/openstack-network/2.0/content/router_ext.html">OpenStack L3 Networking Extension Documentation -
 * Router API Operations</a>
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IQuantumL3Capability extends ICapability {

}
