package org.opennaas.extensions.capability.macbridge.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a static VLAN Registration Entry in the Filtering database 
 * as specified by standard IEEE 802.1q
 * @author eduardgrasa
 *
 */
public class StaticVLANRegistrationEntry {
	
	/**
	 * The VLAN id
	 */
	private int vlanID = 0;
	
	/**
	 * A Port Map, consisting of a control element for each outbound Port
	 */
	private List<PortConfiguration> portConfigurations = null;
	
	public StaticVLANRegistrationEntry(){
		this.portConfigurations = new ArrayList<PortConfiguration>();
	}
	
	public List<PortConfiguration> getPortConfigurations(){
		return this.portConfigurations;
	}
	
	public int getVlanID() {
		return vlanID;
	}

	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
	}
}
