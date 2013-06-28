package org.opennaas.extensions.gim.model.core;

import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

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
