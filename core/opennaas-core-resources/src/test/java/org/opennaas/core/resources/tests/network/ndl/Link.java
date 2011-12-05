package org.opennaas.core.resources.tests.network.ndl;

import javax.xml.bind.annotation.XmlAttribute;

public class Link {
	private String name;

	@XmlAttribute(name = "resource", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Link [name=" + name + "]";
	}
	


}
