package org.opennaas.extensions.pdu.capability;

import java.util.Date;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public interface IPDUPowerMonitoringIDsCapability extends ICapability {
	
	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	public MeasuredLoad getCurrentPowerMetrics(String portId) throws Exception;

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	public PowerMonitorLog getPowerMetricsByTimeRange(String portId, Date from, Date to) throws Exception;


}
