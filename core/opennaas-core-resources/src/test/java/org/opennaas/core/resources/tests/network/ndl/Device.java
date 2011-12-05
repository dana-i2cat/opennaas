package org.opennaas.core.resources.tests.network.ndl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;


public class Device {
	String name;
	List<InterfaceId> hasInterfaces;

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
