package org.opennaas.extensions.vcpe.descriptor.request;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VCPENetworkRequest {

	private List<RequestElement>	elements;

	@XmlElement(name = "requestElement")
	public List<RequestElement> getElements() {
		return elements;
	}

	public void setElements(List<RequestElement> elements) {
		this.elements = elements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		VCPENetworkRequest other = (VCPENetworkRequest) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

}
