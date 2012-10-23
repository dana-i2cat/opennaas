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
	private String						vcpeNetworkId;
	private String						vcpeNetworkName;
	private String						templateName;
	private List<VCPENetworkElement>	elements;

	private String						clientIpAddressRange;

	/**
	 * @return
	 */
	public List<VCPENetworkElement> getElements() {
		return elements;
	}

	/**
	 * @param elements
	 */
	public void setElements(List<VCPENetworkElement> elements) {
		this.elements = elements;
	}

	/**
	 * @return the vcpeNetworkId
	 */
	public String getVcpeNetworkId() {
		return vcpeNetworkId;
	}

	/**
	 * @param vcpeNetworkId
	 *            the vcpeNetworkId to set
	 */
	public void setVcpeNetworkId(String vcpeNetworkId) {
		this.vcpeNetworkId = vcpeNetworkId;
	}

	/**
	 * @return the vcpeNetworkName
	 */
	public String getVcpeNetworkName() {
		return vcpeNetworkName;
	}

	/**
	 * @param vcpeNetworkName
	 *            the vcpeNetworkName to set
	 */
	public void setVcpeNetworkName(String vcpeNetworkName) {
		this.vcpeNetworkName = vcpeNetworkName;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName
	 *            the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getClientIpAddressRange() {
		return clientIpAddressRange;
	}

	public void setClientIpAddressRange(String clientIpAddressRange) {
		this.clientIpAddressRange = clientIpAddressRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IModel#getChildren()
	 */
	@Override
	public List<String> getChildren() {
		return new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.core.resources.IModel#toXml()
	 */
	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientIpAddressRange == null) ? 0 : clientIpAddressRange.hashCode());
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
		result = prime * result + ((vcpeNetworkId == null) ? 0 : vcpeNetworkId.hashCode());
		result = prime * result + ((vcpeNetworkName == null) ? 0 : vcpeNetworkName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VCPENetworkModel other = (VCPENetworkModel) obj;
		if (clientIpAddressRange == null) {
			if (other.clientIpAddressRange != null)
				return false;
		} else if (!clientIpAddressRange.equals(other.clientIpAddressRange))
			return false;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		if (vcpeNetworkId == null) {
			if (other.vcpeNetworkId != null)
				return false;
		} else if (!vcpeNetworkId.equals(other.vcpeNetworkId))
			return false;
		if (vcpeNetworkName == null) {
			if (other.vcpeNetworkName != null)
				return false;
		} else if (!vcpeNetworkName.equals(other.vcpeNetworkName))
			return false;
		return true;
	}

}
