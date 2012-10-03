package org.opennaas.extensions.vcpe.descriptor.request;

import java.util.List;

import javax.xml.bind.annotation.XmlIDREF;

public class RequestDevice extends RequestElement {

	private String					name;

	private String					parentName;

	@XmlIDREF
	private List<RequestInterface>	interfaces;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public List<RequestInterface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<RequestInterface> interfaces) {
		this.interfaces = interfaces;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentName == null) ? 0 : parentName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestDevice other = (RequestDevice) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (interfaces == null) {
			if (other.interfaces != null)
				return false;
		} else if (!interfaces.equals(other.interfaces))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentName == null) {
			if (other.parentName != null)
				return false;
		} else if (!parentName.equals(other.parentName))
			return false;
		return true;
	}

}
