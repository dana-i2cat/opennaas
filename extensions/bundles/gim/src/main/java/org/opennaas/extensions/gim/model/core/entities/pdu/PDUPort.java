package org.opennaas.extensions.gim.model.core.entities.pdu;

import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUPort extends PowerSource {

	private String	name;
	private int		outletIndex;

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
	 * @return the outletIndex
	 */
	public int getOutletIndex() {
		return outletIndex;
	}

	/**
	 * @param outletIndex
	 *            the outletIndex to set
	 */
	public void setOutletIndex(int outletIndex) {
		this.outletIndex = outletIndex;
	}

}
