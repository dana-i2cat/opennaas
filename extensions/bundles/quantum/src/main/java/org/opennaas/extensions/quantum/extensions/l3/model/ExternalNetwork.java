package org.opennaas.extensions.quantum.extensions.l3.model;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
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
