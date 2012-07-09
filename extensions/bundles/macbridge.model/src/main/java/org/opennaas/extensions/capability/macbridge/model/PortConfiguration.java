package org.opennaas.extensions.capability.macbridge.model;

public class PortConfiguration {
	
	public enum RegistrationType {REGISTRATION_FIXED, REGISTRATION_FORBIDDEN, NORMAL_REGISTRATION}; 
	
	/**
	 * The number of the port to whom this configuration applies
	 */
	private String portInterfaceId = null;
	
	/**
	 * The Registrar Administrative Control values for the GVRP protocol (Clause 11) for the VLAN. 
	 * In addition to providing control over the operation of GVRP, these values can also directly 
	 * affect the forwarding behavior of the Bridge, as described in 8.8.9.
	 */
	private RegistrationType registrationType = RegistrationType.NORMAL_REGISTRATION;
	
	/**
	 * Whether frames are to be VLAN-tagged or untagged when transmitted. The entries in the Port 
	 * Map that specify untagged transmission compose the untagged set for the VLAN. The 
	 * untagged set is empty if no Static VLAN Registration Entry exists for the VLAN.
	 */
	private boolean tagged = false;
	
	public PortConfiguration(){
	}
	
	public PortConfiguration(String portInterfaceId, boolean tagged){
		this.portInterfaceId = portInterfaceId;
		this.tagged = tagged;
	}

	public String getPortInterfaceId() {
		return portInterfaceId;
	}

	public void setPortInterfaceId(String portInterfaceId) {
		this.portInterfaceId = portInterfaceId;
	}

	public RegistrationType getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(RegistrationType registrationType) {
		this.registrationType = registrationType;
	}

	public boolean isTagged() {
		return tagged;
	}

	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
}
