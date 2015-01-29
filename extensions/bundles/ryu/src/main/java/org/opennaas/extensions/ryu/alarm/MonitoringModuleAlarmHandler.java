package org.opennaas.extensions.ryu.alarm;

/*
 * #%L
 * OpenNaaS :: Ryu Resource
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.extensions.ryu.client.monitoringmodule.IMonitoringModuleCallbackAPI;

public class MonitoringModuleAlarmHandler implements IMonitoringModuleCallbackAPI, IMonitoringModuleAlarmHandler {

	Log												log	= LogFactory.getLog(MonitoringModuleAlarmHandler.class);

	private Map<AlarmIdentifier, IAlarmObserver>	observers;

	public MonitoringModuleAlarmHandler() {
		observers = new ConcurrentHashMap<AlarmIdentifier, IAlarmObserver>();
	}

	@Override
	public void alarmReceived(String dpid, String portNumber) {
		log.info("Received alarm : [dpid=" + dpid + ", port=" + portNumber + "]");

		if (StringUtils.isEmpty(dpid) || portNumber == null)
			throw new NullPointerException("Dpid and port are not nullable parameters.");

		if (!StringUtils.isNumeric(portNumber))
			throw new IllegalArgumentException("Port should be numeric.");

		AlarmIdentifier alarmIdentifier = new AlarmIdentifier(dpid, new Integer(portNumber));

		if (!observers.containsKey(alarmIdentifier))
			throw new IllegalStateException("There's no registered observer for alarms with filter [dpid=" + dpid + ",port=" + portNumber + "]");

		observers.get(alarmIdentifier).alarmReceived();

	}

	@Override
	public void addAlarmObserver(String dpid, Integer port, IAlarmObserver alarmObserver) {

		log.info("Registering alarm observer with filter [dpid=" + dpid + ", port=" + port + "]");

		if (StringUtils.isEmpty(dpid) || port == null || alarmObserver == null)
			throw new NullPointerException("Dpid, port and alamObserver are not nullable parameters.");

		AlarmIdentifier alarmIdentifier = new AlarmIdentifier(dpid, port);

		if (observers.containsKey(alarmIdentifier))
			throw new IllegalStateException("There already exists a registered alarm for dpid=" + dpid + " and port=" + port);

		observers.put(alarmIdentifier, alarmObserver);

		log.info("Registered alarm observer with filter [dpid=" + dpid + ", port=" + port + "]");

	}

	@Override
	public void removeAlarmObserver(String dpid, Integer port) {
		log.info("Registering alarm observer with filter [dpid=" + dpid + ", port=" + port + "]");

		if (StringUtils.isEmpty(dpid) || port == null)
			throw new NullPointerException("Dpid and port are not nullable parameters.");

		AlarmIdentifier alarmIdentifier = new AlarmIdentifier(dpid, port);

		observers.remove(alarmIdentifier);

		log.info("Registered alarm observer with filter [dpid=" + dpid + ", port=" + port + "]");

	}

}
