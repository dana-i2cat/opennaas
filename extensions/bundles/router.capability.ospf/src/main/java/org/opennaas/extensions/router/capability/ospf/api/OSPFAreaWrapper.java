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

}
