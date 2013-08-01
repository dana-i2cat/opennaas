package org.opennaas.extensions.router.opener.client.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class IPDataRequest {
	
	@XmlElement(name="address")
	private String address;
	
	@XmlElement(name="prefixlen")
	private String prefixLength;
	
	
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

}
