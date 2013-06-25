package org.opennaas.extensions.gim.model.core.entities.sockets;

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
