package org.opennaas.extensions.roadm.wonesys.actionsets;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys Actionset
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
