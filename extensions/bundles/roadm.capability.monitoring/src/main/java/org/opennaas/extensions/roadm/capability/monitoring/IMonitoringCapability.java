package org.opennaas.extensions.roadm.capability.monitoring;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

@Path("/")
public interface IMonitoringCapability extends ICapability {

	/**
	 * Clear the alarms of the resource
	 * 
	 * @throws CapabilityException
	 */
	@Path("/clearAlarms")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public void clearAlarms() throws CapabilityException;

	/**
	 * Get the alarms of the resource
	 * 
	 * @throws CapabilityException
	 */
	// TODO problem with alarm class. Non-argument constructor.
	public List<ResourceAlarm> getAlarms() throws CapabilityException;
}
