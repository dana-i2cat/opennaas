package org.opennaas.extensions.router.opener.client.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.opener.client.model.IPData;


@XmlRootElement(namespace="http://www.craax.upc.edu/axis2/quagga_openapi")
@XmlAccessorType(XmlAccessType.FIELD)
public class IPRequest {
	
	@XmlElement(name="name")
	private String name;
	
	@XmlElement(name="ip")
	private IPData ip;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IPData getIp() {
		return ip;
	}

	public void setIp(IPData ip) {
		this.ip = ip;
	}

}
