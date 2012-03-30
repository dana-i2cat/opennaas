package org.opennaas.core.resources.descriptor.network;

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
