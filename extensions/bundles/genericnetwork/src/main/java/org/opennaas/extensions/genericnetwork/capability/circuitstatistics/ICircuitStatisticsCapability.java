package org.opennaas.extensions.genericnetwork.capability.circuitstatistics;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.genericnetwork.model.TimePeriod;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@Path("/")
public interface ICircuitStatisticsCapability extends ICapability {

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public void reportStatistics(String csvStatistics);

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getStatistics(TimePeriod timePeriod);

}
