package org.opennaas.extensions.router.opener.client.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "deleteInterfaceIP", namespace = "http://www.craax.upc.edu/axis2/quagga_openapi")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeleteInterfaceIPRequest {

	@XmlElement(name = "interface")
	private IPRequest	iface;

	public IPRequest getIface() {
		return iface;
	}

	public void setIface(IPRequest iface) {
		this.iface = iface;
	}

}