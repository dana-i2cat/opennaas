package org.opennaas.extensions.router.capabilities.api.model.staticroute;

/*
 * #%L
 * OpenNaaS :: Router :: Capabilities :: API
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class StaticRoute {

	private String	netIdIpAdress;
	private String	nextHopIpAddress;
	@XmlElement(name = "isDiscard")
	private boolean	discard;

	public String getNetIdIpAdress() {
		return netIdIpAdress;
	}

	public void setNetIdIpAdress(String netIdIpAdress) {
		this.netIdIpAdress = netIdIpAdress;
	}

	public String getNextHopIpAddress() {
		return nextHopIpAddress;
	}

	public void setNextHopIpAddress(String nextHopIpAddress) {
		this.nextHopIpAddress = nextHopIpAddress;
	}

	public boolean isDiscard() {
		return discard;
	}

	public void setDiscard(boolean discard) {
		this.discard = discard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (discard ? 1231 : 1237);
		result = prime * result + ((netIdIpAdress == null) ? 0 : netIdIpAdress.hashCode());
		result = prime * result + ((nextHopIpAddress == null) ? 0 : nextHopIpAddress.hashCode());
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
		StaticRoute other = (StaticRoute) obj;
		if (discard != other.discard)
			return false;
		if (netIdIpAdress == null) {
			if (other.netIdIpAdress != null)
				return false;
		} else if (!netIdIpAdress.equals(other.netIdIpAdress))
			return false;
		if (nextHopIpAddress == null) {
			if (other.nextHopIpAddress != null)
				return false;
		} else if (!nextHopIpAddress.equals(other.nextHopIpAddress))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StaticRoute [netIdIpAdress=" + netIdIpAdress + ", nextHopIpAddress=" + nextHopIpAddress + ", discard=" + discard + "]";
	}

}
