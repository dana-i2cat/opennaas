package org.opennaas.extensions.genericnetwork.model.topology;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Link extends TopologyElement {

	@XmlIDREF
	private Port	srcPort;

	@XmlIDREF
	private Port	dstPort;

	/**
	 * @return the srcPort
	 */
	public Port getSrcPort() {
		return srcPort;
	}

	/**
	 * @param srcPort
	 *            the srcPort to set
	 */
	public void setSrcPort(Port srcPort) {
		this.srcPort = srcPort;
	}

	/**
	 * @return the dstPort
	 */
	public Port getDstPort() {
		return dstPort;
	}

	/**
	 * @param dstPort
	 *            the dstPort to set
	 */
	public void setDstPort(Port dstPort) {
		this.dstPort = dstPort;
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
		result = prime * result + ((dstPort == null) ? 0 : dstPort.hashCode());
		result = prime * result + ((srcPort == null) ? 0 : srcPort.hashCode());
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
		Link other = (Link) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (dstPort == null) {
			if (other.dstPort != null)
				return false;
		} else if (!dstPort.equals(other.dstPort))
			return false;
		if (srcPort == null) {
			if (other.srcPort != null)
				return false;
		} else if (!srcPort.equals(other.srcPort))
			return false;
		return true;
	}

}
