package org.opennaas.extensions.router.capability.ospf.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ospfProtocolEndpoint")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFProtocolEndpointWrapper implements Serializable {

	private static final long	serialVersionUID	= 7971860759725452902L;

	private String				name;
	private boolean				enabledState;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabledState;
	}

	public void setState(boolean state) {
		this.enabledState = state;
	}

}
