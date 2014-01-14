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

import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

@Entity
public class Interface {

	@Id
	@GeneratedValue
	private long	id;

	@Basic
	private String	nameInterface;

	@Embedded
	private Link	linkTo;

	@Basic
	private String	capacity;

	@Embedded
	private LayerId	atLayer;

	@XmlElement(name = "atLayer", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public LayerId getAtLayer() {
		return atLayer;
	}

	public void setAtLayer(LayerId atLayer) {
		this.atLayer = atLayer;
	}

	@XmlElement(name = "name", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public String getName() {
		return nameInterface;
	}

	public void setName(String name) {
		this.nameInterface = name;
	}

	@XmlElement(name = "linkTo", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public Link getLinkTo() {
		return linkTo;
	}

	public void setLinkTo(Link linkTo) {
		this.linkTo = linkTo;
	}

	@XmlElement(name = "capacity", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "Interface [name=" + nameInterface + ", linkTo=" + linkTo + ", capacity="
				+ capacity + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
		result = prime * result + ((linkTo == null) ? 0 : linkTo.hashCode());
		result = prime * result + ((nameInterface == null) ? 0 : nameInterface.hashCode());
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
		Interface other = (Interface) obj;
		if (capacity == null) {
			if (other.capacity != null)
				return false;
		} else if (!capacity.equals(other.capacity))
			return false;
		if (linkTo == null) {
			if (other.linkTo != null)
				return false;
		} else if (!linkTo.equals(other.linkTo))
			return false;
		if (nameInterface == null) {
			if (other.nameInterface != null)
				return false;
		} else if (!nameInterface.equals(other.nameInterface))
			return false;
		return true;
	}
}
