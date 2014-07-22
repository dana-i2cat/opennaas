package org.opennaas.extensions.gim.model.core.entities.sockets;

/*
 * #%L
 * GIM :: GIModel and APC PDU driver
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

import org.opennaas.extensions.gim.model.load.RatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PowerSocket {

	private String			id;
	private String			elementId;
	private RatedLoad		ratedLoad;
	private boolean			powerState;
	private PowerMonitorLog	powerMonitorLog;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the id of the element this socket is hosted in
	 * 
	 * @return the elementId
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * @param elementId
	 *            the elementId to set
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public RatedLoad getRatedLoad() {
		return ratedLoad;
	}

	public void setRatedLoad(RatedLoad ratedLoad) {
		this.ratedLoad = ratedLoad;
	}

	public boolean getPowerState() {
		return powerState;
	}

	public void setPowerState(boolean powerState) {
		this.powerState = powerState;
	}

	/**
	 * @return the monitoringLog
	 */
	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	/**
	 * @param monitoringLog
	 *            the monitoringLog to set
	 */
	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

}
