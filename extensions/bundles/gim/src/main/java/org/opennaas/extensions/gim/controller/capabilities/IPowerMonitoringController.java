package org.opennaas.extensions.gim.controller.capabilities;



import java.util.Date;

import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;


/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerMonitoringController {

	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	public MeasuredLoad getCurrentPowerMetrics() throws Exception;

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	public PowerMonitorLog getPowerMetricsByTimeRange(Date from, Date to) throws Exception;

}
