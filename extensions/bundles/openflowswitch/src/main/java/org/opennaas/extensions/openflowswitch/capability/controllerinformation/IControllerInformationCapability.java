package org.opennaas.extensions.openflowswitch.capability.controllerinformation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.HealthState;
import org.opennaas.extensions.openflowswitch.capability.controllerinformation.model.MemoryUsage;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@Path("/")
public interface IControllerInformationCapability extends ICapability {

	@GET
	@Path("/memoryUsage")
	@Produces(MediaType.APPLICATION_XML)
	public MemoryUsage getControllerMemoryUsage() throws CapabilityException;

	@GET
	@Path("/healthState")
	@Produces(MediaType.APPLICATION_XML)
	public HealthState getHealthState() throws CapabilityException;

}
