package org.opennaas.extensions.vcpe.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VCPENetworkModel implements IModel {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -1793468268517626224L;

	private List<VCPENetworkElement>	elements;

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	public List<VCPENetworkElement> getElements() {
		return elements;
	}

	public void setElements(List<VCPENetworkElement> elements) {
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
		VCPENetworkModel other = (VCPENetworkModel) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

}
