package org.opennaas.extensions.vcpe.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 * This class represents a VCPENetwork model. <br/>
 * It is the root of the model and the only class in it annotated with as XmlRootElement. Hence, it is the only class where xml marshaling and
 * unmarshaling should be applied to. This is due to using id and idref in VCPENetworkElements to marshal into xml.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VCPENetworkModel implements IModel {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -1793468268517626224L;
	private String						id;
	private String						name;
	private String						templateType;
	private String						clientIpRange;
	private String						nocIpRange;
	private BGP							bgp;
	private VRRP						vrrp;
	private boolean						created;
	private List<VCPENetworkElement>	elements			= new ArrayList<VCPENetworkElement>();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the clientIpRange
	 */
	public String getClientIpRange() {
		return clientIpRange;
	}

	/**
	 * @param clientIpRange the clientIpRange to set
	 */
	public void setClientIpRange(String clientIpRange) {
		this.clientIpRange = clientIpRange;
	}

	/**
	 * @return the nocIpRange
	 */
	public String getNocIpRange() {
		return nocIpRange;
	}

	/**
	 * @param nocIpRange the nocIpRange to set
	 */
	public void setNocIpRange(String nocIpRange) {
		this.nocIpRange = nocIpRange;
	}

	/**
	 * @return
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * @param created
	 */
	public void setCreated(boolean created) {
		this.created = created;
	}

	/**
	 * @return the bgp
	 */
	public BGP getBgp() {
		return bgp;
	}

	/**
	 * @param bgp the bgp to set
	 */
	public void setBgp(BGP bgp) {
		this.bgp = bgp;
	}

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

	/**
	 * @return the vrrp
	 */
	public VRRP getVrrp() {
		return vrrp;
	}

	/**
	 * @param vrrp the vrrp to set
	 */
	public void setVrrp(VRRP vrrp) {
		this.vrrp = vrrp;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
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
		result = prime * result + ((bgp == null) ? 0 : bgp.hashCode());
		result = prime * result + ((clientIpRange == null) ? 0 : clientIpRange.hashCode());
		result = prime * result + (created ? 1231 : 1237);
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((templateType == null) ? 0 : templateType.hashCode());
		result = prime * result + ((vrrp == null) ? 0 : vrrp.hashCode());
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
		if (bgp == null) {
			if (other.bgp != null)
				return false;
		} else if (!bgp.equals(other.bgp))
			return false;
		if (clientIpRange == null) {
			if (other.clientIpRange != null)
				return false;
		} else if (!clientIpRange.equals(other.clientIpRange))
			return false;
		if (created != other.created)
			return false;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (templateType == null) {
			if (other.templateType != null)
				return false;
		} else if (!templateType.equals(other.templateType))
			return false;
		if (vrrp == null) {
			if (other.vrrp != null)
				return false;
		} else if (!vrrp.equals(other.vrrp))
			return false;
		return true;
	}

}
