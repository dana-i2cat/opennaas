package org.opennaas.extensions.ofnetwork.capability.monitoring;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.ofnetwork.model.NetworkStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@Path("/")
public interface IMonitoringNetworkCapability extends ICapability {

	@GET
	@Path("readNetworkStatistics")
	@Produces(MediaType.APPLICATION_XML)
	public NetworkStatistics getNetworkStatistics() throws CapabilityException;

}
