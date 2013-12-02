package org.opennaas.extensions.router.opener.client.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.opener.client.model.InterfaceID;

@XmlRootElement(namespace = "http://www.craax.upc.edu/axis2/quagga_openapi")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddInterfaceRequest {

	@XmlElement(name = "interface")
	private InterfaceID	id;

	public InterfaceID getId() {
		return id;
	}

	public void setId(InterfaceID id) {
		this.id = id;
	}

}
