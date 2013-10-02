package org.opennaas.extensions.router.opener.client.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.opener.client.model.Interface;

@XmlRootElement(name = "getInterface", namespace = "http://www.craax.upc.edu/axis2/quagga_openapi")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInterfaceResponse {

	@XmlElement(name = "interface")
	private Interface	iface;

	public Interface getInterface() {
		return iface;
	}

	public void setInterface(Interface iface) {
		this.iface = iface;
	}

}
