package org.opennaas.extensions.quantum.extensions.l3.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "external_network")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalNetwork {

	private String	network_id;

	public String getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(String network_id) {
		this.network_id = network_id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalNetwork other = (ExternalNetwork) obj;
		if (!network_id.equals(other.getNetwork_id()))
			return false;
		return true;
	}
}
