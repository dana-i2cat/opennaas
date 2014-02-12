package org.opennaas.extensions.genericnetwork.model.topology;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class TopologyElementState {

	private boolean	congested	= false;

	/**
	 * @return the congested
	 */
	public boolean isCongested() {
		return congested;
	}

	/**
	 * @param congested
	 *            the congested to set
	 */
	public void setCongested(boolean congested) {
		this.congested = congested;
	}

}
