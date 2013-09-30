package org.opennaas.extensions.ofertie.ncl.provisioner.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Contains QoS network requirements for a flow.
 * 
 * TODO define units for each attribute,
 * try to make them stay in an int if possible 
 * (avoid floating point when possible).
 * 
 * @author Isart Canyameres Gimenez (i2cat) 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class QoSRequirements {
	
	private int minDelay;
	private int maxDelay;
	
	private int minJitter;
	private int maxJitter;
	
	private int minBandwidth;
	private int maxBandwidth;
	
	private int minPacketLoss;
	private int maxPacketLoss;
	
	public int getMinDelay() {
		return minDelay;
	}
	
	public void setMinDelay(int minDelay) {
		this.minDelay = minDelay;
	}
	
	public int getMaxDelay() {
		return maxDelay;
	}
	
	public void setMaxDelay(int maxDelay) {
		this.maxDelay = maxDelay;
	}
	
	public int getMinJitter() {
		return minJitter;
	}
	
	public void setMinJitter(int minJitter) {
		this.minJitter = minJitter;
	}
	
	public int getMaxJitter() {
		return maxJitter;
	}
	
	public void setMaxJitter(int maxJitter) {
		this.maxJitter = maxJitter;
	}
	public int getMinBandwidth() {
		return minBandwidth;
	}
	public void setMinBandwidth(int minBandwidth) {
		this.minBandwidth = minBandwidth;
	}
	
	public int getMaxBandwidth() {
		return maxBandwidth;
	}
	
	public void setMaxBandwidth(int maxBandwidth) {
		this.maxBandwidth = maxBandwidth;
	}
	
	public int getMinPacketLoss() {
		return minPacketLoss;
	}
	
	public void setMinPacketLoss(int minPacketLoss) {
		this.minPacketLoss = minPacketLoss;
	}
	
	public int getMaxPacketLoss() {
		return maxPacketLoss;
	}
	
	public void setMaxPacketLoss(int maxPacketLoss) {
		this.maxPacketLoss = maxPacketLoss;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxBandwidth;
		result = prime * result + maxDelay;
		result = prime * result + maxJitter;
		result = prime * result + maxPacketLoss;
		result = prime * result + minBandwidth;
		result = prime * result + minDelay;
		result = prime * result + minJitter;
		result = prime * result + minPacketLoss;
		return result;
	}

	/* (non-Javadoc)
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
		QoSRequirements other = (QoSRequirements) obj;
		if (maxBandwidth != other.maxBandwidth)
			return false;
		if (maxDelay != other.maxDelay)
			return false;
		if (maxJitter != other.maxJitter)
			return false;
		if (maxPacketLoss != other.maxPacketLoss)
			return false;
		if (minBandwidth != other.minBandwidth)
			return false;
		if (minDelay != other.minDelay)
			return false;
		if (minJitter != other.minJitter)
			return false;
		if (minPacketLoss != other.minPacketLoss)
			return false;
		return true;
	}

}
