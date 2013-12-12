package org.opennaas.extensions.openflowswitch.capability.monitoring;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

/**
 * Monitoring Capability
 * 
 * @author Julio Carlos Barrera
 * 
 */
@Path("/")
public interface IMonitoringCapability extends ICapability {

	/**
	 * Get switch statistics
	 * 
	 * @return a {@link Map} with port ID as key and {@link PortStatistics} as value.
	 * @throws CapabilityException
	 */
	@GET
	@Path("/readPortStatistics")
	@Produces(MediaType.APPLICATION_XML)
	public Map<Integer, PortStatistics> getPortStatistics() throws CapabilityException;

}
