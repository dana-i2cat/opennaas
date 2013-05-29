package controller.capabilities;

import gim.core.entities.pdu.PDUPort;
import gim.load.MeasuredLoad;
import gim.log.PowerMonitorLog;

import java.util.Date;

public interface IPDUPowerMonitoringCapability {

	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	public MeasuredLoad getCurrentPowerMetrics(PDUPort port) throws Exception;

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	public PowerMonitorLog getPowerMetricsByTimeRange(PDUPort port, Date from, Date to) throws Exception;

}
