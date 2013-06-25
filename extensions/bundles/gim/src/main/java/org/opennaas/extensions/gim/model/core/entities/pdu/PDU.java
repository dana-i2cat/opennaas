package org.opennaas.extensions.gim.model.core.entities.pdu;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;

/**
 * An IPowerDelivery implementation formed by a PDU with its ports.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDU extends PowerDelivery {

	private List<PDUPort>	pduPorts;
	private String			name;

	/**
	 * @return the pduPorts
	 */
	public List<PDUPort> getPduPorts() {
		return pduPorts;
	}

	/**
	 * @param pduPorts
	 *            the pduPorts to set
	 */
	public void setPduPorts(List<PDUPort> pduPorts) {
		this.pduPorts = pduPorts;
	}

	/**
	 * Return consumers associated to the PDU ports.
	 */
	public List<PowerConsumer> getPowerConsumers() {
		List<PowerConsumer> allConsumers = new ArrayList<PowerConsumer>();
		for (PDUPort port : pduPorts) {
			allConsumers.addAll(port.getPowerConsumers());
		}
		return allConsumers;
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

}
