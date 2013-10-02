package org.opennaas.extensions.router.opener.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class InterfaceID {

	@XmlElement(name = "name")
	private String	name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
