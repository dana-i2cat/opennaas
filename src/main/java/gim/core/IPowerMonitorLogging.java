package gim.core;

import gim.log.PowerMonitorLog;

/**
 * An interface to announce implementor holds a PowerMonitorLog.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerMonitorLogging {

	/**
	 * 
	 * @return this system's PowerMonitorLog
	 */
	public PowerMonitorLog getPowerMonitorLog();

}
