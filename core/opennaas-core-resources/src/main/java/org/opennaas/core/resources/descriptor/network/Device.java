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

}
