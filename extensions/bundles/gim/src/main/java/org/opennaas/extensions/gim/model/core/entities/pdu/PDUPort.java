package org.opennaas.extensions.gim.model.core.entities.pdu;

import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUPort extends PowerDelivery {

	private String			name;
	private PowerMonitorLog	powerMonitorLog;

	private PDU				pdu;

	public PDU getPdu() {
		return pdu;
	}

	public void setPdu(PDU pdu) {
		this.pdu = pdu;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return @code{IPowerSupply}s associated to the PDU
	 */
	public List<PowerSupply> getPowerSupplies() {
		return pdu.getPowerSupplies();
	}

	/**
	 * @return the powerMonitorLog
	 */
	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	/**
	 * @param powerMonitorLog
	 *            the powerMonitorLog to set
	 */
	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

}
