package org.opennaas.core.resources.descriptor.network;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAttribute;

@Embeddable
public class Link {
	@Basic
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
