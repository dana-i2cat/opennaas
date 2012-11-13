package org.opennaas.extensions.capability.macbridge.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a VLAN Configuration, with a name and a vlanId
 * 
 * @author eduardgrasa
 * 
 */
@XmlRootElement
public class VLANConfiguration {

	/**
	 * A name describing the VLAN
	 */
	private String	name	= null;

	/**
	 * The VLAN ID
	 */
	private int		vlanID	= 0;

	public VLANConfiguration() {
	}

	public VLANConfiguration(String name, int vlanID) {
		this.name = name;
		this.vlanID = vlanID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVlanID() {
		return vlanID;
	}

	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
	}

}
