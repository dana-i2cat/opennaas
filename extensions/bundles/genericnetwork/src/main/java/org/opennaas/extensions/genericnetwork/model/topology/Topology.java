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

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class Topology {

	@XmlElementWrapper(name = "networkElements")
	@XmlElement(name = "networkElement")
	private Set<NetworkElement>			networkElements;

	@XmlElementWrapper(name = "links")
	@XmlElement(name = "link")
	private Set<Link>					links;

	/**
	 * Bidirectional Map storing the relation between network topology port IDs and per-device port IDs.<br>
	 * Using Guava's {@link BiMap}, more info <a href="https://code.google.com/p/guava-libraries/wiki/NewCollectionTypesExplained#BiMap">here</a>.
	 */

	private BiMap<String, DevicePortId>	networkDevicePortIdsMap;

	public Topology() {
		networkDevicePortIdsMap = HashBiMap.create();
		links = new HashSet<Link>();
		networkElements = new HashSet<NetworkElement>();
	}

	/**
	 * @return the networkElements
	 */
	public Set<NetworkElement> getNetworkElements() {
		return networkElements;
	}

	/**
	 * @param networkElements
	 *            the networkElements to set
	 */
	public void setNetworkElements(Set<NetworkElement> networkElements) {
		this.networkElements = networkElements;
	}

	/**
	 * @return the links
	 */
	public Set<Link> getLinks() {
		return links;
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(Set<Link> links) {
		this.links = links;
	}

	public BiMap<String, DevicePortId> getNetworkDevicePortIdsMap() {
		return networkDevicePortIdsMap;
	}

	public void setNetworkDevicePortIdsMap(BiMap<String, DevicePortId> networkDevicePortIdsMap) {
		this.networkDevicePortIdsMap = networkDevicePortIdsMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + ((networkDevicePortIdsMap == null) ? 0 : networkDevicePortIdsMap.hashCode());
		result = prime * result + ((networkElements == null) ? 0 : networkElements.hashCode());
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
		Topology other = (Topology) obj;
		if (links == null) {
			if (other.links != null)
				return false;
		} else if (!links.equals(other.links))
			return false;
		if (networkDevicePortIdsMap == null) {
			if (other.networkDevicePortIdsMap != null)
				return false;
		} else if (!networkDevicePortIdsMap.equals(other.networkDevicePortIdsMap))
			return false;
		if (networkElements == null) {
			if (other.networkElements != null)
				return false;
		} else if (!networkElements.equals(other.networkElements))
			return false;
		return true;
	}

}
