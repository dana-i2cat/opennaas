package org.opennaas.extensions.genericnetwork.model.circuit;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class QoSPolicy implements Serializable {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long	serialVersionUID	= 7226728765828544543L;

	private int					minLatency;
	private int					maxLatency;

	private int					minJitter;
	private int					maxJitter;

	private int					minThroughput;
	private int					maxThroughput;

	private int					minPacketLoss;
	private int					maxPacketLoss;

	/**
	 * @return the minLantency
	 */
	public int getMinLatency() {
		return minLatency;
	}

	/**
	 * @param minLantency
	 *            the minLantency to set
	 */
	public void setMinLatency(int minLantency) {
		this.minLatency = minLantency;
	}

	/**
	 * @return the maxLatency
	 */
	public int getMaxLatency() {
		return maxLatency;
	}

	/**
	 * @param maxLatency
	 *            the maxLatency to set
	 */
	public void setMaxLatency(int maxLatency) {
		this.maxLatency = maxLatency;
	}

	/**
	 * @return the minJitter
	 */
	public int getMinJitter() {
		return minJitter;
	}

	/**
	 * @param minJitter
	 *            the minJitter to set
	 */
	public void setMinJitter(int minJitter) {
		this.minJitter = minJitter;
	}

	/**
	 * @return the maxJitter
	 */
	public int getMaxJitter() {
		return maxJitter;
	}

	/**
	 * @param maxJitter
	 *            the maxJitter to set
	 */
	public void setMaxJitter(int maxJitter) {
		this.maxJitter = maxJitter;
	}

	/**
	 * @return the minThroughput
	 */
	public int getMinThroughput() {
		return minThroughput;
	}

	/**
	 * @param minThroughput
	 *            the minThroughput to set
	 */
	public void setMinThroughput(int minThroughput) {
		this.minThroughput = minThroughput;
	}

	/**
	 * @return the maxThroughput
	 */
	public int getMaxThroughput() {
		return maxThroughput;
	}

	/**
	 * @param maxThroughput
	 *            the maxThroughput to set
	 */
	public void setMaxThroughput(int maxThroughput) {
		this.maxThroughput = maxThroughput;
	}

	/**
	 * @return the minPacketLoss
	 */
	public int getMinPacketLoss() {
		return minPacketLoss;
	}

	/**
	 * @param minPacketLoss
	 *            the minPacketLoss to set
	 */
	public void setMinPacketLoss(int minPacketLoss) {
		this.minPacketLoss = minPacketLoss;
	}

	/**
	 * @return the maxPaquetLoss
	 */
	public int getMaxPacketLoss() {
		return maxPacketLoss;
	}

	/**
	 * @param maxPaquetLoss
	 *            the maxPaquetLoss to set
	 */
	public void setMaxPacketLoss(int maxPacketLoss) {
		this.maxPacketLoss = maxPacketLoss;
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
		result = prime * result + maxJitter;
		result = prime * result + maxLatency;
		result = prime * result + maxPacketLoss;
		result = prime * result + maxThroughput;
		result = prime * result + minJitter;
		result = prime * result + minLatency;
		result = prime * result + minPacketLoss;
		result = prime * result + minThroughput;
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
		QoSPolicy other = (QoSPolicy) obj;
		if (maxJitter != other.maxJitter)
			return false;
		if (maxLatency != other.maxLatency)
			return false;
		if (maxPacketLoss != other.maxPacketLoss)
			return false;
		if (maxThroughput != other.maxThroughput)
			return false;
		if (minJitter != other.minJitter)
			return false;
		if (minLatency != other.minLatency)
			return false;
		if (minPacketLoss != other.minPacketLoss)
			return false;
		if (minThroughput != other.minThroughput)
			return false;
		return true;
	}

}
