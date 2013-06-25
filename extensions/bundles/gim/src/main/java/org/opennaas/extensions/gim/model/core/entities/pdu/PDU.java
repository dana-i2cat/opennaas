package org.opennaas.extensions.gim.model.core.entities.pdu;

import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;

/**
 * An IPowerDelivery implementation formed by a PDU with its ports.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDU extends PowerDelivery {

	private String	name;

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
