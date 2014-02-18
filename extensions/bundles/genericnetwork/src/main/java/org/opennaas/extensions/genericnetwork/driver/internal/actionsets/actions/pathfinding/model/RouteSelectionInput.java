package org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteSelectionInput {

	private String	srcIP;
	private String	dstIP;
	private String	tos;
	private String	srcPort;
	private String	dstPort;

	/**
	 * Default constructor. ONLY FOR JAXB. Not to be used manually.
	 */
	public RouteSelectionInput() {

	}

	public RouteSelectionInput(String srcIP, String dstIP, String tos) {
		super();
		this.srcIP = srcIP;
		this.dstIP = dstIP;
		this.tos = tos;
	}

	public RouteSelectionInput(String srcIP, String dstIP, String tos, String srcPort, String dstPort) {
		super();
		this.srcIP = srcIP;
		this.dstIP = dstIP;
		this.tos = tos;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
	}

	public String getSrcIP() {
		return srcIP;
	}

	public void setSrcIP(String srcIP) {
		this.srcIP = srcIP;
	}

	public String getDstIP() {
		return dstIP;
	}

	public void setDstIP(String dstIP) {
		this.dstIP = dstIP;
	}

	public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}

	public String getDstPort() {
		return dstPort;
	}

	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dstIP == null) ? 0 : dstIP.hashCode());
		result = prime * result + ((dstPort == null) ? 0 : dstPort.hashCode());
		result = prime * result + ((srcIP == null) ? 0 : srcIP.hashCode());
		result = prime * result + ((srcPort == null) ? 0 : srcPort.hashCode());
		result = prime * result + ((tos == null) ? 0 : tos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteSelectionInput other = (RouteSelectionInput) obj;
		if (dstIP == null) {
			if (other.dstIP != null)
				return false;
		} else if (!dstIP.equals(other.dstIP))
			return false;
		if (dstPort == null) {
			if (other.dstPort != null)
				return false;
		} else if (!dstPort.equals(other.dstPort))
			return false;
		if (srcIP == null) {
			if (other.srcIP != null)
				return false;
		} else if (!srcIP.equals(other.srcIP))
			return false;
		if (srcPort == null) {
			if (other.srcPort != null)
				return false;
		} else if (!srcPort.equals(other.srcPort))
			return false;
		if (tos == null) {
			if (other.tos != null)
				return false;
		} else if (!tos.equals(other.tos))
			return false;
		return true;
	}
}
