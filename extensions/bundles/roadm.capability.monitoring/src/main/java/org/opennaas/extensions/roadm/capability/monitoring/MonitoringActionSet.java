/**
 * 
 */
package org.opennaas.extensions.roadm.capability.monitoring;

import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * @author Jordi
 */
public class MonitoringActionSet implements IActionSetDefinition {

	public final static String	PROCESS_ALARM	= "processAlarm";
	public final static String	REGISTER		= "registerAsListener";
	public final static String	GET_ALARMS		= "listAlarms";
	public final static String	CLEAR_ALARMS	= "clearAlarms";

}
