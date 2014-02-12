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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (congested ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopologyElementState other = (TopologyElementState) obj;
		if (congested != other.congested)
			return false;
		return true;
	}

}
