package org.opennaas.extensions.genericnetwork.capability.nclmonitoring.portstatistics;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedPortStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedSwitchPortStatistics;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@Path("/")
public interface IPortStatisticsMonitoringCapability extends ICapability {
	
	
	/**
	 * Retrieves port statistics in given time period
	 * 
	 * @param period
	 * @return
	 */
	@GET
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public TimedSwitchPortStatistics getPortStatistics(TimePeriod period);
	
	/**
	 * Retrieves port statistics of given switch in given time period
	 * 
	 * @param period
	 * @param switchId
	 * @return
	 */
	@GET
	@Path("/{switchId}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public TimedPortStatistics getPortStatistics(TimePeriod period, @PathParam("switchId") String switchId);
	
	

}
