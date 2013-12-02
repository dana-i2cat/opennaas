package org.opennaas.extensions.pdu.capability;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

@Path("/")
public interface IPDUPowerMonitoringIDsCapability extends ICapability {

	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	@Path("/powermetrics/{portId}")
	@GET
	public MeasuredLoad getCurrentPowerMetrics(@PathParam("portId") String portId) throws Exception;

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	@Path("/powerlog/{portId}")
	@GET
	public PowerMonitorLog getPowerMetricsByTimeRange(@PathParam("portId") String portId, Date from, Date to) throws Exception;

}
