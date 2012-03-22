package org.opennaas.core.resources.descriptor.network;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

@Entity
public class Device {

	@Id
	@GeneratedValue
	private long		id;

	@Basic
	String				name;

	@ElementCollection
	List<InterfaceId>	hasInterfaces;

	@XmlElement(name = "name", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "hasInterface", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public List<InterfaceId> getHasInterfaces() {
		return hasInterfaces;
	}

	public void setHasInterfaces(List<InterfaceId> hasInterfaces) {
		this.hasInterfaces = hasInterfaces;
	}

	@Override
	public String toString() {
		return "Device [name=" + name + ", hasInterfaces=" + hasInterfaces
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hasInterfaces == null) ? 0 : hasInterfaces.hashCode());
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
		Device other = (Device) obj;
		if (hasInterfaces == null) {
			if (other.hasInterfaces != null)
				return false;
		} else if (!hasInterfaces.equals(other.hasInterfaces))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
