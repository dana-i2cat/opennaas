package org.opennaas.extensions.router.capability.ospf.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;

@XmlRootElement(name = "ospfProtocolEndpoint")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFProtocolEndpointWrapper implements Serializable {

	private static final long	serialVersionUID	= 7971860759725452902L;

	private String				name;
	private EnabledState		enabledState;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EnabledState getEnabledState() {
		return enabledState;
	}

	public void setState(EnabledState state) {
		this.enabledState = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enabledState == null) ? 0 : enabledState.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OSPFProtocolEndpointWrapper other = (OSPFProtocolEndpointWrapper) obj;
		if (enabledState != other.enabledState)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
