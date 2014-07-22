package org.opennaas.core.resources.descriptor.network;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

@Entity
public class NetworkDomain {

	@Id
	@GeneratedValue
	private long	id;

	@Basic
	String			name;

	@ElementCollection
	List<DeviceId>	hasDevices;

	@XmlElement(name = "name", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "hasDevice", namespace = "http://www.science.uva.nl/research/sne/ndl/domain#")
	public List<DeviceId> getHasDevices() {
		return hasDevices;
	}

	public void setHasDevices(List<DeviceId> hasDevices) {
		this.hasDevices = hasDevices;
	}

	@Override
	public String toString() {
		return "Device [name=" + name + ", hasDevices=" + hasDevices
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hasDevices == null) ? 0 : hasDevices.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		NetworkDomain other = (NetworkDomain) obj;
		if (hasDevices == null) {
			if (other.hasDevices != null)
				return false;
		} else if (!hasDevices.equals(other.hasDevices))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
