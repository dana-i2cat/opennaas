package controller.capabilities;

import gim.load.MeasuredLoad;
import gim.log.PowerMonitorLog;

import java.util.Date;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerMonitoringCapability {

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
