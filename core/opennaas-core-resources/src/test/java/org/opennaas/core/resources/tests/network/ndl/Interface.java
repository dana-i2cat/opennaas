package org.opennaas.core.resources.tests.network.ndl;

import javax.xml.bind.annotation.XmlElement;



public class Interface {
	private String name;
	private Link linkTo;
	private String 	capacity;
	
	
	@XmlElement(name = "name", namespace = "http://www.science.uva.nl/research/sne/ndl#")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		return "Interface [name=" + name + ", linkTo=" + linkTo + ", capacity="
				+ capacity + "]";
	}
	
}
