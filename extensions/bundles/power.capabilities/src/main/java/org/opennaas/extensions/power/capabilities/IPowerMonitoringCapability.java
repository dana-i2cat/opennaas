package org.opennaas.extensions.power.capabilities;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

@Path("/")
public interface IPowerMonitoringCapability extends ICapability {
	
	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	@Path("/powermetrics")
	@GET
	public MeasuredLoad getCurrentPowerMetrics() throws Exception;

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	@Path("/powerlog")
	@GET
	public PowerMonitorLog getPowerMetricsByTimeRange(Date from, Date to) throws Exception;

}
