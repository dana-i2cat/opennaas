package org.opennaas.extensions.router.opener.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class IPData {
	
	@XmlElement(name="address")
	private String address;
	
	@XmlElement(name="prefix-length")
	private String prefixLength;
	
	@XmlElement(name="familytype")
	private String familyType;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPrefixLength() {
		return prefixLength;
	}
	public void setPrefixLength(String prefixLength) {
		this.prefixLength = prefixLength;
	}
	public String getFamilyType() {
		return familyType;
	}
	public void setFamilyType(String familyType) {
		this.familyType = familyType;
	}

}
