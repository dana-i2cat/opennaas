package org.opennaas.extensions.genericnetwork.model.topology;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TopologyElement {

	private TopologyElementState	state;

	public TopologyElement() {
		state = new TopologyElementState();
	}

	/**
	 * @return the state
	 */
	public TopologyElementState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(TopologyElementState state) {
		this.state = state;
	}

}
