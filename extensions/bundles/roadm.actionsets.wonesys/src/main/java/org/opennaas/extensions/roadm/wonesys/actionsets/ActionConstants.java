package org.opennaas.extensions.roadm.wonesys.actionsets;

import org.opennaas.extensions.roadm.capability.connections.ConnectionsActionSet;
import org.opennaas.extensions.roadm.capability.monitoring.MonitoringActionSet;

public class ActionConstants {
	public final static String	MAKECONNECTION		= ConnectionsActionSet.MAKE_CONNECTION;
	public final static String	REMOVECONNECTION	= ConnectionsActionSet.REMOVE_CONNECTION;
	public final static String	REFRESHCONNECTIONS	= "refreshModelConnections";
	public final static String	GETINVENTORY		= "getInventory";
	public final static String	LOCKNODE			= "lockNode";
	public final static String	UNLOCKNODE			= "unlockNode";

	public final static String	PROCESSALARM		= MonitoringActionSet.PROCESS_ALARM;
	public final static String	REGISTER			= MonitoringActionSet.REGISTER;
	public final static String	GET_ALARMS			= MonitoringActionSet.GET_ALARMS;
	public final static String	CLEAR_ALARMS		= MonitoringActionSet.CLEAR_ALARMS;

}
