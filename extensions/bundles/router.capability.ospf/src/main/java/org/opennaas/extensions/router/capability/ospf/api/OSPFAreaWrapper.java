package org.opennaas.extensions.router.capability.ospf.api;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ospfArea")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFAreaWrapper implements Serializable {

	private static final long						serialVersionUID	= -4577590599939054176L;

	private String									name;

	@XmlElement(name = "ospfProtocolEndpoint")
	private Collection<OSPFProtocolEndpointWrapper>	ospfProtocolEndpoints;

	public Collection<OSPFProtocolEndpointWrapper> getOspfProtocolEndpoints() {
		return ospfProtocolEndpoints;
	}

	public void setOspfProtocolEndpoints(Collection<OSPFProtocolEndpointWrapper> ospfProtocolEndpoints) {
		this.ospfProtocolEndpoints = ospfProtocolEndpoints;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ospfProtocolEndpoints == null) ? 0 : ospfProtocolEndpoints.hashCode());
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
		OSPFAreaWrapper other = (OSPFAreaWrapper) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ospfProtocolEndpoints == null) {
			if (other.ospfProtocolEndpoints != null)
				return false;
		} else if (!ospfProtocolEndpoints.equals(other.ospfProtocolEndpoints))
			return false;
		return true;
	}

}
