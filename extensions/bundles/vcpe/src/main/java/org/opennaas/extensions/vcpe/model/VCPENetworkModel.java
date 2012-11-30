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
	private boolean						created;
	private String						clientIpAddressRange;
	private BGP							bgp;
	private VRRP						vrrp;

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

	/**
	 * @return the clientIpAddressRange
	 */
	public String getClientIpAddressRange() {
		return clientIpAddressRange;
	}

	/**
	 * @param clientIpAddressRange
	 */
	public void setClientIpAddressRange(String clientIpAddressRange) {
		this.clientIpAddressRange = clientIpAddressRange;
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
	 * @param bgp
	 *            the bgp to set
	 */
	public void setBgp(BGP bgp) {
		this.bgp = bgp;
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
	 * @param vrrp
	 *            the vrrp to set
	 */
	public void setVrrp(VRRP vrrp) {
		this.vrrp = vrrp;
	}

}
