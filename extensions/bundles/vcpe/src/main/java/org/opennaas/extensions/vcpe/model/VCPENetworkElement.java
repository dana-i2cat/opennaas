package org.opennaas.extensions.vcpe.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ Domain.class, Interface.class, Link.class, Router.class })
public abstract class VCPENetworkElement {

	@XmlID
	protected String	nameInTemplate;

	protected String	name;

	public String getNameInTemplate() {
		return nameInTemplate;
	}

	public void setNameInTemplate(String nameInTemplate) {
		this.nameInTemplate = nameInTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nameInTemplate == null) ? 0 : nameInTemplate.hashCode());
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
		VCPENetworkElement other = (VCPENetworkElement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameInTemplate == null) {
			if (other.nameInTemplate != null)
				return false;
		} else if (!nameInTemplate.equals(other.nameInTemplate))
			return false;
		return true;
	}

}
